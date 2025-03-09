package com.cmdpro.datanessence.integration.mekanism;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPoint;
import com.cmdpro.datanessence.block.transmission.ItemPointBlockEntity;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static com.cmdpro.datanessence.integration.DataNEssenceIntegration.hasMekanism;

public class ChemicalNode extends BaseCapabilityPoint {

    public ChemicalNode(Properties properties) {
        super(properties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemRegistry.CHEMICAL_WIRE.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (!hasMekanism)
            return null;
        return new ChemicalNodeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof ChemicalNodeBlockEntity ent) {
                ChemicalNodeBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
}
