package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public record PlayGenderfluidTransitionEffect(BlockPos pos, Vec3 itemPos) implements Message {

    public static final Type<PlayGenderfluidTransitionEffect> TYPE = new Type<>(DataNEssence.locate("play_genderfluid_transition_effect"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayGenderfluidTransitionEffect obj) {
        buf.writeBlockPos(obj.pos);
        buf.writeVec3(obj.itemPos);
    }

    public static PlayGenderfluidTransitionEffect read(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Vec3 itemPos = buf.readVec3();
        return new PlayGenderfluidTransitionEffect(pos, itemPos);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Level level = player.level();

        RandomSource random = level.getRandom();
        float speedMult = 0.75f;
        player.level().playSound(player, pos, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1f, 1f);
        Vec3 pos = new Vec3(itemPos.x, this.pos.getCenter().add(0, 0.5, 0).y, itemPos.z);
        for (int i = 0; i < 10; i++) {
            level.addParticle(new RhombusParticleOptions().setColor(new Color(0xff6ab3fc)).setAdditive(true), pos.x, pos.y, pos.z, ((random.nextFloat()*2f)-1f)*speedMult, ((random.nextFloat()*2f)-1f)*speedMult, ((random.nextFloat()*2f)-1f)*speedMult);
            level.addParticle(new MoteParticleOptions().setColor(new Color(0xffffffff)).setAdditive(true), pos.x, pos.y, pos.z, ((random.nextFloat()*2f)-1f)*speedMult, ((random.nextFloat()*2f)-1f)*speedMult, ((random.nextFloat()*2f)-1f)*speedMult);
            level.addParticle(new SmallCircleParticleOptions().setColor(new Color(0xfffc92bb)).setAdditive(true), pos.x, pos.y, pos.z, ((random.nextFloat()*2f)-1f)*speedMult, ((random.nextFloat()*2f)-1f)*speedMult, ((random.nextFloat()*2f)-1f)*speedMult);
        }
        for (int i = 0; i < 16; i++) {
            Vec2 dir = new Vec2((float)Math.sin(Math.toRadians(i*(360f/16f))), (float)Math.cos(Math.toRadians(i*(360f/16f))));
            List<ParticleOptions> particles = new ArrayList<>();
            particles.add(new RhombusParticleOptions().setColor(new Color(0xff6ab3fc)).setAdditive(true));
            particles.add(new MoteParticleOptions().setColor(new Color(0xffffffff)).setAdditive(true));
            particles.add(new SmallCircleParticleOptions().setColor(new Color(0xfffc92bb)).setAdditive(true));
            particles = Util.shuffledCopy(particles.toArray(new ParticleOptions[0]), random);
            speedMult = 1f;
            level.addParticle(particles.get(0), pos.x, pos.y, pos.z, dir.x*speedMult, 0, dir.y*speedMult);
            speedMult /= 2f;
            level.addParticle(particles.get(1), pos.x, pos.y, pos.z, dir.x*speedMult, 0, dir.y*speedMult);
            speedMult /= 2f;
            level.addParticle(particles.get(2), pos.x, pos.y, pos.z, dir.x*speedMult, 0, dir.y*speedMult);
        }
    }
}
