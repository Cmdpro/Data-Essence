package com.cmdpro.datanessence.moddata;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientPlayerUnlockedEntries {
    private static List<ResourceLocation> unlocked = new ArrayList<>();
    private static List<ResourceLocation> incomplete = new ArrayList<>();
    public static void set(List<ResourceLocation> unlocked, List<ResourceLocation> incomplete) {
        ClientPlayerUnlockedEntries.unlocked = unlocked;
        ClientPlayerUnlockedEntries.incomplete = incomplete;
    }
    public static List<ResourceLocation> getUnlocked() {
        return unlocked;
    }
    public static List<ResourceLocation> getIncomplete() {
        return incomplete;
    }
}
