package com.cmdpro.datanessence.moddata;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ClientPlayerData {
    private static boolean[] unlockedEssences = new boolean[] { false, false, false, false };
    public static void set(boolean[] unlockedEssences, BlockPos linkPos) {
        ClientPlayerData.unlockedEssences = unlockedEssences;
        ClientPlayerData.linkPos = linkPos;
    }
    public static boolean[] getUnlockedEssences() {
        return unlockedEssences;
    }
    public static BlockPos getLinkPos() {
        return linkPos;
    }
    public static BlockPos linkPos;
}
