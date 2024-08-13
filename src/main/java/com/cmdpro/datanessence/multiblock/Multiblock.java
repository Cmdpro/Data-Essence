package com.cmdpro.datanessence.multiblock;

import com.cmdpro.datanessence.block.auxiliary.Charger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Multiblock {
    public String[][] multiblockLayers;
    public HashMap<Character, BlockState> key;
    public BlockPos offset;
    private List<List<StateAndPos>> states;
    public Multiblock(String[][] multiblockLayers, HashMap<Character, BlockState> key, BlockPos offset) {
        this.multiblockLayers = multiblockLayers;
        key.put(' ', Blocks.AIR.defaultBlockState());
        this.key = key;
        this.offset = offset;
    }
    public List<List<StateAndPos>> getStates() {
        return getStates(false);
    }
    public List<List<StateAndPos>> getStates(boolean forceCacheReset) {
        if (forceCacheReset || this.states == null) {
            int x = 0;
            int y = 0;
            int z = 0;
            List<List<StateAndPos>> states = new ArrayList<>();
            for (String[] i : multiblockLayers) {
                z = 0;
                for (String o : i) {
                    List<StateAndPos> layer = new ArrayList<>();
                    x = 0;
                    for (char p : o.toCharArray()) {
                        layer.add(new StateAndPos(key.get(p), new BlockPos(x, y, z).offset(offset.getX(), offset.getY(), offset.getZ())));
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
        for (List<StateAndPos> i : getStates()) {
            for (StateAndPos o : i) {
                if (o.state.is(Blocks.AIR)) {
                    continue;
                }
                BlockPos blockPos = o.offset.rotate(rotation).offset(pos.getX(), pos.getY(), pos.getZ());
                BlockState state = level.getBlockState(blockPos);
                if (state.is(o.state.getBlock())) {
                    boolean stateMatches = true;
                    for (Property<?> p : o.state.getProperties()) {
                        if (state.hasProperty(p)) {
                            if (!state.getValue(p).equals(o.state.getValue(p))) {
                                stateMatches = false;
                                break;
                            }
                        } else {
                            stateMatches = false;
                            break;
                        }
                    }
                    if (!stateMatches) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    public static class StateAndPos {
        public StateAndPos(BlockState state, BlockPos offset) {
            this.state = state;
            this.offset = offset;
        }
        public BlockState state;
        public BlockPos offset;
    }
}
