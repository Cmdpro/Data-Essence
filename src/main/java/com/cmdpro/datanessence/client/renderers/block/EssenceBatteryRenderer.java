package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.storage.EssenceBatteryBlockEntity;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;

import java.awt.*;

public class EssenceBatteryRenderer implements BlockEntityRenderer<EssenceBatteryBlockEntity> {
    @Override
    public void render(EssenceBatteryBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.scale(1.001f, 1.001f, 1.001f);
        TextureAtlasSprite sprite =
                Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(OVERLAY);
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        VertexConsumer builder = pBuffer.getBuffer(RenderType.TRANSLUCENT);

        Color color = new Color(1.0f, 1.0f, 1.0f, pBlockEntity.storage.getEssence(EssenceTypeRegistry.ESSENCE.get())/pBlockEntity.storage.getMaxEssence());

        renderQuad(pBlockEntity, Direction.SOUTH, builder, pPoseStack, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, u0, v0, u1, v1, pPackedLight, color.getRGB());
        renderQuad(pBlockEntity, Direction.NORTH, builder, pPoseStack, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, u0, v0, u1, v1, pPackedLight, color.getRGB());

        //Rotate the pose stack because I failed to figure out how to make it work with vertex positioning
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderQuad(pBlockEntity, Direction.EAST, builder, pPoseStack, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, u0, v0, u1, v1, pPackedLight, color.getRGB());
        renderQuad(pBlockEntity, Direction.WEST, builder, pPoseStack, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, u0, v0, u1, v1, pPackedLight, color.getRGB());

        pPoseStack.popPose();
    }
    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v, int packedLight, int color) {
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
    }

    private static void renderQuad(EssenceBatteryBlockEntity pBlockEntity, Direction pDirection, VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int color) {
        if (Block.shouldRenderFace(pBlockEntity.getBlockState(), pBlockEntity.getLevel(), pBlockEntity.getBlockPos(), pDirection, pBlockEntity.getBlockPos().relative(pDirection))) {
            drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color);
            drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color);
            drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color);
            drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color);
        }
    }
    EntityRenderDispatcher renderDispatcher;
    public static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "block/essence_battery_overlay");
    public EssenceBatteryRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}
