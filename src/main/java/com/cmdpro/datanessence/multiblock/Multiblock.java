package com.cmdpro.datanessence.multiblock;

import com.cmdpro.datanessence.block.auxiliary.Charger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Multiblock {
    public String[][] multiblockLayers;
    public HashMap<Character, BlockState> key;
    public BlockPos offset;
    public Multiblock(String[][] multiblockLayers, HashMap<Character, BlockState> key, BlockPos offset) {
        this.multiblockLayers = multiblockLayers;
        key.put(' ', Blocks.AIR.defaultBlockState());
        this.key = key;
        this.offset = offset;
    }
    public List<List<StateAndPos>> getStates() {
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
        return states;
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
