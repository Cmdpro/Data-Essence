package com.cmdpro.datanessence.screen.databank;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public abstract class MinigameCreator<T extends Minigame> {
    public abstract T fromJson(JsonObject json);
    public abstract T fromNetwork(FriendlyByteBuf buf);
    public abstract void toNetwork(T page, FriendlyByteBuf buf);
}
