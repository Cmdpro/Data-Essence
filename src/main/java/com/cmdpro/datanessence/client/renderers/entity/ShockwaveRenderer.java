package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.ShockwaveEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.awt.*;

public class ShockwaveRenderer extends EntityRenderer<ShockwaveEntity> {

    public ShockwaveRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    public ResourceLocation getTextureLocation(ShockwaveEntity shockwave) {
        return DataNEssence.locate("textures/entity/shockwave.png");
    }

    @Override
    public boolean shouldRender(ShockwaveEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(ShockwaveEntity shockwave, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(shockwave, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        float range = shockwave.size+(shockwave.speed*partialTick);
        float alpha = 1f-Math.clamp(range/shockwave.maxSize, 0f, 1f);

        Color color = new Color(1f, 1f, 1f, alpha);

        poseStack.pushPose();
        RenderSystem.enableBlend();
        renderQuad(bufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(shockwave))), poseStack, -1*range, 0, -1*range, 1*range, 0, 1*range, 0, 0, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color.getRGB());
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v, int packedLight, int packedOverlay, int color) {
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setLight(packedLight)
                .setOverlay(packedOverlay)
                .setNormal(0, 1, 0);
    }

    private static void renderQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int packedOverlay, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, packedOverlay, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, packedOverlay, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, packedOverlay, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, packedOverlay, color);
    }
}