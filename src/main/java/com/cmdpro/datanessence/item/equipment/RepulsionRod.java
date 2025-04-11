package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;

import net.minecraft.resources.ResourceLocation;
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

        target.knockback(4.0d, player.getX() - target.getX(), player.getZ() - target.getZ());
        ItemEssenceContainer.removeEssence(stack, LUNAR, useCost);

        Vec3 ang = player.getLookAngle();
        Vec3 pos = player.getEyePosition();

        Color color = new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().color);

        for ( int i = 0; i < 12; i++ ) {
            var particle = ( world.random.nextInt() % 2 == 0 ) ? new RhombusParticleOptions(color, true) : new SmallCircleParticleOptions(color, true);
            world.addParticle(particle, pos.x, pos.y-0.45, pos.z, ang.x*1.25, ang.y*1.25+0.45, ang.z*1.25);
            // todo: random colors (desaturated yellows), random pos, random velocity
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ItemEssenceContainer.getEssence(stack, LUNAR) < ItemEssenceContainer.getMaxEssence(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round( ItemEssenceContainer.getEssence(stack, LUNAR) * 13.0F / ItemEssenceContainer.getMaxEssence(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return EssenceTypeRegistry.LUNAR_ESSENCE.get().color;
    }
}
