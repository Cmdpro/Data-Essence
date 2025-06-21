package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record PlayAncientCombatUnitBlinkEffect(Vec3 from, Vec3 to, boolean curve) implements Message {

    public static final Type<PlayAncientCombatUnitBlinkEffect> TYPE = new Type<>(DataNEssence.locate("play_ancient_combat_unit_blink_effect"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayAncientCombatUnitBlinkEffect obj) {
        buf.writeVec3(obj.from);
        buf.writeVec3(obj.to);
        buf.writeBoolean(obj.curve);
    }

    public static PlayAncientCombatUnitBlinkEffect read(RegistryFriendlyByteBuf buf) {
        Vec3 from = buf.readVec3();
        Vec3 to = buf.readVec3();
        boolean curve = buf.readBoolean();
        return new PlayAncientCombatUnitBlinkEffect(from, to, curve);
    }
    private static Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
        double d0 = pTarget.x - pVec.x;
        double d1 = pTarget.y - pVec.y;
        double d2 = pTarget.z - pVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
                Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
                Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
        );
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Level level = player.level();

        RandomSource random = level.getRandom();

        float angleMult = random.nextBoolean() ? -1 : 1;

        Vec3 center = from.lerp(to, 0.5f);
        float length = (float) from.vectorTo(to).length();
        int maxShifts = (int) (from.distanceTo(to) / 0.25);
        for (int j = 0; j < maxShifts; j++) {
            Vec3 shifted = from.lerp(to, (float) j / (float) maxShifts);
            if (curve) {
                double radians = Math.toRadians((180f / (float) maxShifts) * (float) j)*angleMult;
                Vector3f shift = new Vector3f((float)Math.sin(radians)/((float)from.distanceTo(to)/5f), 0, (float)Math.cos(radians)).mul(length / 2f);
                Quaternionf rotation = new Quaternionf();
                Vec2 angle = calculateRotationVector(from, to);
                rotation.rotateY((float) Math.toRadians(-angle.y + 180));
                rotation.rotateX((float)Math.toRadians(-angle.x));
                shift.rotate(rotation);
                shifted = center.add(
                    shift.x, shift.y, shift.z
                );
            }
            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.WITCH, shifted.x, shifted.y, shifted.z, 0, 0, 0);
        }
        for (int i = 0; i < 32; i++) {
            level
                    .addParticle(
                            ParticleTypes.REVERSE_PORTAL,
                            true,
                            to.x,
                            to.y + ((random.nextDouble() * 2f)-1f),
                            to.z,
                            random.nextGaussian()/2f,
                            0.0,
                            random.nextGaussian()/2f
                    );
        }
    }
}
