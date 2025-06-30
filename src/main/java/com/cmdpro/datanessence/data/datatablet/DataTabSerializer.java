package com.cmdpro.datanessence.data.datatablet;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.Optional;

public class DataTabSerializer {
    public DataTab read(ResourceLocation entryId, JsonObject json) {
        DataTab entry = ICondition.getWithWithConditionsCodec(CODEC, JsonOps.INSTANCE, json).orElse(null);
        if (entry != null) {
            entry.id = entryId;
            return entry;
        } else {
            return null;
        }
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, DataTab> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(pValue.id);
        ItemStack.STREAM_CODEC.encode(pBuffer, pValue.icon);
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.name);
        DataTab.DataTabPlacement.STREAM_CODEC.encode(pBuffer, pValue.placement);
    }, (pBuffer) -> {
        ResourceLocation id = pBuffer.readResourceLocation();
        ItemStack icon = ItemStack.STREAM_CODEC.decode(pBuffer);
        Component name = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        DataTab.DataTabPlacement placement = DataTab.DataTabPlacement.STREAM_CODEC.decode(pBuffer);
        DataTab entry = new DataTab(id, icon, name, placement);
        return entry;
    });
    public static final MapCodec<DataTab> ORIGINAL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ItemStack.CODEC.fieldOf("icon").forGetter((entry) -> entry.icon),
            ComponentSerialization.CODEC.fieldOf("name").forGetter((entry) -> entry.name),
            DataTab.DataTabPlacement.CODEC.fieldOf("placement").forGetter((entry) -> entry.placement)
    ).apply(instance, (icon, name, placement) -> new DataTab(null, icon, name, placement)));
    public static final Codec<Optional<WithConditions<DataTab>>> CODEC = ConditionalOps.createConditionalCodecWithConditions(ORIGINAL_CODEC.codec());
}