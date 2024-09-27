package com.cmdpro.datanessence.api.datatablet;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class PageSerializer<T extends Page> {
    public abstract Codec<T> getCodec();
    public abstract StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
}
