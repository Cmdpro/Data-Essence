package com.cmdpro.datanessence.screen.databank;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public abstract class MinigameCreator<T extends Minigame> {
    public abstract T createMinigame();
    public abstract MinigameSerializer getSerializer();
}
