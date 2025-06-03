package com.cmdpro.datanessence.moddata;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientPlayerUnlockedEntries {
    private static List<ResourceLocation> unlocked = new ArrayList<>();
    private static HashMap<ResourceLocation, Integer> incomplete = new HashMap<>();
    public static void set(List<ResourceLocation> unlocked, HashMap<ResourceLocation, Integer> incomplete) {
        ClientPlayerUnlockedEntries.unlocked = unlocked;
        ClientPlayerUnlockedEntries.incomplete = incomplete;
    }
    public static List<ResourceLocation> getUnlocked() {
        return unlocked;
    }
    public static HashMap<ResourceLocation, Integer> getIncomplete() {
        return incomplete;
    }
}
