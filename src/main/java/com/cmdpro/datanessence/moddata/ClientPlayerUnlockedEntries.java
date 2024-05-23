package com.cmdpro.datanessence.moddata;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientPlayerUnlockedEntries {
    private static List<ResourceLocation> unlocked = new ArrayList<>();
    public static void set(List<ResourceLocation> unlocked) {
        ClientPlayerUnlockedEntries.unlocked = unlocked;
    }
    public static List<ResourceLocation> getUnlocked() {
        return unlocked;
    }
}
