package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

public class RepulsionRod extends Item {
    public float useCost = 50f;
    public static ResourceLocation LUNAR = DataNEssence.locate("lunar_essence");

    public RepulsionRod(Properties properties) {
        super(properties.component(DataComponentRegistry.ESSENCE_STORAGE, new ItemEssenceContainer(List.of(LUNAR), 7500)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (ItemEssenceContainer.getEssence(stack, LUNAR) < useCost) {
            return InteractionResultHolder.fail(stack);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ALLAY_THROW, SoundSource.PLAYERS, 1.0f, world.random.nextFloat());

        Vec3 ang = player.getLookAngle();
        Vec3 eyePos = player.getEyePosition();

        if (!world.isClientSide) {
            ItemEssenceContainer.removeEssence(stack, LUNAR, useCost);
            player.getCooldowns().addCooldown(this, 10);

            double range = 6.0;

            Vec3 endPos = eyePos.add(ang.scale(range));
            AABB searchBox = new AABB(eyePos, endPos).inflate(2.0);

            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, searchBox, entity ->
                    entity != player && !entity.isSpectator()
            );

            for (LivingEntity target : targets) {
                Vec3 toTarget = target.position().subtract(player.position()).normalize();
                double dot = ang.dot(toTarget);

                if (dot > 0.5) { // -1 to 1 (0 to 360 degrees) maybe ill add a method to turn degrees into dot product

                    Vec3 pushForce = ang.normalize().scale(2.0);

                    target.setDeltaMovement(target.getDeltaMovement().add(pushForce));

                    target.hasImpulse = true;
                    target.hurtMarked = true;
                }
            }
        }

        if (world.isClientSide) {
            for (int i = 0; i < 12; i++) {
                int rg = Math.clamp(world.random.nextInt(255), 50, 255);

                var hsv = Color.RGBtoHSB(rg, rg, 0, null);
                Color color = new Color(Color.HSBtoRGB(hsv[0], 0.5f, 1.0f));

                float r1 = world.random.nextFloat() * 0.25f;
                float r2 = world.random.nextFloat() * 0.45f + 1.0f;

                double x = (world.random.nextInt() % 2 == 0) ? eyePos.x + r1 : eyePos.x - r1;
                double y = (world.random.nextInt() % 2 == 0) ? eyePos.y + r1 : eyePos.y - r1;
                double z = (world.random.nextInt() % 2 == 0) ? eyePos.z + r1 : eyePos.z - r1;

                double vx = ang.x * r2;
                double vy = ang.y * r2 + 0.45;
                double vz = ang.z * r2;

                var particle = (world.random.nextInt() % 2 == 0)
                        ? new RhombusParticleOptions().setColor(color).setAdditive(true)
                        : new SmallCircleParticleOptions().setColor(color).setAdditive(true);

                world.addParticle(particle, x, y - 0.45, z, vx, vy, vz);
            }
        }

        return InteractionResultHolder.success(stack);
    }
}