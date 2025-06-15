package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.api.CommonVariables;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CognizantCube extends Item {
    final int textColor = 0x9c26c7; // the color messages display in

    public CognizantCube(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
        return 38;
    }
    @Override
    @SuppressWarnings("removal")
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                if (player.getUseItem() == itemInHand && player.isUsingItem()) {
                    float f = (((float)itemInHand.getUseDuration(player)-(float)player.getUseItemRemainingTicks()) + partialTick);
                    poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(Math.toRadians(Math.clamp(0f, 36f, f) * (360f / 12f))) * 25f));
                    applyItemArmTransform(poseStack, arm, equipProcess);
                    return true;
                } else {
                    return false;
                }
            }
            private void applyItemArmTransform(PoseStack pPoseStack, HumanoidArm pHand, float pEquippedProg) {
                int i = pHand == HumanoidArm.RIGHT ? 1 : -1;
                pPoseStack.translate((float)i * 0.56F, -0.52F + pEquippedProg * -0.6F, -0.72F);
            }
        });
    }
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }
    public List<Component> results = new ArrayList<>() {
        {
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result1").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result2").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result3").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result4").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result5").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result6").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result7").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result8").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result9").withStyle(Style.EMPTY.withColor(textColor)));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result10").withStyle(Style.EMPTY.withColor(textColor)));
        }
    };
    public Component additionalPylons = Component.translatable("item.datanessence.magitech_8_ball.result11").withStyle(Style.EMPTY.withFont(CommonVariables.ANCIENT_FONT).withColor(textColor));
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(itemstack);
    }
    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (!pLevel.isClientSide) {
            if (pRemainingUseDuration % 6 == 0) {
                pLevel.playSound(null, pLivingEntity.blockPosition(), SoundRegistry.COGNIZANT_CUBE_SHAKE.value(), SoundSource.PLAYERS);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide) {
            pLevel.playSound(null, pLivingEntity.blockPosition(), SoundRegistry.COGNIZANT_CUBE_MESSAGE.value(), SoundSource.PLAYERS);
            if (pLivingEntity instanceof Player player) {
                Component msg = results.get(RandomUtils.nextInt(0, results.size()));
                if (RandomUtils.nextInt(0, 25) == 0) {
                    msg = additionalPylons;
                }
                player.sendSystemMessage(msg);
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return pStack;
    }
}
