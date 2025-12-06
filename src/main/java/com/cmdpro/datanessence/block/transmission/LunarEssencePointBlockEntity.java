package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.node.ICustomEssencePointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.joml.Math;

import java.awt.*;
import java.util.List;

public class LunarEssencePointBlockEntity extends BaseEssencePointBlockEntity {
    public LunarEssencePointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNAR_ESSENCE_POINT.get(), pos, state);
    }

    @Override
    public Color[] linkColor() {
        return new Color[] {
                new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color),
                new Color(0xddc587)
        };
    }

    @Override
    public void transfer(BaseEssencePointBlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {
        int transferAmount = (int) Math.floor((float)getFinalSpeed(DataNEssenceConfig.essencePointTransfer)/(float)other.size());
        for (GraphPath<BlockPos, DefaultEdge> i : other) {
            if (level.getBlockEntity(i.getEndVertex()) instanceof BaseEssencePointBlockEntity entity) {
                BlockEntity fromEnt = from.getLevel().getBlockEntity(from.getBlockPos().relative(from.getDirection().getOpposite()));
                BlockEntity toEnt = entity.getLevel().getBlockEntity(entity.getBlockPos().relative(((BaseEssencePointBlockEntity) entity).getDirection().getOpposite()));
                if (fromEnt instanceof EssenceBlockEntity fromStorage && toEnt instanceof EssenceBlockEntity toStorage) {
                    if (fromEnt instanceof ICustomEssencePointBehaviour behaviour) {
                        if (!behaviour.canExtractEssence(fromStorage.getStorage(), toStorage.getStorage(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), transferAmount)) {
                            return;
                        }
                    }
                    if (toEnt instanceof ICustomEssencePointBehaviour behaviour) {
                        if (!behaviour.canInsertEssence(fromStorage.getStorage(), toStorage.getStorage(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), transferAmount)) {
                            return;
                        }
                    }
                    EssenceStorage.transferEssence(fromStorage.getStorage(), toStorage.getStorage(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), transferAmount);
                    updateBlock(fromEnt);
                    updateBlock(toEnt);
                }
            }
        }
    }

    public void updateBlock(BlockEntity ent) {
        BlockState blockState = level.getBlockState(ent.getBlockPos());
        this.level.sendBlockUpdated(ent.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
}
