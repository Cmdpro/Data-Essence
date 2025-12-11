// DisplaySignalRenderer.java
package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.block.logic.DisplaySignalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
//fuck this
public class DisplaySignalRenderer implements BlockEntityRenderer<DisplaySignalBlockEntity> {


    private static final Map<String, Direction> CUSTOM_FACES = new HashMap<>();

    static {
        //overrides:
        // CUSTOM_FACES.put("minecraft:furnace", Direction.SOUTH);
        // CUSTOM_FACES.put("datanessence:some_weird_block", Direction.EAST);
    }

    public DisplaySignalRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(DisplaySignalBlockEntity be,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay) {
        ItemStack stack = be.getDisplayedStack();
        if (stack.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        // Get a baked model for this stack
        BakedModel model = mc.getItemRenderer().getModel(stack, be.getLevel(), null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon(); // main sprite

        poseStack.pushPose();

        // Move to block center
        poseStack.translate(0.5, 0.5, 0.5);

        // Rotate so the quad faces the correct direction
        Direction faceDir = getDisplayDirection(be, stack);
        rotateToFace(poseStack, faceDir);

        // Pull quad slightly in front of the block face to avoid z-fighting
        poseStack.translate(0.0, 0.0, 0.501);

        // Scale down so the icon fits nicely
        float scale = 0.9f;
        poseStack.scale(scale, scale, scale);

        // Draw a simple 1x1 quad with that sprite
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        drawSpriteQuad(poseStack, vc, sprite, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private Direction getDisplayDirection(DisplaySignalBlockEntity be, ItemStack stack) {
        // 1) Per-block override by registry id (if it’s a block item)
        if (stack.getItem() instanceof BlockItem bi) {
            var id = bi.getBlock().builtInRegistryHolder().key().location().toString();
            Direction override = CUSTOM_FACES.get(id);
            if (override != null) {
                return override;
            }
        }

        // 2) If block has a facing property, use that
        BlockState state = be.getBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        if (state.hasProperty(BlockStateProperties.FACING)) {
            return state.getValue(BlockStateProperties.FACING);
        }

        // 3) Default
        return Direction.NORTH;
    }

    private void rotateToFace(PoseStack poseStack, Direction face) {
        // Assume we want the quad to face OUT of the “front” side
        switch (face) {
            case NORTH -> {
                // front is +Z in our local space
            }
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            case UP    -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case DOWN  -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
    }

    private void drawSpriteQuad(PoseStack poseStack,
                                VertexConsumer vc,
                                TextureAtlasSprite sprite,
                                int packedLight,
                                int packedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float x0 = -0.5f;
        float x1 =  0.5f;
        float y0 = -0.5f;
        float y1 =  0.5f;
        float z  =  0.0f;

        putVertex(vc, pose, x0, y0, z, u0, v1, packedLight, packedOverlay);
        putVertex(vc, pose, x1, y0, z, u1, v1, packedLight, packedOverlay);
        putVertex(vc, pose, x1, y1, z, u1, v0, packedLight, packedOverlay);
        putVertex(vc, pose, x0, y1, z, u0, v0, packedLight, packedOverlay);
    }


    private void putVertex(VertexConsumer vc,
                           PoseStack.Pose pose,
                           float x, float y, float z,
                           float u, float v,
                           int packedLight,
                           int packedOverlay) {

        int lightU = packedLight & 0xFFFF;
        int lightV = (packedLight >> 16) & 0xFFFF;
        boolean temp = 0 != packedOverlay;
        vc.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setUv1(OverlayTexture.u(packedOverlay), OverlayTexture.v(false))
                .setUv2(lightU, lightV)
                .setNormal(pose, 0.0f, 0.0f, 1.0f);
    }

}
