package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.databank.DataBankEntries;
import com.cmdpro.datanessence.databank.DataBankEntry;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.datatablet.Entries;
import com.cmdpro.datanessence.datatablet.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataTabletUtil {
    public static void unlockEntry(Player player, ResourceLocation entry, boolean incomplete) {
        Entry entry2 = Entries.entries.get(entry);
        List<ResourceLocation> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE);
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        boolean finalIncomplete = false;
        if (incomplete) {
            if (!((ServerPlayer) player).getAdvancements().getOrStartProgress(player.getServer().getAdvancements().get(entry2.completionAdvancement)).isDone()) {
                unlocked = incompleteEntries;
                finalIncomplete = true;
            }
        }
        if (entry2 != null && entry2.isUnlockedServer(player) && !unlocked.contains(entry)) {
            if (!finalIncomplete) {
                incompleteEntries.remove(entry);
            }
            unlocked.add(entry);
            PlayerDataUtil.unlockEntry((ServerPlayer) player, entry, finalIncomplete);
            checkForTierUpgrades(player);
        }
    }

    public static void unlockEntryAndParents(Player player, ResourceLocation entry, boolean incomplete) {
        Entry entry2 = Entries.entries.get(entry);
        List<ResourceLocation> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE);
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        boolean finalIncomplete = false;
        if (incomplete) {
            if (!((ServerPlayer) player).getAdvancements().getOrStartProgress(player.getServer().getAdvancements().get(entry2.completionAdvancement)).isDone()) {
                unlocked = incompleteEntries;
                finalIncomplete = true;
            }
        }
        if (entry2 != null && !unlocked.contains(entry)) {
            if (!finalIncomplete) {
                incompleteEntries.remove(entry);
            }
            unlocked.add(entry);
            PlayerDataUtil.unlockEntry((ServerPlayer) player, entry, finalIncomplete);
            for (Entry i : entry2.getParentEntries()) {
                DataTabletUtil.unlockEntryAndParents(player, i.id, i.incomplete);
            }
        }
        checkForTierUpgrades(player);
    }

    public static boolean playerHasEntry(Player player, ResourceLocation entry, boolean allowIncomplete) {
        if (entry != null) {
            if (player.getData(AttachmentTypeRegistry.UNLOCKED).contains(entry)) {
                return true;
            } else return allowIncomplete && player.getData(AttachmentTypeRegistry.INCOMPLETE).contains(entry);
        }
        return false;
    }
    public static boolean playerHasEntry(Player player, ResourceLocation entry) {
        return playerHasEntry(player, entry, false);
    }

    public static int getTier(Player player) {
        return player.getData(AttachmentTypeRegistry.TIER);
    }

    public static void setTier(Player player, int tier) {
        player.setData(AttachmentTypeRegistry.TIER, tier);
        PlayerDataUtil.sendTier((ServerPlayer) player, true);
        List<EssenceType> essenceTypes = DataTabletUtil.getUnlockedTypesForTier(tier);
        boolean anyUnlocked = false;
        for (EssenceType i : essenceTypes) {
            if (!player.getData(AttachmentTypeRegistry.UNLOCKED_ESSENCES).getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(i), false)) {
                player.getData(AttachmentTypeRegistry.UNLOCKED_ESSENCES).put(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(i), true);
                anyUnlocked = true;
            }
        }
        if (anyUnlocked) {
            PlayerDataUtil.updateData((ServerPlayer) player);
        }
    }
    public static int getMaxTier() {
        int tier = 0;
        for (var i : DataBankEntries.entries.values()) {
            if (i.tier > tier) {
                tier = i.tier;
            }
        }
        return tier;
    }
    public static void checkForTierUpgrades(Player player) {
        int tier = getMaxTier();
        var unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        for (var i : Entries.entries.values()) {
            if (!unlocked.contains(i.id) && i.critical) {
                Optional<DataBankEntry> entry = DataBankEntries.entries.values().stream().filter((a) -> a.entry.equals(i.id)).findFirst();
                if (entry.isPresent()) {
                    if (tier > entry.get().tier) {
                        tier = entry.get().tier;
                    }
                }
            }
        }
        if (getTier(player) != tier) {
            setTier(player, tier);
        }
    }
    public static List<EssenceType> getUnlockedTypesForTier(int tier) {
        List<EssenceType> essenceTypes = new ArrayList<>();
        for (EssenceType i : DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.stream().toList()) {
            if (tier >= i.tier) {
                essenceTypes.add(i);
            }
        }
        return essenceTypes;
    }
}
