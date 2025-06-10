package com.cmdpro.datanessence.api.essence.container;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container that contains infinite of multiple kinds of Essence.
 */
public class InfiniteEssenceContainer implements EssenceStorage {
    protected final Map<EssenceType, Float> storedEssence = new Object2FloatArrayMap<>(); // current Essence stored here
    protected final float totalEssence; // maximum capacity for all types

    /**
     * Transforms the container data into a NBT compound.
     * @return NBT compound of the storage
     */
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        ListTag tag2 = new ListTag();
        for (Map.Entry<EssenceType, Float> i : storedEssence.entrySet()) {
            CompoundTag tag3 = new CompoundTag();
            tag3.putString("Type", DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(i.getKey()).toString());
            tag3.putFloat("Amount", i.getValue());
            tag2.add(tag3);
        }
        tag.put("Essence", tag2);
        return tag;
    }

    /**
     * Creates a container from the given NBT compound.
     * @param tag a compound containing EssenceType (int), stored amount (float), and total capacity (float)
     * @return a new SingleEssenceContainer with the given data
     */
    public void fromNbt(@NotNull CompoundTag tag) {
        Map<EssenceType, Float> types = new HashMap<>();
        for (Tag i : (ListTag)tag.get("Essence")) {
            CompoundTag tag2 = (CompoundTag)i;
            types.put(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(ResourceLocation.tryParse(tag2.getString("Type"))), tag2.getFloat("Amount"));
        }
        this.storedEssence.clear();
        this.storedEssence.putAll(types);
    }

    public InfiniteEssenceContainer(float totalEssence) {
        this(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.stream().toList(), totalEssence);
    }

    public InfiniteEssenceContainer(Iterable<EssenceType> supportedTypes, float totalEssence) {
        this.totalEssence = totalEssence;
        for (EssenceType type : supportedTypes) {
            this.storedEssence.put(type, totalEssence);
        }
    }

    public InfiniteEssenceContainer(Map<EssenceType, Float> types, float totalEssence) {
        this.totalEssence = totalEssence;
        this.storedEssence.putAll(types);
    }

    @Override
    public float getEssence(EssenceType type) {
        return this.storedEssence.getOrDefault(type, getMaxEssence());
    }

    @Override
    public void addEssence(EssenceType type, float amount) {
        if (this.storedEssence.containsKey(type)) {
            this.storedEssence.put(type, getMaxEssence());
        }
    }

    @Override
    public void removeEssence(EssenceType type, float amount) {
        if (this.storedEssence.containsKey(type)) {
            this.storedEssence.put(type, getMaxEssence());
        }
    }

    @Override
    public float getMaxEssence() {
        return totalEssence;
    }

    @Override
    public EssenceStorage getStorage() {
        return this;
    }

    @Override
    public Set<EssenceType> getSupportedEssenceTypes() {
        return this.storedEssence.keySet();
    }
}
