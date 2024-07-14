package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.ClientModEvents;
import com.cmdpro.datanessence.DataNEssence;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Magitech8Ball extends Item {
    public Magitech8Ball(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 20;
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                if (player.getUseItem() == itemInHand && player.isUsingItem()) {
                    applyEatTransform(poseStack, partialTick, arm, itemInHand);
                    applyItemArmTransform(poseStack, arm, equipProcess);
                    return true;
                } else {
                    return false;
                }
            }
            private void applyEatTransform(PoseStack pPoseStack, float pPartialTicks, HumanoidArm pHand, ItemStack pStack) {
                float f = (float) Minecraft.getInstance().player.getUseItemRemainingTicks() - pPartialTicks + 1.0F;
                float f1 = f / (float)pStack.getUseDuration();
                if (f1 < 0.8F) {
                    float f2 = Mth.abs(Mth.cos(f / 4.0F * (float) Math.PI) * 0.1F);
                    pPoseStack.translate(0.0F, f2, 0.0F);
                }

                float f3 = 1.0F - (float)Math.pow((double)f1, 27.0);
                int i = pHand == HumanoidArm.RIGHT ? 1 : -1;
                pPoseStack.translate(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
                pPoseStack.mulPose(Axis.YP.rotationDegrees((float)i * f3 * 90.0F));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(f3 * 10.0F));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees((float)i * f3 * 30.0F));
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
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result1"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result2"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result3"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result4"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result5"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result6"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result7"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result8"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result9"));
            this.add(Component.translatable("item.datanessence.magitech_8_ball.result10"));
        }
    };
    public Component additionalPylons = Component.translatable("item.datanessence.magitech_8_ball.result11").withStyle(Style.EMPTY.withFont(new ResourceLocation(DataNEssence.MOD_ID, "ancient")));
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(itemstack);
    }
    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (!pLevel.isClientSide) {
            if (pRemainingUseDuration % 4 == 0) {
                pLevel.playSound(null, pLivingEntity.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.PLAYERS);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide) {
            pLevel.playSound(null, pLivingEntity.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);
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
