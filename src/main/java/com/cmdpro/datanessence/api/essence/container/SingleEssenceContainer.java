package com.cmdpro.datanessence.api.essence.container;

import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * A container that can store a single type of Essence.
 */
public class SingleEssenceContainer implements EssenceStorage {
    protected final EssenceType storedType; // the type of Essence this contains
    protected float storedEssence; // the amount of that Essence this currently has
    protected final float totalStorage; // the maximum capacity of the container

    /**
     * A container that can store a single type of Essence.
     * @param storedType the type of Essence this contains
     * @param totalStorage the maximum capacity of the container
     */
    public SingleEssenceContainer(EssenceType storedType, float totalStorage) {
        this.storedType = storedType;
        this.storedEssence = 0f;
        this.totalStorage = totalStorage;
    }

    /**
     * A container that can store a single type of Essence.
     * @param storedType the type of Essence this contains
     * @param totalStorage the maximum capacity of the container
     * @param amount the amount of that Essence this currently has
     */
    public SingleEssenceContainer(EssenceType storedType, float totalStorage, float amount) {
        this.storedType = storedType;
        this.storedEssence = amount;
        this.totalStorage = totalStorage;
    }

    @Override
    public float getEssence(EssenceType type) {
        if (type == storedType) {
            return storedEssence;
        }
        return 0f;
    }

    @Override
    public void addEssence(EssenceType type, float amount) {
        if (type == storedType) {
            this.storedEssence += amount; // TODO clamp this
        }
    }

    @Override
    public void removeEssence(EssenceType type, float amount) {
        if (type == storedType) {
            this.storedEssence -= amount; // TODO also clamp this
        }
    }

    @Override
    public float getMaxEssence() {
        return totalStorage;
    }

    /**
     * Transforms the container data into a NBT compound.
     * @return NBT compound of the storage
     */
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Amount", this.storedEssence);
        tag.putInt("Type", this.storedType.ordinal());
        tag.putFloat("Capacity", this.totalStorage);
        return tag;
    }

    /**
     * Creates a container from the given NBT compound.
     * @param tag a compound containing EssenceType (int), stored amount (float), and total capacity (float)
     * @return a new SingleEssenceContainer with the given data
     */
    public SingleEssenceContainer fromNbt(@NotNull CompoundTag tag) {
        EssenceType storedType = EssenceType.essences[tag.getInt("Type")];
        float storedAmount = tag.getFloat("Amount");
        float totalCapacity = tag.getFloat("Capacity");
        return new SingleEssenceContainer(storedType, totalCapacity, storedAmount);
    }
}
