package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.data.databank.DataBankEntries;
import com.cmdpro.datanessence.data.databank.DataBankEntry;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DataTabletUtil {
    public static void unlockEntry(Player player, ResourceLocation entry, int incompleteStage) {
        Entry entry2 = Entries.entries.get(entry);
        HashMap<ResourceLocation, Integer> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE_STAGES);
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        int finalIncompleteStage = incompleteStage;
        if (entry2.isIncomplete(incompleteStage)) {
            int stage = Math.max(incompleteStage, entry2.getIncompleteStageServer(player));
            while (true) {
                if (stage >= entry2.completionStages.size()) {
                    break;
                }
                List<ResourceLocation> advancements = entry2.completionStages.get(stage).completionAdvancements;
                boolean isUnlocked = true;
                for (ResourceLocation i : advancements) {
                    if (!((ServerPlayer) player).getAdvancements().getOrStartProgress(player.getServer().getAdvancements().get(i)).isDone()) {
                        isUnlocked = false;
                        break;
                    }
                }
                if (isUnlocked) {
                    stage++;
                } else {
                    break;
                }
            }
            finalIncompleteStage = stage;
        }
        if (entry2 != null && entry2.isUnlockedServer(player) && !unlocked.contains(entry)) {
            if (finalIncompleteStage < entry2.completionStages.size()) {
                incompleteEntries.put(entry, finalIncompleteStage);
            } else {
                incompleteEntries.remove(entry);
                unlocked.add(entry);
            }
            PlayerDataUtil.unlockEntry((ServerPlayer) player, entry, finalIncompleteStage);
            checkForTierUpgrades(player);
        }
    }

    public static void removeEntry(Player player, ResourceLocation id) {
        Entry entryToRemove = Entries.entries.get(id);
        List<ResourceLocation> unlockedEntries = player.getData(AttachmentTypeRegistry.UNLOCKED);

        if (entryToRemove != null && entryToRemove.isUnlockedServer(player) && unlockedEntries.contains(id)) {
            unlockedEntries.remove(id);
            PlayerDataUtil.updateUnlockedEntries((ServerPlayer)player);
            checkForTierUpgrades(player);
        }
    }

    public static void unlockEntryAndParents(Player player, ResourceLocation entry, int incompleteStage) {
        Entry entry2 = Entries.entries.get(entry);
        HashMap<ResourceLocation, Integer> incompleteEntries = player.getData(AttachmentTypeRegistry.INCOMPLETE_STAGES);
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        int finalIncompleteStage = incompleteStage;
        if (entry2.isIncomplete(incompleteStage)) {
            int stage = Math.max(incompleteStage, entry2.getIncompleteStageServer(player));
            while (true) {
                if (stage >= entry2.completionStages.size()) {
                    break;
                }
                List<ResourceLocation> advancements = entry2.completionStages.get(stage).completionAdvancements;
                boolean isUnlocked = true;
                for (ResourceLocation i : advancements) {
                    if (!((ServerPlayer) player).getAdvancements().getOrStartProgress(player.getServer().getAdvancements().get(i)).isDone()) {
                        isUnlocked = false;
                        break;
                    }
                }
                if (isUnlocked) {
                    stage++;
                } else {
                    break;
                }
            }
            finalIncompleteStage = stage;
        }
        if (entry2 != null && entry2.isUnlockedServer(player) && !unlocked.contains(entry)) {
            if (finalIncompleteStage < entry2.completionStages.size()) {
                incompleteEntries.put(entry, finalIncompleteStage);
            } else {
                incompleteEntries.remove(entry);
                unlocked.add(entry);
            }
            PlayerDataUtil.unlockEntry((ServerPlayer) player, entry, finalIncompleteStage);
            for (Entry i : entry2.getParentEntries()) {
                DataTabletUtil.unlockEntryAndParents(player, i.id, i.completionStages.size());
            }
        }
        checkForTierUpgrades(player);
    }

    public static boolean playerHasEntry(Player player, ResourceLocation entry, int completionStage) {
        if (entry != null) {
            if (player.getData(AttachmentTypeRegistry.UNLOCKED).contains(entry)) {
                return true;
            } else if (completionStage != -1) {
                HashMap<ResourceLocation, Integer> incomplete = player.getData(AttachmentTypeRegistry.INCOMPLETE_STAGES);
                if (incomplete.containsKey(entry)) {
                    return incomplete.get(entry) >= completionStage;
                }
            }
        }
        return false;
    }
    public static boolean playerHasEntry(Player player, ResourceLocation entry, boolean allowIncomplete) {
        return playerHasEntry(player, entry, allowIncomplete ? 0 : -1);
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
