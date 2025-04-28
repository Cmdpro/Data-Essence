package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.awt.*;

public record PlayBufferTransferParticle(BlockPos pos, Color color) implements Message {

    public static final Type<PlayBufferTransferParticle> TYPE = new Type<>(DataNEssence.locate("play_buffer_transfer_particle"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayBufferTransferParticle obj) {
        buf.writeBlockPos(obj.pos);
        buf.writeInt(obj.color.getRGB());
    }

    public static PlayBufferTransferParticle read(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Color color = new Color(buf.readInt());

        return new PlayBufferTransferParticle(pos, color);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Level world = player.level();

        RandomSource random = world.getRandom();
        double d0 = 0.5625;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!world.getBlockState(blockpos).isSolidRender(world, blockpos) && direction != Direction.UP && direction != Direction.DOWN) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5 + d0 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5 + d0 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5 + d0 * (double)direction.getStepZ() : (double)random.nextFloat();
                world.addParticle(
                        new MoteParticleOptions().setColor(color).setAdditive(true).setLifetime(20), (double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3, 0.0, 0.1, 0.0
                );
            }
        }
    }
}
