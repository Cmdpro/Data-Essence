package com.cmdpro.datanessence;

import com.cmdpro.datanessence.block.BaseEssencePoint;
import com.cmdpro.datanessence.moddata.PlayerModData;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.EntrySyncS2CPacket;
import com.cmdpro.datanessence.screen.datatablet.ClientEntries;
import com.cmdpro.datanessence.screen.datatablet.EntryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DataNEssence.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerModDataProvider.PLAYER_MODDATA).isPresent()) {
                event.addCapability(new ResourceLocation(DataNEssence.MOD_ID, "properties"), new PlayerModDataProvider());
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            event.player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                if (data.getLinkFrom() != null) {
                    if (data.getLinkFrom().getBlockPos().getCenter().distanceTo(event.player.getBoundingBox().getCenter()) > 20) {
                        data.setLinkFrom(null);
                        data.updateData(event.player);
                    } else if (data.getLinkFrom().getBlockState().getBlock() instanceof BaseEssencePoint block) {
                        if (!event.player.isHolding(block.getRequiredWire())) {
                            data.setLinkFrom(null);
                            data.updateData(event.player);
                        }
                    }
                }
            });
        }
    }
    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(EntryManager.getOrCreateInstance());
    }
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                syncToPlayer(player);
            }
        } else {
            syncToPlayer(event.getPlayer());
        }
    }
    protected static void syncToPlayer(ServerPlayer player) {
        ModMessages.sendToPlayer(new EntrySyncS2CPacket(ClientEntries.entries), player);
    }
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        if(event.isWasDeath()) {
            oldPlayer.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerModData.class);
    }
    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide()) {
            if(event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(data -> {
                    data.updateData((ServerPlayer)event.getEntity());
                });
            }
        }
    }
}
