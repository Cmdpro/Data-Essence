package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.block.entity.EssenceBufferBlockEntity;
import com.cmdpro.datanessence.block.entity.FluidBufferBlockEntity;
import com.cmdpro.datanessence.block.entity.ItemBufferBlockEntity;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.moddata.PlayerModData;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.DataBankEntrySyncS2CPacket;
import com.cmdpro.datanessence.networking.packet.PlayerTierSyncS2CPacket;
import com.cmdpro.datanessence.networking.packet.UnlockedEntrySyncS2CPacket;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistry;
import org.joml.Math;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class DataNEssenceUtil {
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
    public static class DataTabletUtil {
        public static void unlockEntry(Player player, ResourceLocation entry) {
            player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                Entry entry2 = Entries.entries.get(entry);
                if (entry2 != null && (entry2.getParentEntry() == null || data.getUnlocked().contains(entry2.getParentEntry().id)) && !data.getUnlocked().contains(entry)) {
                    data.getUnlocked().add(entry);
                    data.unlockEntry(player, entry);
                }
            });
        }
        public static void unlockEntryAndParents(Player player, ResourceLocation entry) {
            player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                Entry entry2 = Entries.entries.get(entry);
                if (entry2 != null && !data.getUnlocked().contains(entry)) {
                    data.getUnlocked().add(entry);
                    data.unlockEntry(player, entry);
                    Entry parent = entry2.getParentEntry();
                    while (parent != null) {
                        if (!data.getUnlocked().contains(parent.id)) {
                            data.getUnlocked().add(parent.id);
                            data.unlockEntry(player, parent.id);
                        }
                        parent = parent.getParentEntry();
                    }
                }
            });
        }
        public static boolean playerHasEntry(Player player, String entry) {
            if (entry != null) {
                return player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).resolve().get().getUnlocked().contains(ResourceLocation.tryParse(entry));
            }
            return false;
        }
        public static int getTier(Player player) {
            return player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).resolve().get().getTier();
        }
        public static void setTier(Player player, int tier) {
            player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                data.setTier(tier);
                data.sendTier(player, true);
            });
        }
    }
    public static BlockState getHiddenBlock(Block block, Player player) {
        if (player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).isPresent()) {
            PlayerModData data = player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).resolve().get();
            for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
                if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                    continue;
                }
                if (i.originalBlock.equals(block)) {
                    if (!data.getUnlocked().contains(i.entry)) {
                        return i.hiddenAs;
                    }
                    break;
                }
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
                container.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent((resolved) -> {
                    buffer.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((resolved2) -> {
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
                                    resolved2.extractItem(o, copy2.getCount()-copy.getCount(), false);
                                    break;
                                }
                            }
                        }
                    });
                });
            } else if (!(ent instanceof EssenceBufferBlockEntity || ent instanceof FluidBufferBlockEntity)) {
                break;
            }
        }
    }
    public static void getFluidsFromBuffersBelow(BlockEntity container) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof FluidBufferBlockEntity buffer) {
                container.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((resolved) -> {
                    buffer.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((resolved2) -> {
                        for (int o = 0; o < resolved2.getTanks(); o++) {
                            FluidStack copy = resolved2.getFluidInTank(o).copy();
                            if (!copy.isEmpty()) {
                                copy.setAmount(Math.clamp(0, 4000, copy.getAmount()));
                                int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                                resolved2.getFluidInTank(o).shrink(filled);
                            }
                        }
                    });
                });
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
        player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(data -> {
            data.updateData((ServerPlayer)player);
        });
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
