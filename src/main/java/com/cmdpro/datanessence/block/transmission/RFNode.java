package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPoint;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RFNode extends BaseCapabilityPoint {

    public RFNode(Properties properties) {
        super(properties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemRegistry.RF_WIRE.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RFNodeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof RFNodeBlockEntity ent) {
                RFNodeBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
}
