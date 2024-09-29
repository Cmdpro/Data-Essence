package com.cmdpro.datanessence.multiblock;

import com.cmdpro.datanessence.api.multiblock.MultiblockPredicate;
import com.cmdpro.datanessence.multiblock.predicates.BlockstateMultiblockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class Multiblock {
    public String[][] multiblockLayers;
    public Map<Character, MultiblockPredicate> key;
    public BlockPos offset;
    private List<List<PredicateAndPos>> states;
    public Multiblock(String[][] multiblockLayers, Map<Character, MultiblockPredicate> key, BlockPos offset) {
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
    public Rotation getMultiblockRotation(Level level, BlockPos pos) {
        if (checkMultiblock(level, pos, Rotation.NONE)) {
            return Rotation.NONE;
        }
        if (checkMultiblock(level, pos, Rotation.COUNTERCLOCKWISE_90)) {
            return Rotation.COUNTERCLOCKWISE_90;
        }
        if (checkMultiblock(level, pos, Rotation.CLOCKWISE_180)) {
            return Rotation.CLOCKWISE_180;
        }
        if (checkMultiblock(level, pos, Rotation.CLOCKWISE_90)) {
            return Rotation.CLOCKWISE_90;
        }
        return null;
    }
    public boolean checkMultiblock(Level level, BlockPos pos) {
        return checkMultiblock(level, pos, Rotation.NONE);
    }
    public boolean checkMultiblockAll(Level level, BlockPos pos) {
        return checkMultiblock(level, pos, Rotation.NONE) || checkMultiblock(level, pos, Rotation.CLOCKWISE_90) || checkMultiblock(level, pos, Rotation.CLOCKWISE_180) || checkMultiblock(level, pos, Rotation.COUNTERCLOCKWISE_90);
    }
    public boolean checkMultiblock(Level level, BlockPos pos, Rotation rotation) {
        for (List<PredicateAndPos> i : getStates()) {
            for (PredicateAndPos o : i) {
                if (o.predicate == null) {
                    continue;
                }
                BlockPos blockPos = o.offset.rotate(rotation).offset(pos.getX(), pos.getY(), pos.getZ());
                BlockState state = level.getBlockState(blockPos);
                if (!o.predicate.isSame(state, rotation)) {
                    return false;
                }
            }
        }
        return true;
    }
    public List<List<String>> getMultiblockLayersList() {
        return Arrays.stream(multiblockLayers).map((a) -> Arrays.stream(a).toList()).toList();
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
