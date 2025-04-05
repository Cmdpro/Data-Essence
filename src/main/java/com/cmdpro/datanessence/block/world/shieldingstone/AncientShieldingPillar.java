package com.cmdpro.datanessence.block.world.shieldingstone;

import com.cmdpro.datanessence.api.block.ShieldingStone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.util.FakePlayer;

public class AncientShieldingPillar extends RotatedPillarBlock implements ShieldingStone {

    public AncientShieldingPillar() {
        super(Properties.of()
                .requiresCorrectToolForDrops()
                .strength(-1.0f, 360000f)
                .sound(SoundType.DEEPSLATE_TILES)
                .mapColor(MapColor.COLOR_PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM));
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player instanceof FakePlayer)
            return 0.0f;
        int i = player.hasCorrectToolForDrops(state, player.level(), pos) ? 250 : 1500;
        return player.getDigSpeed(state, pos) / 2.0F / i;
    }
}
