package com.cmdpro.datanessence.block.world.shieldingstone;

import com.cmdpro.datanessence.api.block.ShieldingStone;
import com.cmdpro.datanessence.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.common.util.FakePlayer;

public class AncientShieldingGlass extends TransparentBlock implements ShieldingStone {

    public AncientShieldingGlass() {
        super(Properties.of()
                .requiresCorrectToolForDrops()
                .strength(-1.0f, 360000f)
                .sound(SoundType.GLASS)
                .instrument(NoteBlockInstrument.HAT)
                .noOcclusion()
                .isViewBlocking(BlockRegistry::never)
                .isSuffocating(BlockRegistry::never)
                .isValidSpawn(BlockRegistry::never)
                .isRedstoneConductor(BlockRegistry::never));
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player instanceof FakePlayer)
            return 0.0f;
        int i = player.hasCorrectToolForDrops(state, player.level(), pos) ? 250 : 1500;
        return player.getDigSpeed(state, pos) / 2.0F / i;
    }
}
