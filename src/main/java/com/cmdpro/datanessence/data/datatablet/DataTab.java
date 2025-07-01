package com.cmdpro.datanessence.data.datatablet;

import com.cmdpro.datanessence.DataNEssence;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DataTab {

    public DataTab(ResourceLocation id, ItemLike icon, Component name, boolean alwaysShown, DataTabPlacement placement) {
        this.id = id;
        this.icon = new ItemStack(icon);
        this.name = name;
        this.alwaysShown = alwaysShown;
        this.placement = placement;
    }
    public DataTab(ResourceLocation id, ItemStack icon, Component name, boolean alwaysShown, DataTabPlacement placement) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.alwaysShown = alwaysShown;
        this.placement = placement;
    }
    public Component name;
    public ItemStack icon;
    public ResourceLocation id;
    public boolean alwaysShown;
    public DataTabPlacement placement;
    public record DataTabPlacement(ResourceLocation placeBefore, ResourceLocation placeAfter) {
        public static final ResourceLocation IGNORED = DataNEssence.locate("ignored");
        public static final ResourceLocation ALL = DataNEssence.locate("all");
        public static final StreamCodec<RegistryFriendlyByteBuf, DataTabPlacement> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeResourceLocation(pValue.placeBefore);
            pBuffer.writeResourceLocation(pValue.placeAfter);
        }, (pBuffer) -> {
            ResourceLocation placeBefore = pBuffer.readResourceLocation();
            ResourceLocation placeAfter = pBuffer.readResourceLocation();
            return new DataTabPlacement(placeBefore, placeAfter);
        });
        public static final MapCodec<DataTabPlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("place_before", IGNORED).forGetter((entry) -> entry.placeBefore),
                ResourceLocation.CODEC.optionalFieldOf("place_after", IGNORED).forGetter((entry) -> entry.placeAfter)
        ).apply(instance, DataTabPlacement::new));
    }
}
