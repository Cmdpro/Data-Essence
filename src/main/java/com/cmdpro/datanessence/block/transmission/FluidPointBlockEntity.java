package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.node.ICustomFluidPointBehaviour;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.joml.Math;

import java.awt.*;
import java.util.List;

public class FluidPointBlockEntity extends BaseCapabilityPointBlockEntity {
    public FluidPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_POINT.get(), pos, state);
    }

    @Override
    public Color[] linkColor() {
        return new Color[] {
                new Color(0x56bae9),
                new Color(0x2e3ee7)
        };
    }

    @Override
    public void transfer(BaseCapabilityPointBlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {
        int transferAmount = (int)Math.floor((float)getFinalSpeed(DataNEssenceConfig.fluidPointTransfer)/(float)other.size());
        for (GraphPath<BlockPos, DefaultEdge> i : other) {
            if (level.getBlockEntity(i.getEndVertex()) instanceof BaseCapabilityPointBlockEntity ent) {
                List<FluidStack> allowedFluidstacks = null;
                for (BlockPos j : i.getVertexList()) {
                    if (level.getBlockEntity(j) instanceof BaseCapabilityPointBlockEntity ent2) {
                        List<FluidStack> value = ent2.getValue(DataNEssence.locate("allowed_fluidstacks"), null);
                        if (allowedFluidstacks == null) {
                            allowedFluidstacks = value;
                        } else if (value != null) {
                            allowedFluidstacks = allowedFluidstacks.stream().filter((stack1) -> value.stream().anyMatch((stack2) -> FluidStack.isSameFluid(stack1, stack2))).toList();
                        }
                    }
                }
                IFluidHandler resolved = level.getCapability(Capabilities.FluidHandler.BLOCK, ent.getBlockPos().relative(ent.getDirection().getOpposite()), ent.getDirection());
                IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, from.getBlockPos().relative(from.getDirection().getOpposite()), from.getDirection());
                if (resolved == null || resolved2 == null) {
                    continue;
                }
                if (level.getBlockEntity(from.getBlockPos().relative(from.getDirection().getOpposite())) instanceof ICustomFluidPointBehaviour behaviour) {
                    if (!behaviour.canExtractFluid(resolved, resolved2)) {
                        continue;
                    }
                }
                if (level.getBlockEntity(ent.getBlockPos().relative(ent.getDirection().getOpposite())) instanceof ICustomFluidPointBehaviour behaviour) {
                    if (!behaviour.canInsertFluid(resolved, resolved2)) {
                        continue;
                    }
                }
                for (int o = 0; o < resolved2.getTanks(); o++) {
                    FluidStack copy = resolved2.getFluidInTank(o).copy();
                    if (allowedFluidstacks != null && allowedFluidstacks.stream().noneMatch((stack) -> FluidStack.isSameFluid(stack, copy))) {
                        continue;
                    }
                    if (!copy.isEmpty()) {
                        copy.setAmount(Math.clamp(0, transferAmount, copy.getAmount()));
                        int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        resolved2.drain(new FluidStack(copy.getFluid(), filled), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }
    }
}
