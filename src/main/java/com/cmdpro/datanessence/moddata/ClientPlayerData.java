package com.cmdpro.datanessence.moddata;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

public class ClientPlayerData {
    private static boolean[] unlockedEssences = new boolean[] { false, false, false, false };
    public static void set(boolean[] unlockedEssences, BlockPos linkPos, Color linkColor) {
        ClientPlayerData.unlockedEssences = unlockedEssences;
        ClientPlayerData.linkPos = linkPos;
        ClientPlayerData.linkColor = linkColor;
    }
    public static void setTier(int tier) {
        ClientPlayerData.tier = tier;
    }
    public static boolean[] getUnlockedEssences() {
        return unlockedEssences;
    }
    public static BlockPos getLinkPos() {
        return linkPos;
    }
    private static BlockPos linkPos;
    public static Color getLinkColor() {
        return linkColor;
    }
    private static Color linkColor;
    public static int getTier() {
        return tier;
    }
    private static int tier;
}
