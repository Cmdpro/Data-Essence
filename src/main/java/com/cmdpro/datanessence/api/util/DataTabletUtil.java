package com.cmdpro.datanessence.api.util;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class DataTabletUtil {
    public static void unlockEntry(Player player, ResourceLocation entry, boolean incomplete) {
        Entry entry2 = Entries.entries.get(entry);
        List<ResourceLocation> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE);
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        if (incomplete) {
            if (!((ServerPlayer) player).getAdvancements().getOrStartProgress(player.getServer().getAdvancements().get(entry2.completionAdvancement)).isDone()) {
                unlocked = incompleteEntries;
            } else {
                incompleteEntries.remove(entry);
            }
        }
        if (entry2 != null && entry2.isUnlockedServer(player) && !unlocked.contains(entry)) {
            if (!incomplete) {
                incompleteEntries.remove(entry);
            }
            unlocked.add(entry);
            PlayerDataUtil.unlockEntry((ServerPlayer) player, entry, incomplete);
        }
    }

    public static void unlockEntryAndParents(Player player, ResourceLocation entry, boolean incomplete) {
        Entry entry2 = Entries.entries.get(entry);
        List<ResourceLocation> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE);
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        if (incomplete) {
            if (!((ServerPlayer) player).getAdvancements().getOrStartProgress(player.getServer().getAdvancements().get(entry2.completionAdvancement)).isDone()) {
                unlocked = incompleteEntries;
            } else {
                incompleteEntries.remove(entry);
            }
        }
        if (entry2 != null && !unlocked.contains(entry)) {
            if (!incomplete) {
                incompleteEntries.remove(entry);
            }
            unlocked.add(entry);
            PlayerDataUtil.unlockEntry((ServerPlayer) player, entry, incomplete);
            for (Entry i : entry2.getParentEntries()) {
                DataTabletUtil.unlockEntryAndParents(player, i.id, i.incomplete);
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
        PlayerDataUtil.sendTier((ServerPlayer) player, true);
        EssenceType essenceType = DataTabletUtil.getUnlockedTypeForTier(tier);
        if (essenceType != null) {
            if (!player.getData(AttachmentTypeRegistry.UNLOCKED_ESSENCES).getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(essenceType), false)) {
                player.getData(AttachmentTypeRegistry.UNLOCKED_ESSENCES).put(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(essenceType), true);
                PlayerDataUtil.updateData((ServerPlayer) player);
            }
        }
    }

    @Nullable
    private static EssenceType getUnlockedTypeForTier(int tier) {
        EssenceType essenceType = null;
        if (tier >= 1) {
            essenceType = EssenceTypeRegistry.ESSENCE.get();
        }
        if (tier >= 3) {
            essenceType = EssenceTypeRegistry.LUNAR_ESSENCE.get();
        }
        if (tier >= 5) {
            essenceType = EssenceTypeRegistry.NATURAL_ESSENCE.get();
        }
        if (tier >= 7) {
            essenceType = EssenceTypeRegistry.EXOTIC_ESSENCE.get();
        }
        return essenceType;
    }
}
