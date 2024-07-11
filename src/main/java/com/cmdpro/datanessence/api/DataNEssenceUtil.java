package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.block.entity.EssenceBufferBlockEntity;
import com.cmdpro.datanessence.block.entity.FluidBufferBlockEntity;
import com.cmdpro.datanessence.block.entity.ItemBufferBlockEntity;
import com.cmdpro.datanessence.computers.ComputerData;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.*;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Math;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataNEssenceUtil {
    public static class PlayerDataUtil {
        public static void updateData(ServerPlayer player) {
            Color linkColor = new Color(0, 0, 0, 0);
            Optional<BlockEntity> linkFromEntity = player.getData(AttachmentTypeRegistry.LINK_FROM);
            BlockPos linkFrom = null;
            if (linkFromEntity.isPresent()) {
                linkFrom = linkFromEntity.get().getBlockPos();
                if (linkFromEntity.get() instanceof BaseEssencePointBlockEntity linkFrom2) {
                    linkColor = linkFrom2.linkColor();
                } else if (linkFromEntity.get() instanceof BaseCapabilityPointBlockEntity linkFrom2) {
                    linkColor = linkFrom2.linkColor();
                }
            }
            ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(getUnlockedEssences(player), linkFrom, linkColor), (player));
        }
        public static void updateUnlockedEntries(ServerPlayer player) {
            ModMessages.sendToPlayer(new UnlockedEntrySyncS2CPacket(player.getData(AttachmentTypeRegistry.UNLOCKED)), (player));
        }
        public static void unlockEntry(ServerPlayer player, ResourceLocation entry) {
            ModMessages.sendToPlayer(new UnlockEntryS2CPacket(entry), (player));
        }
        public static void sendTier(ServerPlayer player, boolean showIndicator) {
            ModMessages.sendToPlayer(new PlayerTierSyncS2CPacket(player.getData(AttachmentTypeRegistry.TIER), showIndicator), player);
        }
        public static boolean[] getUnlockedEssences(ServerPlayer player) {
            return new boolean[] {
                    player.getData(AttachmentTypeRegistry.UNLOCKED_ESSENCE),
                    player.getData(AttachmentTypeRegistry.UNLOCKED_LUNAR_ESSENCE),
                    player.getData(AttachmentTypeRegistry.UNLOCKED_NATURAL_ESSENCE),
                    player.getData(AttachmentTypeRegistry.UNLOCKED_EXOTIC_ESSENCE)
            };
        }
    }
    public static class DataBankUtil {
        public static void sendDataBankEntries(ServerPlayer player, ResourceLocation[] ids) {
            Map<ResourceLocation, DataBankEntry> entries = new HashMap<>();
            for (ResourceLocation i : ids) {
                entries.put(i, DataBankEntries.entries.get(i));
            }
            ModMessages.sendToPlayer(new DataBankEntrySyncS2CPacket(entries), (player));
        }
        public static void sendDataBankEntries(Player player, ResourceLocation[] ids) {
            sendDataBankEntries((ServerPlayer)player, ids);
        }
    }
    public static class ComputerUtil {
        public static void openComputer(ServerPlayer player, ComputerData data) {
            ModMessages.sendToPlayer(new ComputerDataSyncS2CPacket(data), (player));
        }
        public static void openComputer(Player player, ComputerData data) {
            openComputer((ServerPlayer)player, data);
        }
    }
    public static class DataTabletUtil {
        public static void unlockEntry(Player player, ResourceLocation entry) {
            Entry entry2 = Entries.entries.get(entry);
            List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
            if (entry2 != null && entry2.isUnlockedServer(player) && !unlocked.contains(entry)) {
                unlocked.add(entry);
                PlayerDataUtil.unlockEntry((ServerPlayer)player, entry);
            }
        }
        public static void unlockEntryAndParents(Player player, ResourceLocation entry) {
            List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
            Entry entry2 = Entries.entries.get(entry);
            if (entry2 != null && !unlocked.contains(entry)) {
                unlocked.add(entry);
                PlayerDataUtil.unlockEntry((ServerPlayer)player, entry);
                for (Entry i : entry2.getParentEntries()) {
                    unlockEntryAndParents(player, i.id);
                }
            }
        }
        public static boolean playerHasEntry(Player player, ResourceLocation entry) {
            if (entry != null) {
                return player.getData(AttachmentTypeRegistry.UNLOCKED).contains(entry);
            }
            return false;
        }
        public static int getTier(Player player) {
            return player.getData(AttachmentTypeRegistry.TIER);
        }
        public static void setTier(Player player, int tier) {
            player.setData(AttachmentTypeRegistry.TIER, tier);
            PlayerDataUtil.sendTier((ServerPlayer)player, true);
        }
    }
    public static BlockState getHiddenBlock(Block block, Player player) {
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                if (!unlocked.contains(i.entry)) {
                    return i.hiddenAs;
                }
                break;
            }
        }
        return null;
    }
    public static void getEssenceFromBuffersBelow(EssenceContainer container) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof EssenceBufferBlockEntity buffer) {
                transferEssence(buffer, container, buffer.getMaxEssence());
                transferLunarEssence(buffer, container, buffer.getMaxLunarEssence());
                transferNaturalEssence(buffer, container, buffer.getMaxNaturalEssence());
                transferExoticEssence(buffer, container, buffer.getMaxExoticEssence());
            } else if (!(ent instanceof ItemBufferBlockEntity || ent instanceof FluidBufferBlockEntity)) {
                break;
            }
        }
    }
    public static void getItemsFromBuffersBelow(BlockEntity container) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof ItemBufferBlockEntity buffer) {
                IItemHandler resolved = container.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, container.getBlockPos(), Direction.DOWN);
                IItemHandler resolved2 = container.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, buffer.getBlockPos(), null);
                boolean movedAnything = false;
                for (int o = 0; o < resolved2.getSlots(); o++) {
                    ItemStack copy = resolved2.getStackInSlot(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setCount(Math.clamp(0, 16, copy.getCount()));
                        ItemStack copy2 = copy.copy();
                        int p = 0;
                        while (p < resolved.getSlots()) {
                            ItemStack copyCopy = copy.copy();
                            int remove = resolved.insertItem(p, copyCopy, false).getCount();
                            if (remove < copyCopy.getCount()) {
                                movedAnything = true;
                            }
                            copy.setCount(remove);
                            if (remove <= 0) {
                                break;
                            }
                            p++;
                        }
                        if (movedAnything) {
                            resolved2.extractItem(o, copy2.getCount() - copy.getCount(), false);
                            break;
                        }
                    }
                }
            } else if (!(ent instanceof EssenceBufferBlockEntity || ent instanceof FluidBufferBlockEntity)) {
                break;
            }
        }
    }
    public static void getFluidsFromBuffersBelow(BlockEntity container) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof FluidBufferBlockEntity buffer) {
                IFluidHandler resolved = container.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, container.getBlockPos(), Direction.DOWN);
                IFluidHandler resolved2 = container.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, buffer.getBlockPos(), null);
                for (int o = 0; o < resolved2.getTanks(); o++) {
                    FluidStack copy = resolved2.getFluidInTank(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setAmount(Math.clamp(0, 4000, copy.getAmount()));
                        int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        resolved2.getFluidInTank(o).shrink(filled);
                    }
                }
            } else if (!(ent instanceof EssenceBufferBlockEntity || ent instanceof ItemBufferBlockEntity)) {
                break;
            }
        }
    }
    public static void drawLine(ParticleOptions particle, Vec3 point1, Vec3 point2, Level level, double space) {
        double distance = point1.distanceTo(point2);
        Vec3 vector = point2.subtract(point1).normalize().multiply(space, space, space);
        double length = 0;
        for (Vec3 point = point1; length < distance; point = point.add(vector)) {
            ((ServerLevel)level).sendParticles(particle, point.x, point.y, point.z, 1, 0, 0, 0, 0);
            length += space;
        }
    }
    public static void updatePlayerData(Player player) {
        PlayerDataUtil.updateData((ServerPlayer)player);
    }
    public static void transferEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxEssence() > 0 && to.getMaxEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxEssence()-to.getEssence(), Math.min(from.getEssence(), amount));
            from.setEssence(from.getEssence()-trueAmount);
            to.setEssence(to.getEssence()+trueAmount);
        }
    }
    public static void transferLunarEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxLunarEssence() > 0 && to.getMaxLunarEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxLunarEssence()-to.getLunarEssence(), Math.min(from.getLunarEssence(), amount));
            from.setLunarEssence(from.getLunarEssence()-trueAmount);
            to.setLunarEssence(to.getLunarEssence()+trueAmount);
        }
    }
    public static void transferNaturalEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxNaturalEssence() > 0 && to.getMaxNaturalEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxNaturalEssence()-to.getNaturalEssence(), Math.min(from.getNaturalEssence(), amount));
            from.setNaturalEssence(from.getNaturalEssence()-trueAmount);
            to.setNaturalEssence(to.getNaturalEssence()+trueAmount);
        }
    }
    public static void transferExoticEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxExoticEssence() > 0 && to.getMaxExoticEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxExoticEssence()-to.getExoticEssence(), Math.min(from.getExoticEssence(), amount));
            from.setExoticEssence(from.getExoticEssence()-trueAmount);
            to.setExoticEssence(to.getExoticEssence()+trueAmount);
        }
    }
}
