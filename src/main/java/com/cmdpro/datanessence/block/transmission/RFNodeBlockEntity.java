package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.List;

public class RFNodeBlockEntity extends BaseCapabilityPointBlockEntity {

    public RFNodeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.RF_NODE.get(), pos, state);
    }

    @Override
    public Color[] linkColor() {
        return new Color[] {
                new Color(0x64719e),
                new Color(0x332a63)
        };
    }

    @Override
    public void transfer(BaseCapabilityPointBlockEntity sender, List<GraphPath<BlockPos, DefaultEdge>> desintations) {
        int transferAmount = Integer.MAX_VALUE; // I do not believe in limits

        for (GraphPath<BlockPos, DefaultEdge> i : desintations) {
            if (level.getBlockEntity(i.getEndVertex()) instanceof BaseCapabilityPointBlockEntity receiver) {
                IEnergyStorage receiverEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, receiver.getBlockPos().relative(receiver.getDirection().getOpposite()), receiver.getDirection());
                IEnergyStorage senderEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, sender.getBlockPos().relative(sender.getDirection().getOpposite()), sender.getDirection());

                if (senderEnergy == null || receiverEnergy == null)
                    continue;

                int transferred = receiverEnergy.receiveEnergy(transferAmount, false);
                senderEnergy.extractEnergy(transferred, false);
            }
        }
    }
}
