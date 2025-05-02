package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.entity.LunarStrike;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.awt.*;

public class LunarStrikeRenderer extends EntityRenderer<LunarStrike> {

    public LunarStrikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(LunarStrike pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        pPoseStack.translate(0D, 0.5D*1.5, 0D);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getEntityData().get(LunarStrike.DIRECTION)));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(-((pEntity.level().getLevelData().getGameTime()*5) % 360)));
        pPoseStack.scale(1.5F, 1.5F, 1.5F);
        pPoseStack.pushPose();
        pPoseStack.translate(0, (0.5*1.5)-(0.5), 0);
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(BlockRegistry.LUNAR_CRYSTAL_SEED.get()), ItemDisplayContext.HEAD, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), 0);
        pPoseStack.popPose();
        pPoseStack.popPose();
    }
    public ResourceLocation getTextureLocation(LunarStrike pEntity) {
        return null;
    }
}