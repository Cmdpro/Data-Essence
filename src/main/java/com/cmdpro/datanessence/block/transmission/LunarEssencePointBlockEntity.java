package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.misc.ICustomEssencePointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LunarEssencePointBlockEntity extends BaseEssencePointBlockEntity {
    public LunarEssencePointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNAR_ESSENCE_POINT.get(), pos, state);
        storage = new SingleEssenceContainer(EssenceTypeRegistry.LUNAR_ESSENCE.get(), Float.MAX_VALUE);
    }
    @Override
    public Color linkColor() {
        return new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().getColor());
    }
    @Override
    public void deposit(BlockEntity otherEntity, EssenceStorage other) {
        if (otherEntity instanceof ICustomEssencePointBehaviour behaviour) {
            if (!behaviour.canInsertEssence(other, EssenceTypeRegistry.LUNAR_ESSENCE.get(), getFinalSpeed(DataNEssenceConfig.essencePointTransfer))) {
                return;
            }
        }
        EssenceStorage.transferEssence(storage, other, EssenceTypeRegistry.LUNAR_ESSENCE.get(), getFinalSpeed(DataNEssenceConfig.essencePointTransfer));
    }

    @Override
    public void transfer(BlockEntity otherEntity, EssenceStorage other) {
        if (other.getEssence(EssenceTypeRegistry.LUNAR_ESSENCE.get()) >= getFinalSpeed(DataNEssenceConfig.essencePointTransfer)) {
            return;
        }
        deposit(otherEntity, other);
    }
    @Override
    public void take(BlockEntity otherEntity, EssenceStorage other) {
        if (storage.getEssence(EssenceTypeRegistry.LUNAR_ESSENCE.get()) >= getFinalSpeed(DataNEssenceConfig.essencePointTransfer)) {
            return;
        }
        if (otherEntity instanceof ICustomEssencePointBehaviour behaviour) {
            if (!behaviour.canExtractEssence(other, EssenceTypeRegistry.LUNAR_ESSENCE.get(), getFinalSpeed(DataNEssenceConfig.essencePointTransfer))) {
                return;
            }
        }
        EssenceStorage.transferEssence(other, storage, EssenceTypeRegistry.LUNAR_ESSENCE.get(), getFinalSpeed(DataNEssenceConfig.essencePointTransfer));
    }
}
