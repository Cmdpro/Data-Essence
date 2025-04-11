package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DataComponentRegistry {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(DataNEssence.MOD_ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> DATA_ID = DATA_COMPONENTS.registerComponentType("data_id", builder -> builder
            .persistent(ResourceLocation.CODEC)
            .networkSynchronized(ResourceLocation.STREAM_CODEC)
            .cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DATA_INCOMPLETE = DATA_COMPONENTS.registerComponentType("data_incomplete", builder -> builder
            .persistent(Codec.BOOL)
            .networkSynchronized(ByteBufCodecs.BOOL)
            .cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> MOLD = DATA_COMPONENTS.registerComponentType("mold", builder -> builder
            .persistent(ResourceLocation.CODEC)
            .networkSynchronized(ResourceLocation.STREAM_CODEC)
            .cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEssenceContainer>> ESSENCE_STORAGE = DATA_COMPONENTS.registerComponentType("essence_storage", builder -> builder
            .persistent(ItemEssenceContainer.CODEC.codec())
            .networkSynchronized(ItemEssenceContainer.STREAM_CODEC)
            .cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<SoundEvent>>> PLAYING_MUSIC = DATA_COMPONENTS.registerComponentType("playing_sound", builder -> builder
            .persistent(ResourceKey.codec(Registries.SOUND_EVENT))
            .networkSynchronized(ResourceKey.streamCodec(Registries.SOUND_EVENT))
            .cacheEncoding()
    );
}