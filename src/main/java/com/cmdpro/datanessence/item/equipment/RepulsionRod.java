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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if ( ItemEssenceContainer.getEssence(stack, LUNAR) < useCost )
            return InteractionResult.FAIL;

        Level world = player.level();

        world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ALLAY_THROW, SoundSource.PLAYERS, 1.0f, world.random.nextFloat());
        target.knockback(4.0d, player.getX() - target.getX(), player.getZ() - target.getZ());
        ItemEssenceContainer.removeEssence(stack, LUNAR, useCost);

        // Particles
        Vec3 ang = player.getLookAngle();
        Vec3 pos = player.getEyePosition();

        for ( int i = 0; i < 12; i++ ) {
            int rg = Math.clamp(world.random.nextInt(255), 50, 255);

            var hsv = Color.RGBtoHSB(rg, rg, 0, null);
            Color color = new Color(Color.HSBtoRGB(hsv[0], 0.5f, 1.0f));

            float r1 = world.random.nextFloat() * 0.25f;
            float r2 = world.random.nextFloat() * 0.45f + 1.0f;

            double x = ( world.random.nextInt() % 2 == 0 ) ? pos.x+r1 : pos.x-r1;
            double y = ( world.random.nextInt() % 2 == 0 ) ? pos.y+r1 : pos.y-r1;
            double z = ( world.random.nextInt() % 2 == 0 ) ? pos.z+r1 : pos.z-r1;

            double vx = ang.x*r2;
            double vy = ang.y*r2+0.45;
            double vz = ang.z*r2;

            var particle = ( world.random.nextInt() % 2 == 0 ) ? new RhombusParticleOptions().setColor(color).setAdditive(true) : new SmallCircleParticleOptions().setColor(color).setAdditive(true);
            world.addParticle(particle, x, y-0.45, z, vx, vy, vz);
        }

        return InteractionResult.SUCCESS;
    }
}
