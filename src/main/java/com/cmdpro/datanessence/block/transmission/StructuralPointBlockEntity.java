package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.signal.IStructuralSignalProvider;
import com.cmdpro.datanessence.api.signal.IStructuralSignalReceiver;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class StructuralPointBlockEntity extends BaseEssencePointBlockEntity {

    public StructuralPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.STRUCTURAL_POINT.get(), pos, state);
    }

    @Override
    public Color[] linkColor() {
        return new Color[]{new Color((0xFFFF55)), new Color((0xFFFF55))};
    }

    /**
     * Delegates to the base tick which handles graph logic, then calls this.transfer(...)
     */
    public static void tick(net.minecraft.world.level.Level level,
                            BlockPos pos,
                            BlockState state,
                            StructuralPointBlockEntity be) {
        BaseEssencePointBlockEntity.tick(level, pos, state, be);
    }

    /**
     * Transfer structural signals from the "from" node to all end nodes.
     * Works similarly to EssencePointBlockEntity.transfer but for signals.
     */
    @Override
    public void transfer(BaseEssencePointBlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {
        if (level == null || level.isClientSide) {
            return;
        }

        // Block the node is attached to on the "from" side
        BlockEntity fromEnt = from.getLevel().getBlockEntity(
                from.getBlockPos().relative(from.getDirection().getOpposite())
        );
        if (!(fromEnt instanceof IStructuralSignalProvider provider)) {
            return;
        }

        //Get ALL signals from the provider (multi-signal)
        Map<String, Integer> signals = provider.getSignals();
        if (signals.isEmpty()) {
            return;
        }

        // Send the same bundle of signals along every outgoing path
        for (GraphPath<BlockPos, DefaultEdge> path : other) {
            BlockPos endPos = path.getEndVertex();
            if (!(level.getBlockEntity(endPos) instanceof BaseEssencePointBlockEntity endNode)) {
                continue;
            }

            BlockEntity toEnt = endNode.getLevel().getBlockEntity(
                    endNode.getBlockPos().relative(endNode.getDirection().getOpposite())
            );

            if (toEnt instanceof IStructuralSignalReceiver receiver) {
                receiver.acceptSignals(signals);
                if (toEnt instanceof BlockEntity be) {
                    // force client update if your reader has a GUI
                    BlockState st = be.getLevel().getBlockState(be.getBlockPos());
                    be.getLevel().sendBlockUpdated(be.getBlockPos(), st, st, 3);
                    be.setChanged();
                }
            }
        }
    }


    /**
     * Same helper pattern as in EssencePointBlockEntity to force a block update on connected blocks.
     */
    public void updateBlock(BlockEntity ent) {
        BlockState state = level.getBlockState(ent.getBlockPos());
        this.level.sendBlockUpdated(ent.getBlockPos(), state, state, 3);
        this.setChanged();
    }
}
