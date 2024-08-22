package com.cmdpro.datanessence.multiblock.predicates;

import com.cmdpro.datanessence.multiblock.MultiblockPredicate;
import com.cmdpro.datanessence.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.registry.MultiblockPredicateRegistry;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockstateMultiblockPredicate extends MultiblockPredicate {
    public BlockstateMultiblockPredicate(BlockState self) {
        this.self = self;
    }
    public BlockState self;
    @Override
    public boolean isSame(BlockState other, Rotation rotation) {
        if (other.is(self.getBlock())) {
            boolean stateMatches = true;
            for (Property<?> p : self.rotate(rotation).getProperties()) {
                if (other.hasProperty(p)) {
                    if (!other.getValue(p).equals(self.rotate(rotation).getValue(p))) {
                        stateMatches = false;
                        break;
                    }
                } else {
                    stateMatches = false;
                    break;
                }
            }
            return stateMatches;
        } else {
            return false;
        }
    }

    @Override
    public MultiblockPredicateSerializer getSerializer() {
        return MultiblockPredicateRegistry.BLOCKSTATE.get();
    }

    @Override
    public BlockState getVisual() {
        return self;
    }
}
