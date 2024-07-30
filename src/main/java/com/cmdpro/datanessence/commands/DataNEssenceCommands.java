package com.cmdpro.datanessence.commands;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.DragonPartsSyncS2CPacket;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DataNEssenceCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(DataNEssence.MOD_ID)
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("reset_learned")
                        .executes((command) -> {
                            return resetlearned(command);
                        })
                )
                .then(Commands.literal("set_tier")
                        .then(Commands.argument("tier", IntegerArgumentType.integer(0))
                                .executes((command) -> {
                                    return settier(command);
                                }))

                )
                .then(Commands.literal("unlock")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .suggests((stack, builder) -> {
                                    return SharedSuggestionProvider.suggest(Entries.entries.keySet().stream().map(ResourceLocation::toString), builder);
                                })
                                .then(Commands.argument("incomplete", BoolArgumentType.bool())
                                .executes((command -> {
                                    return unlock(command);
                                }))))
                )
                .then(Commands.literal("check_entry_overlaps")
                        .executes(command -> {
                            return checkoverlaps(command);
                        })
                )
                .then(Commands.literal("give_dragon_part")
                        .then(Commands.argument("part", StringArgumentType.string()).suggests((stack, builder) -> {
                            return SharedSuggestionProvider.suggest(new String[] { "horns", "tail", "wings" }, builder);
                        }).executes(command -> {
                            return givedragonpart(command);
                        }))
                )
                .then(Commands.literal("unlock_all")
                        .executes(command -> {
                            return unlockall(command);
                        })
                )
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
            Player player = (Player) command.getSource().getEntity();
            String part = command.getArgument("part", String.class);
            if (part.equals("horns")) {
                player.setData(AttachmentTypeRegistry.HAS_HORNS, true);
            } else if (part.equals("tail")) {
                player.setData(AttachmentTypeRegistry.HAS_TAIL, true);
            } else if (part.equals("wings")) {
                player.setData(AttachmentTypeRegistry.HAS_WINGS, true);
            }
            ModMessages.sendToPlayersTrackingEntityAndSelf(new DragonPartsSyncS2CPacket(player.getId(), player.getData(AttachmentTypeRegistry.HAS_HORNS), player.getData(AttachmentTypeRegistry.HAS_TAIL), player.getData(AttachmentTypeRegistry.HAS_WINGS)), (ServerPlayer)player);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.give_dragon_part", part);
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int settier(CommandContext<CommandSourceStack> command) {
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            int tier = command.getArgument("tier", int.class);
            DataNEssenceUtil.DataTabletUtil.setTier(player, tier);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.set_tier", tier);
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int resetlearned(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            List<ResourceLocation> unlockedEntries = player.getData(AttachmentTypeRegistry.UNLOCKED);
            unlockedEntries.clear();
            List<ResourceLocation> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE);
            incompleteEntries.clear();
            DataNEssenceUtil.PlayerDataUtil.updateUnlockedEntries((ServerPlayer)player);
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.reset_learned");
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int unlock(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            ResourceLocation entry = command.getArgument("id", ResourceLocation.class);
            boolean incomplete = command.getArgument("incomplete", Boolean.class);
            if (Entries.entries.containsKey(entry)) {
                Entry entry2 = Entries.entries.get(entry);
                if (!entry2.incomplete && incomplete) {
                    command.getSource().sendFailure(Component.translatable("commands.datanessence.unlock.entry_has_no_incomplete", entry.toString()));
                    return 0;
                }
                List<ResourceLocation> unlockedEntries = player.getData(AttachmentTypeRegistry.UNLOCKED);
                if (!unlockedEntries.contains(entry)) {
                    DataNEssenceUtil.DataTabletUtil.unlockEntryAndParents(player, entry, incomplete);
                    command.getSource().sendSuccess(() -> {
                        return Component.translatable("commands.datanessence.unlock.unlock_entry", entry.toString());
                    }, true);
                } else {
                    command.getSource().sendFailure(Component.translatable("commands.datanessence.unlock.entry_already_unlocked", entry.toString()));
                    return 0;
                }
            } else {
                command.getSource().sendFailure(Component.translatable("commands.datanessence.unlock.entry_doesnt_exist", entry.toString()));
                return 0;
            }
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int unlockall(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            for (Entry i : Entries.entries.values()) {
                DataNEssenceUtil.DataTabletUtil.unlockEntry(player, i.id, false);
            }
            command.getSource().sendSuccess(() -> {
                return Component.translatable("commands.datanessence.unlock_all");
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
