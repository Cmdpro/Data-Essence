package com.cmdpro.datanessence.commands;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.api.util.PlayerDataUtil;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.DragonPartsSync;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;

public class DataNEssenceCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(DataNEssence.MOD_ID)
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("entry")
                        .then(Commands.argument("target", EntityArgument.player())
                        // /datanessence entry <id || all> <true/false> [true/false]
                        // <resource location of entry OR all> <whether to grant or revoke it> [incomplete status; must default to false]
                        .then(Commands.literal("reset_all")
                                .executes((command) -> {
                                    return resetlearned(command);
                                })
                        )
                        .then(Commands.literal("unlock_all")
                                .executes(command -> {
                                    return unlockall(command);
                                })
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests((stack, builder) -> {
                                            return SharedSuggestionProvider.suggest(Entries.entries.keySet().stream().map(ResourceLocation::toString), builder);
                                        })
                                        .then(Commands.argument("set", BoolArgumentType.bool())
                                                .executes((command -> {
                                                    return modifyEntries(command, false);
                                                }))
                                                .then(Commands.argument("incomplete", BoolArgumentType.bool())
                                                    .executes((command -> {
                                                    return modifyEntries(command, true);
                                                }))))))
                        )
                )
                .then(Commands.literal("set_tier")
                        .then(Commands.argument("tier", IntegerArgumentType.integer(0))
                                .executes((command) -> {
                                    return settier(command);
                                }))

                )
                .then(Commands.literal("check_entry_overlaps")
                        .executes(command -> {
                            return checkoverlaps(command);
                        })
                )
                .then(Commands.literal("give_dragon_part")
                        .then(Commands.argument("part", StringArgumentType.string()).suggests((stack, builder) -> {
                            return SharedSuggestionProvider.suggest(new String[] { "horns", "tail", "wings", "all" }, builder);
                        }).executes(command -> {
                            return givedragonpart(command);
                        }))
                )
                .then(Commands.literal("maximize")
                        .executes(command -> {
                            return maximize(command);
                        }))
        );
    }
    private static int checkoverlaps(CommandContext<CommandSourceStack> command) {
        boolean foundIssues = false;
        for (Entry i : Entries.entries.values()) {
            for (Entry o : Entries.entries.values()) {
                if(i != o && i.x == o.x && i.y == o.y && i.tab.equals(o.tab)) {
                    foundIssues = true;
                    if (command.getSource().isPlayer()) {
                        command.getSource().getEntity().sendSystemMessage(Component.translatable("commands.datanessence.check_entry_overlaps.found", i.id.toString(), o.id.toString()));
                    } else {
                        DataNEssence.LOGGER.warn("Entry \"" + i.id.toString() + "\" is overlapping with entry \"" + o.id.toString() + "\"");
                    }
                }
            }
        }
        if (!foundIssues) {
            command.getSource().getEntity().sendSystemMessage(Component.translatable("commands.datanessence.check_entry_overlaps.no_found"));
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int givedragonpart(CommandContext<CommandSourceStack> command) {
        if(command.getSource().getEntity() instanceof Player) {
            Player player;
            try {
                player = command.getArgument("target", EntitySelector.class).findSinglePlayer(command.getSource());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }

            String part = command.getArgument("part", String.class);
            if (part.equals("horns")) {
                player.setData(AttachmentTypeRegistry.HAS_HORNS, true);
            } else if (part.equals("tail")) {
                player.setData(AttachmentTypeRegistry.HAS_TAIL, true);
            } else if (part.equals("wings")) {
                player.setData(AttachmentTypeRegistry.HAS_WINGS, true);
            } else if (part.equals("all")) {
                player.setData(AttachmentTypeRegistry.HAS_HORNS, true);
                player.setData(AttachmentTypeRegistry.HAS_TAIL, true);
                player.setData(AttachmentTypeRegistry.HAS_WINGS, true);
            }
            ModMessages.sendToPlayersTrackingEntityAndSelf(new DragonPartsSync(player.getId(), player.getData(AttachmentTypeRegistry.HAS_HORNS), player.getData(AttachmentTypeRegistry.HAS_TAIL), player.getData(AttachmentTypeRegistry.HAS_WINGS)), (ServerPlayer)player);
            command.getSource().sendSuccess(() -> {
                return part.equals("all") ? Component.translatable("commands.datanessence.give_all_dragon_parts", player.getName()) : Component.translatable("commands.datanessence.give_dragon_part", part, player.getName());
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int settier(CommandContext<CommandSourceStack> command) {
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            int tier = command.getArgument("tier", int.class);
            DataTabletUtil.setTier(player, tier);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.set_tier", tier, player.getName());
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int resetlearned(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player;
            try {
                player = command.getArgument("target", EntitySelector.class).findSinglePlayer(command.getSource());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }

            List<ResourceLocation> unlockedEntries = player.getData(AttachmentTypeRegistry.UNLOCKED);
            unlockedEntries.clear();
            HashMap<ResourceLocation, Integer> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE_STAGES);
            incompleteEntries.clear();
            HashMap<ResourceLocation, Boolean> unlockedEssences = player.getData(AttachmentTypeRegistry.UNLOCKED_ESSENCES);
            unlockedEssences.clear();
            PlayerDataUtil.updateUnlockedEntries((ServerPlayer)player);
            PlayerDataUtil.updateData((ServerPlayer)player);
            player.setData(AttachmentTypeRegistry.TIER, 0);
            PlayerDataUtil.sendTier((ServerPlayer)player, false);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.reset_learned", player.getName());
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }


    private static int modifyEntries(CommandContext<CommandSourceStack> command, boolean hasAllArgs){
        if(command.getSource().getEntity() instanceof Player) {
            Player player;
            try {
                player = command.getArgument("target", EntitySelector.class).findSinglePlayer(command.getSource());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            ResourceLocation entry = command.getArgument("id", ResourceLocation.class);
            if (Entries.entries.containsKey(entry)) {
                boolean set = command.getArgument("set", Boolean.class);
                Entry entry2 = Entries.entries.get(entry);
                int completionStage = (hasAllArgs) ? command.getArgument("completion_stage", Integer.class) : entry2.completionStages.size();
                if (entry2.completionStages.size() < completionStage) {
                    command.getSource().sendFailure(Component.translatable("commands.datanessence.entry.entry_not_enough_stages", entry.toString(), completionStage));
                    return 0;
                }
                List<ResourceLocation> unlockedEntries = player.getData(AttachmentTypeRegistry.UNLOCKED);
                if (set) {
                    if (!unlockedEntries.contains(entry)) {
                        DataTabletUtil.unlockEntryAndParents(player, entry, completionStage);
                        command.getSource().sendSuccess(() -> {
                            return Component.translatable("commands.datanessence.entry.unlock_entry", entry.toString(), player.getName());
                        }, true);
                    } else {
                        command.getSource().sendFailure(Component.translatable("commands.datanessence.entry.entry_already_unlocked", entry.toString(), player.getName()));
                        return 0;
                    }
                } else {
                    if (unlockedEntries.contains(entry)) {
                        DataTabletUtil.removeEntry(player, entry);
                        command.getSource().sendSuccess(() -> {
                            return Component.translatable("commands.datanessence.entry.remove_entry", entry.toString(), player.getName());
                        }, true);
                    } else {
                        command.getSource().sendFailure(Component.translatable("commands.datanessence.entry.entry_not_learned_in_the_first_place", entry.toString(), player.getName()));
                        return 0;
                    }
                }
            } else {
                command.getSource().sendFailure(Component.translatable("commands.datanessence.entry.entry_doesnt_exist", entry.toString()));
                return 0;
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int unlockall(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            for (Entry i : Entries.entries.values()) {
                DataTabletUtil.unlockEntryAndParents(player, i.id, i.completionStages.size());
            }
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.unlock_all", player.getName());
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int maximize(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();

            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.maximize", player.getName());
            }, true);

            DataTabletUtil.setTier(player, 8);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.set_tier", 8, player.getName());
            }, true);

            for (Entry entry : Entries.entries.values()) {
                DataTabletUtil.unlockEntryAndParents(player, entry.id, entry.completionStages.size());
            }
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.unlock_all", player.getName());
            }, true);

            player.setData(AttachmentTypeRegistry.HAS_HORNS, true);
            player.setData(AttachmentTypeRegistry.HAS_TAIL, true);
            player.setData(AttachmentTypeRegistry.HAS_WINGS, true);
            ModMessages.sendToPlayersTrackingEntityAndSelf(new DragonPartsSync(player.getId(), player.getData(AttachmentTypeRegistry.HAS_HORNS), player.getData(AttachmentTypeRegistry.HAS_TAIL), player.getData(AttachmentTypeRegistry.HAS_WINGS)), (ServerPlayer)player);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.give_all_dragon_parts", player.getName());
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
