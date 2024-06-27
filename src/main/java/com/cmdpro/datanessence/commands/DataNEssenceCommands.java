package com.cmdpro.datanessence.commands;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.PlayerModData;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

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
                                .executes((command -> {
                                    return unlock(command);
                                })))
                )
                .then(Commands.literal("check_entry_overlaps")
                        .executes(command -> {
                            return checkoverlaps(command);
                        })
                )
        );
    }
    private static int checkoverlaps(CommandContext<CommandSourceStack> command) {
        boolean foundIssues = false;
        for (Entry i : Entries.entries.values()) {
            for (Entry o : Entries.entries.values()) {
                if(i != o && i.x == o.x && i.y == o.y) {
                    foundIssues = true;
                    if (command.getSource().isPlayer()) {
                        command.getSource().getEntity().sendSystemMessage(Component.translatable("commands.datanessence.check_entry_overlaps.found", i.id, o.id));
                    } else {
                        DataNEssence.LOGGER.warn("Entry \"" + i.id + "\" is overlapping with entry \"" + o.id + "\"");
                    }
                }
            }
        }
        if (!foundIssues) {
            command.getSource().getEntity().sendSystemMessage(Component.translatable("commands.datanessence.check_entry_overlaps.no_found"));
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
            player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(data -> {
                data.getUnlocked().clear();
                data.updateUnlockedEntries(player);
                command.getSource().sendSuccess(() -> {
                    return Component.translatable("commands.datanessence.reset_learned");
                }, true);
            });
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int unlock(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            ResourceLocation entry = command.getArgument("id", ResourceLocation.class);
            if (Entries.entries.containsKey(entry)) {
                player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                    if (!data.getUnlocked().contains(entry)) {
                        DataNEssenceUtil.DataTabletUtil.unlockEntryAndParents(player, entry);
                        command.getSource().sendSuccess(() -> {
                            return Component.translatable("commands.datanessence.unlock.unlock_entry", entry);
                        }, true);
                    } else {
                        throw new CommandRuntimeException(Component.translatable("commands.datanessence.unlock.entry_already_unlocked", entry));
                    }
                });
            } else {
                throw new CommandRuntimeException(Component.translatable("commands.datanessence.unlock.entry_doesnt_exist", entry));
            }
        }
        return Command.SINGLE_SUCCESS;
    }/*
    private static int learnall(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(data -> {
                data.setBookDiscoverProcess(2);
                data.getUnlocked().clear();
                for (Map.Entry<ResourceLocation, BookCategory> i : BookDataManager.get().getBook(new ResourceLocation(DataNEssence.MOD_ID, "datanessenceguide")).getCategories().entrySet()) {
                    ArrayList<ResourceLocation> list = new ArrayList<>();
                    for (Map.Entry<ResourceLocation, BookEntry> o : i.getValue().getEntries().entrySet()) {
                        if (DataNEssenceUtil.getAnalyzeCondition(o.getValue().getCondition()) != null) {
                            list.add(o.getKey());
                        }
                    }
                    data.getUnlocked().put(i.getKey(), list);
                }
                BookUnlockStateManager.get().updateAndSyncFor((ServerPlayer)player);
            });
        }
        return Command.SINGLE_SUCCESS;
    }*/
}
