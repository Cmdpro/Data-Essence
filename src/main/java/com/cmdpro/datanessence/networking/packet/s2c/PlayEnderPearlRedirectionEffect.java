package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public record PlayEnderPearlRedirectionEffect(List<BlockPos> path) implements Message {

    public static final Type<PlayEnderPearlRedirectionEffect> TYPE = new Type<>(DataNEssence.locate("play_ender_pearl_redirection_effect"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayEnderPearlRedirectionEffect obj) {
        buf.writeCollection(obj.path, RegistryFriendlyByteBuf::writeBlockPos);
    }

    public static PlayEnderPearlRedirectionEffect read(RegistryFriendlyByteBuf buf) {
        List<BlockPos> path = buf.readList(RegistryFriendlyByteBuf::readBlockPos);
        return new PlayEnderPearlRedirectionEffect(path);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (!path.isEmpty()) {
            Level level = player.level();

            RandomSource random = level.getRandom();

            BlockPos start = path.getFirst();
            BlockPos end = path.getLast();
            Vec3 startCenter = start.getCenter();
            Vec3 endCenter = end.getCenter();

            level.playSound(player, startCenter.x, startCenter.y, startCenter.z, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS);
            level.playSound(player, endCenter.x, endCenter.y, endCenter.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
            if (path.size() >= 2) {
                for (int i = 0; i < path.size() - 1; i++) {
                    BlockPos current = path.get(i);
                    BlockPos next = path.get(i + 1);
                    Vec3 currentCenter = current.getCenter();
                    Vec3 nextCenter = next.getCenter();
                    int maxShifts = (int) (currentCenter.distanceTo(nextCenter) / 0.25);
                    for (int j = 0; j < maxShifts; j++) {
                        Vec3 shifted = currentCenter.lerp(nextCenter, (float) j / (float) maxShifts);
                        Vec3 speed = new Vec3(Mth.nextFloat(random, -0.1f, 0.1f), Mth.nextFloat(random, -0.1f, 0.1f), Mth.nextFloat(random, -0.1f, 0.1f));
                        level.addParticle(ParticleTypes.REVERSE_PORTAL, true, shifted.x, shifted.y, shifted.z, speed.x, speed.y, speed.z);
                    }
                }
            }
            for (int i = 0; i < 32; i++) {
                level
                        .addParticle(
                                ParticleTypes.REVERSE_PORTAL,
                                true,
                                endCenter.x,
                                endCenter.y + random.nextDouble() * 2.0,
                                endCenter.z,
                                random.nextGaussian(),
                                0.0,
                                random.nextGaussian()
                        );
            }
        }
    }
}
