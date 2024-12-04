package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.api.CommonVariables;
import com.cmdpro.datanessence.registry.MobEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ShrinkRay extends Item {
    public ShrinkRay(Properties pProperties) {
        super(pProperties);
    }
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        double range = 10;
        double rangeSquared = Mth.square(range);
        HitResult result = pPlayer.pick(range, 0.0f, false);
        Vec3 vec31 = pPlayer.getLookAngle();
        Vec3 vec3 = pPlayer.getEyePosition();
        if (result.getType() != HitResult.Type.MISS) {
            rangeSquared = result.getLocation().distanceToSqr(vec3);
            range = Math.sqrt(result.getLocation().distanceToSqr(vec3));
        }
        Vec3 vec32 = vec3.add(vec31.x * range, vec31.y * range, vec31.z * range);
        AABB aabb = pPlayer.getBoundingBox().expandTowards(vec31.scale(range)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                pPlayer, vec3, vec32, aabb, p_234237_ -> !p_234237_.isSpectator() && p_234237_.isPickable(), rangeSquared
        );
        if (entityHitResult != null) {
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.SHRUNKEN, 20 * 10));
            }
        }
        pPlayer.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.success(itemstack);
    }
}
