package com.cmdpro.datanessence.integration.mekanism;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.NodePathEnd;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;

import java.awt.*;
import java.util.List;

import static com.cmdpro.datanessence.integration.DataNEssenceIntegration.BLOCK_CHEMICAL;

public class ChemicalNodeBlockEntity extends BaseCapabilityPointBlockEntity {

    public ChemicalNodeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.CHEMICAL_NODE.get(), pPos, pBlockState);
    }

    @Override
    public Color linkColor() {
        return new Color(0x65EA0C); // a mad science-y radioactive green
    }

    @Override
    public void transfer(BaseCapabilityPointBlockEntity from, List<NodePathEnd> other) {
        int transferAmount = (int) Math.floor((float)getFinalSpeed(DataNEssenceConfig.fluidPointTransfer)/(float)other.size());

        for (NodePathEnd i : other) {
            BaseCapabilityPointBlockEntity to = (BaseCapabilityPointBlockEntity) i.entity;
            List<ChemicalStack> allowedChemicals = null;

            for (BlockEntity j : i.path) {
                BaseCapabilityPointBlockEntity to2 = (BaseCapabilityPointBlockEntity) j;

                List<ChemicalStack> value = to2.getValue(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "allowed_chemicals"), null);
                if (allowedChemicals == null) {
                    allowedChemicals = value;
                } else if (value != null) {
                    allowedChemicals = allowedChemicals.stream().filter((stack1) -> value.stream().anyMatch((stack2) -> ChemicalStack.isSameChemical(stack1, stack2))).toList();
                }
            }

            IChemicalHandler resolved = level.getCapability(BLOCK_CHEMICAL, i.entity.getBlockPos().relative(to.getDirection().getOpposite()), to.getDirection());
            IChemicalHandler resolved2 = level.getCapability(BLOCK_CHEMICAL, from.getBlockPos().relative(from.getDirection().getOpposite()), from.getDirection());

            if (resolved == null || resolved2 == null) {
                continue;
            }

            if (other instanceof ICustomChemicalNodeBehaviour behaviour) {
                if (!behaviour.canInsertChemical(resolved, resolved2)) {
                    continue;
                }
            }

            for (int o = 0; o < resolved2.getChemicalTanks(); o++) {
                ChemicalStack copy = resolved2.getChemicalInTank(o).copy();
                if (allowedChemicals != null && allowedChemicals.stream().noneMatch((stack) -> ChemicalStack.isSameChemical(stack, copy))) {
                    continue;
                }
                if (!copy.isEmpty()) {
                    copy.setAmount((long) Math.clamp(0, transferAmount, copy.getAmount()));
                    ChemicalStack filled = resolved.insertChemical(copy, Action.EXECUTE);
                    resolved2.extractChemical(new ChemicalStack(copy.getChemical(), filled.getAmount()), Action.EXECUTE);
                }
            }
        }
    }
}
