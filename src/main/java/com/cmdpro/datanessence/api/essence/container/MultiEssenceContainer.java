package com.cmdpro.datanessence.api.essence.container;

import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A container that can store multiple kinds of Essence.
 */
public class MultiEssenceContainer implements EssenceStorage {
    protected final Map<EssenceType, Float> storedEssence = new Object2FloatArrayMap<>(); // current Essence stored here
    protected final float totalEssence; // maximum capacity for all types

    // To define a container that can store every type
    public MultiEssenceContainer(float totalEssence) {
        this(List.of(EssenceType.essences), 0f);
    }

    // To define a container that can store only the specified types - to just use one, see SingleEssenceContainer
    public MultiEssenceContainer(Iterable<EssenceType> supportedTypes, float totalEssence) {
        this.totalEssence = totalEssence;
        for (EssenceType type : supportedTypes) {
            this.storedEssence.put(type, 0f);
        }
    }

    public MultiEssenceContainer(Map<EssenceType, Float> types, float totalEssence) {
        this.totalEssence = totalEssence;
        this.storedEssence.putAll(types);
    }

    @Override
    public float getEssence(EssenceType type) {
        return this.storedEssence.getOrDefault(type, 0f);
    }

    @Override
    public void addEssence(EssenceType type, float amount) {
        float currentAmount = this.storedEssence.getOrDefault(type, 0f);
        this.storedEssence.put(type, currentAmount + amount);
    }

    @Override
    public void removeEssence(EssenceType type, float amount) {
        float currentAmount = this.storedEssence.getOrDefault(type, 0f);
        this.storedEssence.put(type, currentAmount - amount);
    }

    @Override
    public float getMaxEssence() {
        return totalEssence;
    }

    public Set<EssenceType> getSupportedEssenceTypes() {
        return this.storedEssence.keySet();
    }
}
