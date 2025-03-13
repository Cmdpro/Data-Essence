package com.cmdpro.datanessence.block.world.shieldingstone;

import com.cmdpro.datanessence.api.block.ShieldingStone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.util.FakePlayer;

public class LuminousAncientShieldingStone extends Block implements ShieldingStone {

    public LuminousAncientShieldingStone(int lightLevel) {
        super(Properties.of()
                .requiresCorrectToolForDrops()
                .strength(-1.0f, 1500f)
                .sound(SoundType.DEEPSLATE_TILES)
                .mapColor(MapColor.COLOR_PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .lightLevel((blockState) -> { return lightLevel;}));;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player instanceof FakePlayer && state.getBlock() instanceof ShieldingStone)
            return 0.0f;
        int i = player.hasCorrectToolForDrops(state, player.level(), pos) ? 250 : 1500;
        return player.getDigSpeed(state, pos) / 2.0F / i;
    }
}
