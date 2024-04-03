package com.cmdpro.datanessence.commands;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

public class DataNEssenceCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(DataNEssence.MOD_ID)
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("resetlearned")
                        .executes((command) -> {
                            return resetlearned(command);
                        })
                )
        );
    }

    private static int resetlearned(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            Player player = (Player) command.getSource().getEntity();
            player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(data -> {
                data.getUnlocked().clear();
                data.updateData(player);
            });
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
