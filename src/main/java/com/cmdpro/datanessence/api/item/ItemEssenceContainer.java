package com.cmdpro.datanessence.api.item;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A container that can store multiple kinds of Essence.
 */
public class ItemEssenceContainer {
    protected final Map<ResourceLocation, Float> storedEssence = new Object2FloatArrayMap<>(); // current Essence stored here
    protected final float totalEssence; // maximum capacity for all types
    public static final MapCodec<ItemEssenceContainer> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).fieldOf("essence").forGetter((obj) -> obj.storedEssence),
            Codec.FLOAT.fieldOf("maxEssence").forGetter((obj) -> obj.totalEssence)
    ).apply(instance, ItemEssenceContainer::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEssenceContainer> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeFloat(pValue.totalEssence);
        pBuffer.writeMap(pValue.storedEssence, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);
    }, (pBuffer) -> {
        float maxEssence = pBuffer.readFloat();
        Map<ResourceLocation, Float> essence = pBuffer.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat);
        return new ItemEssenceContainer(essence, maxEssence);
    });


    // To define a container that can store every type
    public ItemEssenceContainer(float totalEssence) {
        this(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.keySet(), 0f);
    }

    // To define a container that can store only the specified types - to just use one, see SingleEssenceContainer
    public ItemEssenceContainer(Iterable<ResourceLocation> supportedTypes, float totalEssence) {
        this.totalEssence = totalEssence;
        for (ResourceLocation type : supportedTypes) {
            this.storedEssence.put(type, 0f);
        }
    }

    public ItemEssenceContainer(Map<ResourceLocation, Float> types, float totalEssence) {
        this.totalEssence = totalEssence;
        this.storedEssence.putAll(types);
    }
    public static float getEssence(ItemStack stack, ResourceLocation type) {
        return stack.has(DataComponentRegistry.ESSENCE_STORAGE) ? stack.get(DataComponentRegistry.ESSENCE_STORAGE).storedEssence.getOrDefault(type, 0f) : 0;
    }

    public static void addEssence(ItemStack stack, ResourceLocation type, float amount) {
        if (stack.has(DataComponentRegistry.ESSENCE_STORAGE)) {
            ItemEssenceContainer storage = stack.get(DataComponentRegistry.ESSENCE_STORAGE);
            if (storage.storedEssence.containsKey(type)) {
                storage.storedEssence.put(type, Math.clamp(storage.storedEssence.get(type) + amount, 0, storage.totalEssence));
                stack.set(DataComponentRegistry.ESSENCE_STORAGE, storage);
            }
        }
    }

    public static void removeEssence(ItemStack stack, ResourceLocation type, float amount) {
        if (stack.has(DataComponentRegistry.ESSENCE_STORAGE)) {
            ItemEssenceContainer storage = stack.get(DataComponentRegistry.ESSENCE_STORAGE);
            if (storage.storedEssence.containsKey(type)) {
                storage.storedEssence.put(type, Math.clamp(storage.storedEssence.get(type) - amount, 0, storage.totalEssence));
                stack.set(DataComponentRegistry.ESSENCE_STORAGE, storage);
            }
        }
    }

    public static float getMaxEssence(ItemStack stack) {
        return stack.has(DataComponentRegistry.ESSENCE_STORAGE) ? stack.get(DataComponentRegistry.ESSENCE_STORAGE).totalEssence : 0;
    }

    public static Set<ResourceLocation> getSupportedEssenceTypes(ItemStack stack) {
        return stack.has(DataComponentRegistry.ESSENCE_STORAGE) ? stack.get(DataComponentRegistry.ESSENCE_STORAGE).storedEssence.keySet() : Set.of();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemEssenceContainer that)) return false;
        return Float.compare(totalEssence, that.totalEssence) == 0 && Objects.equals(storedEssence, that.storedEssence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storedEssence, totalEssence);
    }
}