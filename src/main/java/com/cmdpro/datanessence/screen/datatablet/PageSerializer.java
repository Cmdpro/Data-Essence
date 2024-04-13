package com.cmdpro.datanessence.screen.datatablet;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public abstract class PageSerializer<T extends Page> {
    public abstract T fromJson(JsonObject json);
    public abstract T fromNetwork(FriendlyByteBuf buf);
    public abstract void toNetwork(T page, FriendlyByteBuf buf);
}
