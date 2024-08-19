package com.cmdpro.datanessence.multiblock;

import com.cmdpro.datanessence.multiblock.predicates.BlockstateMultiblockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Multiblock {
    public String[][] multiblockLayers;
    public HashMap<Character, MultiblockPredicate> key;
    public BlockPos offset;
    private List<List<PredicateAndPos>> states;
    public Multiblock(String[][] multiblockLayers, HashMap<Character, MultiblockPredicate> key, BlockPos offset) {
        this.multiblockLayers = multiblockLayers;
        key.put(' ', new BlockstateMultiblockPredicate(Blocks.AIR.defaultBlockState()));
        this.key = key;
        this.offset = offset;
    }
    public List<List<PredicateAndPos>> getStates() {
        return getStates(false);
    }
    public List<List<PredicateAndPos>> getStates(boolean forceCacheReset) {
        if (forceCacheReset || this.states == null) {
            int x = 0;
            int y = 0;
            int z = 0;
            List<List<PredicateAndPos>> states = new ArrayList<>();
            for (String[] i : multiblockLayers) {
                z = 0;
                for (String o : i) {
                    List<PredicateAndPos> layer = new ArrayList<>();
                    x = 0;
                    for (char p : o.toCharArray()) {
                        layer.add(new PredicateAndPos(key.get(p), new BlockPos(x, y, z).offset(offset.getX(), offset.getY(), offset.getZ())));
                        x++;
                    }
                    states.add(layer);
                    z++;
                }
                y++;
            }
            this.states = states;
            return states;
        } else {
            return this.states;
        }
    }
    public boolean checkMultiblock(Level level, BlockPos pos) {
        return checkMultiblock(level, pos, Rotation.NONE);
    }
    public boolean checkMultiblock(Level level, BlockPos pos, Rotation rotation) {
        for (List<PredicateAndPos> i : getStates()) {
            for (PredicateAndPos o : i) {
                if (o.predicate == null) {
                    continue;
                }
                BlockPos blockPos = o.offset.rotate(rotation).offset(pos.getX(), pos.getY(), pos.getZ());
                BlockState state = level.getBlockState(blockPos);
                if (!o.predicate.isSame(state)) {
                    return false;
                }
            }
        }
        return true;
    }
    public static class PredicateAndPos {
        public PredicateAndPos(MultiblockPredicate predicate, BlockPos offset) {
            this.predicate = predicate;
            this.offset = offset;
        }
        public MultiblockPredicate predicate;
        public BlockPos offset;
    }
}
