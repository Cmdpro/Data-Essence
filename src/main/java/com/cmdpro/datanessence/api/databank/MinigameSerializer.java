package com.cmdpro.datanessence.api.databank;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class MinigameSerializer<T extends MinigameCreator> {
    public abstract Codec<T> getCodec();
    public abstract StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
}
