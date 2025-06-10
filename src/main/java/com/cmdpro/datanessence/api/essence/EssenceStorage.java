package com.cmdpro.datanessence.api.essence;

import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.List;
import java.util.Set;

/**
 * Implement this to define something that can store Essence.
 */
public interface EssenceStorage {

    /**
     * Transfers Essence of the given type between source and destination containers.
     * @param source the EssenceStorage to take from
     * @param destination the EssenceStorage to put into
     * @param type the EssenceType to transfer
     * @param amount the amount to transfer
     * @return the actual amount of Essence transferred
     */
    static float transferEssence(@NotNull EssenceStorage source, @NotNull EssenceStorage destination, @NotNull EssenceType type, float amount) {
        if (source.getEssence(type) > 0 && destination.getMaxEssence() > 0) {
            float transferred = Math.clamp(0, destination.getMaxEssence()-destination.getEssence(type), Math.min(source.getEssence(type), amount));
            source.removeEssence(type, transferred);
            destination.addEssence(type, transferred);
            return transferred;
        }
        return 0;
    }

    /**
     * Gets the amount of stored Essence of the given type.
     * @param type the Essence type to query
     * @return stored amount
     */
    default float getEssence(EssenceType type) {
        return getStorage().getEssence(type);
    }

    /**
     * Adds Essence of the given type.
     *
     * @param type   the Essence type to operate on
     * @param amount the amount of that type to add
     */
    default void addEssence(EssenceType type, float amount) {
        getStorage().addEssence(type, amount);
    }

    /**
     * Removes Essence of the given type.
     * @param type the Essence type to operate on
     * @param amount the amount of that type to remove
     */
    default void removeEssence(EssenceType type, float amount) {
        getStorage().removeEssence(type, amount);
    }

    /**
     * Gets the total maximum capacity.
     * @return total capacity
     */
    default float getMaxEssence() {
        return getStorage().getMaxEssence();
    }

    /**
     * Gets an instance of the storage.
     * @return storage
     */
    EssenceStorage getStorage();

    Set<EssenceType> getSupportedEssenceTypes();
}
