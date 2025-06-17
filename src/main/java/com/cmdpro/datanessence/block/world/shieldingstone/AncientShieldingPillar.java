package com.cmdpro.datanessence.block.world.shieldingstone;

import com.cmdpro.datanessence.api.block.ShieldingStone;
import com.cmdpro.datanessence.block.DirectionalPillarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.util.FakePlayer;

public class AncientShieldingPillar extends DirectionalPillarBlock implements ShieldingStone {
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
