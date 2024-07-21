package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.BaseCapabilityPoint;
import com.cmdpro.datanessence.api.BaseEssencePoint;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.block.TraversiteRoad;
import com.cmdpro.datanessence.block.entity.StructureProtectorBlockEntity;
import com.cmdpro.datanessence.computers.ComputerTypeManager;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.DragonPartsSyncS2CPacket;
import com.cmdpro.datanessence.networking.packet.EntrySyncS2CPacket;
import com.cmdpro.datanessence.networking.packet.HiddenBlockSyncS2CPacket;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.screen.databank.DataBankEntryManager;
import com.cmdpro.datanessence.screen.databank.DataBankTypeManager;
import com.cmdpro.datanessence.screen.datatablet.DataTabManager;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.EntryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = DataNEssence.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onLivingEntityTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity ent) {
            if (event.getEntity().getBlockStateOn().getBlock() instanceof TraversiteRoad road) {
                if (ent.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(TraversiteRoad.TRAVERSITE_ROAD_SPEED_UUID) == null) {
                    ent.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(TraversiteRoad.TRAVERSITE_ROAD_SPEED_UUID, "Traversite Road Speed", road.boost, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                } else {
                    if (ent.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(TraversiteRoad.TRAVERSITE_ROAD_SPEED_UUID).amount() != road.boost) {
                        ent.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(TraversiteRoad.TRAVERSITE_ROAD_SPEED_UUID);
                        ent.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(TraversiteRoad.TRAVERSITE_ROAD_SPEED_UUID, "Traversite Road Speed", road.boost, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    }
                }
            } else {
                ent.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(TraversiteRoad.TRAVERSITE_ROAD_SPEED_UUID);
            }
        }
    }
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide()) {
            if (!event.getLevel().getBlockState(event.getPos()).is(BlockRegistry.STRUCTURE_PROTECTOR.get())) {
                event.getEntity().getData(AttachmentTypeRegistry.BINDING_STRUCTURE_CONTROLLER).ifPresent((binding) -> {
                    if (binding.bindProcess == 1) {
                        event.getEntity().sendSystemMessage(Component.translatable("block.datanessence.structure_controller.select_pos_2"));
                        binding.offsetCorner1 = event.getPos().offset(binding.getBlockPos().multiply(-1));
                        binding.bindProcess = 2;
                    } else if (binding.bindProcess == 2) {
                        if (!binding.offsetCorner1.offset(binding.getBlockPos()).equals(event.getPos())) {
                            binding.offsetCorner2 = event.getPos().offset(binding.getBlockPos().multiply(-1));
                            binding.bindProcess = 0;
                            event.getEntity().sendSystemMessage(Component.translatable("block.datanessence.structure_controller.finished"));
                            event.getEntity().removeData(AttachmentTypeRegistry.BINDING_STRUCTURE_CONTROLLER);
                        }
                    }
                });
            }
        }
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        boolean creative = event.getPlayer().isCreative();
        if (!creative) {
            if (!event.getLevel().getBlockState(event.getPos()).is(BlockRegistry.STRUCTURE_PROTECTOR.get())) {
                if (!event.getLevel().isClientSide()) {
                    if (((Level) event.getLevel()).hasData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS)) {
                        List<StructureProtectorBlockEntity> ents = ((Level) event.getLevel()).getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS);
                        for (StructureProtectorBlockEntity i : ents) {
                            AABB aabb = AABB.encapsulatingFullBlocks(i.offsetCorner1.offset(i.getBlockPos()), i.offsetCorner2.offset(i.getBlockPos()));
                            if (aabb.contains(event.getPos().getCenter())) {
                                event.setCanceled(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        boolean creative = false;
        if (event.getEntity() instanceof Player player) {
            if (player.isCreative()) {
                creative = true;
            }
        }
        if (!creative) {
            if (!event.getLevel().isClientSide()) {
                if (((Level)event.getLevel()).hasData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS)) {
                    List<StructureProtectorBlockEntity> ents = ((Level) event.getLevel()).getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS);
                    for (StructureProtectorBlockEntity i : ents) {
                        AABB aabb = AABB.encapsulatingFullBlocks(i.offsetCorner1.offset(i.getBlockPos()), i.offsetCorner2.offset(i.getBlockPos()));
                        if (aabb.contains(event.getPos().getCenter())) {
                            event.setCanceled(true);
                            break;
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (!event.getLevel().isClientSide()) {
            if (((Level)event.getLevel()).hasData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS)) {
                List<StructureProtectorBlockEntity> ents = ((Level) event.getLevel()).getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS);
                List<BlockPos> toRemove = new ArrayList<>();
                for (BlockPos i : event.getExplosion().getToBlow()) {
                    for (StructureProtectorBlockEntity o : ents) {
                        AABB aabb = AABB.encapsulatingFullBlocks(o.offsetCorner1.offset(o.getBlockPos()), o.offsetCorner2.offset(o.getBlockPos()));
                        if (aabb.contains(i.getCenter())) {
                            toRemove.add(i);
                            break;
                        }
                    }
                }
                event.getExplosion().getToBlow().removeAll(toRemove);
            }
        }
    }
    @SubscribeEvent
    public static void onBlockDestroy(LivingDestroyBlockEvent event) {
        boolean creative = false;
        if (event.getEntity() instanceof Player player) {
            if (player.isCreative()) {
                creative = true;
            }
        }
        if (!creative) {
            if (!event.getEntity().level().isClientSide()) {
                if (((Level)event.getEntity().level()).hasData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS)) {
                    List<StructureProtectorBlockEntity> ents = ((Level) event.getEntity().level()).getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS);
                    for (StructureProtectorBlockEntity i : ents) {
                        AABB aabb = AABB.encapsulatingFullBlocks(i.offsetCorner1.offset(i.getBlockPos()), i.offsetCorner2.offset(i.getBlockPos()));
                        if (aabb.contains(event.getPos().getCenter())) {
                            event.setCanceled(true);
                            break;
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void playerStartTracking(PlayerEvent.StartTracking evt) {
        Entity target = evt.getTarget();
        Player player = evt.getEntity();

        if (player instanceof ServerPlayer serverPlayer && target instanceof Player targetPlayer) {
            ModMessages.sendToPlayer(new DragonPartsSyncS2CPacket(target.getId(), targetPlayer.getData(AttachmentTypeRegistry.HAS_HORNS), targetPlayer.getData(AttachmentTypeRegistry.HAS_TAIL), targetPlayer.getData(AttachmentTypeRegistry.HAS_WINGS)), serverPlayer);
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide) {
            Optional<BlockEntity> ent = event.getEntity().getData(AttachmentTypeRegistry.LINK_FROM);
            if (ent.isPresent()) {
                if (ent.get().getBlockPos().getCenter().distanceTo(event.getEntity().getBoundingBox().getCenter()) > 20) {
                    event.getEntity().setData(AttachmentTypeRegistry.LINK_FROM, Optional.empty());
                    DataNEssenceUtil.PlayerDataUtil.updateData((ServerPlayer)event.getEntity());
                } else {
                    if (ent.get().getBlockState().getBlock() instanceof BaseEssencePoint block) {
                        if (!event.getEntity().isHolding(block.getRequiredWire())) {
                            event.getEntity().setData(AttachmentTypeRegistry.LINK_FROM, Optional.empty());
                            DataNEssenceUtil.PlayerDataUtil.updateData((ServerPlayer) event.getEntity());
                        }
                    }
                    if (ent.get().getBlockState().getBlock() instanceof BaseCapabilityPoint block) {
                        if (!event.getEntity().isHolding(block.getRequiredWire())) {
                            event.getEntity().setData(AttachmentTypeRegistry.LINK_FROM, Optional.empty());
                            DataNEssenceUtil.PlayerDataUtil.updateData((ServerPlayer) event.getEntity());
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(EntryManager.getOrCreateInstance());
        event.addListener(DataTabManager.getOrCreateInstance());
        event.addListener(DataBankEntryManager.getOrCreateInstance());
        event.addListener(DataBankTypeManager.getOrCreateInstance());
        event.addListener(HiddenBlocksManager.getOrCreateInstance());
        event.addListener(ComputerTypeManager.getOrCreateInstance());
    }
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                syncToPlayer(player);
                ModMessages.sendToPlayersTrackingEntityAndSelf(new DragonPartsSyncS2CPacket(player.getId(), player.getData(AttachmentTypeRegistry.HAS_HORNS), player.getData(AttachmentTypeRegistry.HAS_TAIL), player.getData(AttachmentTypeRegistry.HAS_WINGS)), player);
            }
        } else {
            syncToPlayer(event.getPlayer());
            ModMessages.sendToPlayer(new DragonPartsSyncS2CPacket(event.getPlayer().getId(), event.getPlayer().getData(AttachmentTypeRegistry.HAS_HORNS), event.getPlayer().getData(AttachmentTypeRegistry.HAS_TAIL), event.getPlayer().getData(AttachmentTypeRegistry.HAS_WINGS)), event.getPlayer());
        }
    }
    protected static void syncToPlayer(ServerPlayer player) {
        ModMessages.sendToPlayer(new EntrySyncS2CPacket(Entries.entries, Entries.tabs), player);
        ModMessages.sendToPlayer(new HiddenBlockSyncS2CPacket(HiddenBlocksManager.blocks), player);
    }
    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide()) {
            if(event.getEntity() instanceof ServerPlayer player) {
                ModMessages.sendToPlayer(new DragonPartsSyncS2CPacket(player.getId(), player.getData(AttachmentTypeRegistry.HAS_HORNS), player.getData(AttachmentTypeRegistry.HAS_TAIL), player.getData(AttachmentTypeRegistry.HAS_WINGS)), player);
                DataNEssenceUtil.PlayerDataUtil.updateData(player);
                DataNEssenceUtil.PlayerDataUtil.updateUnlockedEntries(player);
                DataNEssenceUtil.PlayerDataUtil.sendTier(player, false);
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        List<ResourceLocation> entries = new ArrayList<>();
        for (ResourceLocation i : event.getEntity().getData(AttachmentTypeRegistry.INCOMPLETE)) {
            Entry entry = Entries.entries.get(i);
            if (entry.completionAdvancement.equals(event.getAdvancement().id())) {
                entries.add(i);
            }
        }
        for (ResourceLocation i : entries) {
            DataNEssenceUtil.DataTabletUtil.unlockEntry(event.getEntity(), i, false);
        }
    }
}
