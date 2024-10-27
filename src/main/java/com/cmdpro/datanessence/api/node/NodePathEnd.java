package com.cmdpro.datanessence.api.node;

import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NodePathEnd {
    public BlockEntity entity;
    public BlockEntity[] path;
    public NodePathEnd(BlockEntity entity, BlockEntity[] path) {
        this.entity = entity;
        this.path = path;
    }
}
