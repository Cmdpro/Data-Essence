package com.cmdpro.datanessence.moddata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerModDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerModData> PLAYER_MODDATA = CapabilityManager.get(new CapabilityToken<PlayerModData>() { });
    private final LazyOptional<PlayerModData> optional = LazyOptional.of(this::createPlayerData);
    private PlayerModData modData = null;
    private PlayerModData createPlayerData() {
        if (this.modData == null) {
            this.modData = new PlayerModData();
        }
        return this.modData;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_MODDATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerData().loadNBTData(nbt);
    }
}
