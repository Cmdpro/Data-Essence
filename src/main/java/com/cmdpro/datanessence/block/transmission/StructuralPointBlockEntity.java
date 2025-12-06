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

public class StructuralPointBlockEntity extends BaseEssencePointBlockEntity {

    public StructuralPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.STRUCTURAL_POINT.get(), pos, state);
    }

    @Override
    public Color linkColor() {
        // yellow-ish color for structural network
        return new Color(0xFFFF55);
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
    public void transfer(BaseEssencePointBlockEntity from,
                         List<GraphPath<BlockPos, DefaultEdge>> other) {

        for (GraphPath<BlockPos, DefaultEdge> path : other) {
            if (level.getBlockEntity(path.getEndVertex()) instanceof BaseEssencePointBlockEntity endNode) {

                BlockEntity fromEnt = from.getLevel()
                        .getBlockEntity(from.getBlockPos().relative(from.getDirection().getOpposite()));
                BlockEntity toEnt = endNode.getLevel()
                        .getBlockEntity(endNode.getBlockPos().relative(endNode.getDirection().getOpposite()));

                if (fromEnt instanceof IStructuralSignalProvider provider &&
                        toEnt   instanceof IStructuralSignalReceiver receiver) {

                    String id = provider.getSignalId();
                    int amount = provider.getSignalAmount();

                    if (id != null && !id.isEmpty() && amount > 0) {
                        receiver.acceptSignal(id, amount);
                        updateBlock(fromEnt);
                        updateBlock(toEnt);
                    }
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
