package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.misc.ICustomEssencePointBehaviour;
import com.cmdpro.datanessence.api.misc.ICustomFluidPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.sun.jdi.connect.spi.TransportService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.awt.*;
import java.util.List;

public class FluidPointBlockEntity extends BaseCapabilityPointBlockEntity {
    public FluidPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_POINT.get(), pos, state);
    }
    @Override
    public Color linkColor() {
        return new Color(0x56bae9);
    }
    @Override
    public void transfer(BaseCapabilityPointBlockEntity from, List<BaseCapabilityPointBlockEntity> other) {
        int transferAmount = (int)Math.floor((float)getFinalSpeed(DataNEssenceConfig.fluidPointTransfer)/(float)other.size());
        for (BaseCapabilityPointBlockEntity i : other) {
            IFluidHandler resolved = level.getCapability(Capabilities.FluidHandler.BLOCK, i.getBlockPos().relative(i.getDirection().getOpposite()), i.getDirection());
            IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, from.getBlockPos().relative(from.getDirection().getOpposite()), from.getDirection());
            if (resolved == null || resolved2 == null) {
                continue;
            }
            if (other instanceof ICustomFluidPointBehaviour behaviour) {
                if (!behaviour.canInsertFluid(resolved, resolved2)) {
                    continue;
                }
            }
            for (int o = 0; o < resolved2.getTanks(); o++) {
                FluidStack copy = resolved2.getFluidInTank(o).copy();
                if (!copy.isEmpty()) {
                    copy.setAmount(Math.clamp(0, transferAmount, copy.getAmount()));
                    int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                    resolved2.getFluidInTank(o).shrink(filled);
                }
            }
        }
    }
}
