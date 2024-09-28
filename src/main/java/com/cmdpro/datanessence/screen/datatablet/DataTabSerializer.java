package com.cmdpro.datanessence.screen.datatablet;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.pages.CraftingPage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataTabSerializer {
    public DataTab read(ResourceLocation entryId, JsonObject json) {
        DataTab entry = CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow();
        entry.id = entryId;
        return entry;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, DataTab> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(pValue.id);
        ItemStack.STREAM_CODEC.encode(pBuffer, pValue.icon);
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.name);
        pBuffer.writeInt(pValue.priority);
    }, (pBuffer) -> {
        ResourceLocation id = pBuffer.readResourceLocation();
        ItemStack icon = ItemStack.STREAM_CODEC.decode(pBuffer);
        Component name = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        int priority = pBuffer.readInt();
        DataTab entry = new DataTab(id, icon, name, priority);
        return entry;
    });
    public static final MapCodec<DataTab> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ItemStack.CODEC.fieldOf("icon").forGetter((entry) -> entry.icon),
            ComponentSerialization.CODEC.fieldOf("name").forGetter((entry) -> entry.name),
            Codec.INT.fieldOf("priority").forGetter((entry) -> entry.priority)
    ).apply(instance, (icon, name, priority) -> new DataTab(null, icon, name, priority)));
}