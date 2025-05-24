package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.datanessence.block.decoration.EssenceBridgeBlockEntity;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class EssenceBridgeRenderer implements BlockEntityRenderer<EssenceBridgeBlockEntity> {
    public EssenceBridgeRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(EssenceBridgeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(blockEntity, matrix4f, bufferSource.getBuffer(DataNEssenceRenderTypes.ESSENCE_BRIDGE));
    }

    private void renderCube(EssenceBridgeBlockEntity blockEntity, Matrix4f pose, VertexConsumer consumer) {
        float f = this.getOffsetDown();
        float f1 = this.getOffsetUp();
        this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0, 1, 1, 0, Direction.SOUTH);
        this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1, 0, 0, 1, Direction.NORTH);
        this.renderFace(blockEntity, pose, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1, 0, 0, 1, Direction.EAST);
        this.renderFace(blockEntity, pose, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0, 1, 1, 0, Direction.WEST);
        this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, 0, 0, 1, 1, Direction.DOWN);
        this.renderFace(blockEntity, pose, consumer, 0.0F, 1.0F, f1, f1, 1.0F, 1.0F, 0.0F, 0.0F, 0, 0, 1, 1, Direction.UP);
    }

    private void renderFace(
            EssenceBridgeBlockEntity blockEntity,
            Matrix4f pose,
            VertexConsumer consumer,
            float x0,
            float x1,
            float y0,
            float y1,
            float z0,
            float z1,
            float z2,
            float z3,
            float minUvX,
            float minUvY,
            float maxUvX,
            float maxUvY,
            Direction direction
    ) {
        if (Block.shouldRenderFace(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos(), direction, blockEntity.getBlockPos().relative(direction))) {
            int color = 0xFFFF00FF;
            float normalX = direction.getNormal().getX();
            float normalY = direction.getNormal().getY();
            float normalZ = direction.getNormal().getZ();
            consumer.addVertex(pose, x0, y0, z0).setUv(minUvX, minUvY).setColor(color).setNormal(normalX, normalY, normalZ);
            consumer.addVertex(pose, x1, y0, z1).setUv(maxUvX, minUvY).setColor(color).setNormal(normalX, normalY, normalZ);
            consumer.addVertex(pose, x1, y1, z2).setUv(maxUvX, maxUvY).setColor(color).setNormal(normalX, normalY, normalZ);
            consumer.addVertex(pose, x0, y1, z3).setUv(minUvX, maxUvY).setColor(color).setNormal(normalX, normalY, normalZ);
        }
    }

    protected float getOffsetUp() {
        return 1f;
    }

    protected float getOffsetDown() {
        return 0f;
    }
}