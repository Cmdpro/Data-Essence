package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.NodePathEnd;
import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
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

public class NaturalEssencePointBlockEntity extends BaseEssencePointBlockEntity {
    public NaturalEssencePointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.NATURAL_ESSENCE_POINT.get(), pos, state);
    }
    @Override
    public Color linkColor() {
        return new Color(EssenceTypeRegistry.NATURAL_ESSENCE.get().getColor());
    }

    @Override
    public void transfer(BaseEssencePointBlockEntity from, List<NodePathEnd> other) {
        int transferAmount = (int) Math.floor((float)getFinalSpeed(DataNEssenceConfig.essencePointTransfer)/(float)other.size());
        for (NodePathEnd i : other) {
            BlockEntity fromEnt = from.getLevel().getBlockEntity(from.getBlockPos().relative(from.getDirection().getOpposite()));
            BlockEntity toEnt = i.entity.getLevel().getBlockEntity(i.entity.getBlockPos().relative(((BaseEssencePointBlockEntity)i.entity).getDirection().getOpposite()));
            if (fromEnt instanceof EssenceBlockEntity fromStorage && toEnt instanceof EssenceBlockEntity toStorage) {
                if (toEnt instanceof ICustomEssencePointBehaviour behaviour) {
                    if (!behaviour.canInsertEssence(toStorage.getStorage(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), transferAmount)) {
                        return;
                    }
                }
                EssenceStorage.transferEssence(fromStorage.getStorage(), toStorage.getStorage(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), transferAmount);
            }
        }
    }
}
