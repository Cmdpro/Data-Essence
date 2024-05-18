package com.cmdpro.datanessence.moddata;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientPlayerData {
    private static boolean[] unlockedEssences = new boolean[] { false, false, false, false };
    public static void set(boolean[] unlockedEssences) {
        ClientPlayerData.unlockedEssences = unlockedEssences;
    }
    public static boolean[] getUnlockedEssences() {
        return unlockedEssences;
    }
}
