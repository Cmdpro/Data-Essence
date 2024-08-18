package com.cmdpro.datanessence.multiblock;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public abstract class MultiblockPredicateSerializer<T extends MultiblockPredicate> {
    public abstract T fromNetwork(FriendlyByteBuf buf);
    public abstract void toNetwork(FriendlyByteBuf buf, T predicate);
    public abstract T fromJson(JsonObject obj);
}
