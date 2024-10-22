package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.misc.ICustomEssencePointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;

import java.awt.*;
import java.util.List;

public class LunarEssencePointBlockEntity extends BaseEssencePointBlockEntity {
    public LunarEssencePointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNAR_ESSENCE_POINT.get(), pos, state);
    }
    @Override
    public Color linkColor() {
        return new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().getColor());
    }
    @Override
    public void transfer(BaseEssencePointBlockEntity from, List<BaseEssencePointBlockEntity> other) {
        int transferAmount = (int) Math.floor((float)getFinalSpeed(DataNEssenceConfig.essencePointTransfer)/(float)other.size());
        for (BaseEssencePointBlockEntity i : other) {
            BlockEntity fromEnt = from.getLevel().getBlockEntity(from.getBlockPos().relative(from.getDirection().getOpposite()));
            BlockEntity toEnt = i.getLevel().getBlockEntity(i.getBlockPos().relative(i.getDirection().getOpposite()));
            if (fromEnt instanceof EssenceBlockEntity fromStorage && toEnt instanceof EssenceBlockEntity toStorage) {
                if (toEnt instanceof ICustomEssencePointBehaviour behaviour) {
                    if (!behaviour.canInsertEssence(toStorage.getStorage(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), transferAmount)) {
                        return;
                    }
                }
                EssenceStorage.transferEssence(fromStorage.getStorage(), toStorage.getStorage(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), transferAmount);
            }
        }
    }
}
