package com.cmdpro.datanessence.moddata;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientPlayerData {
    private static float currentChunkInstability;
    public static void set(float currentChunkInstability) {
        ClientPlayerData.currentChunkInstability = currentChunkInstability;
    }
    public static float getPlayerChunkInstability() {
        return currentChunkInstability;
    }
}
