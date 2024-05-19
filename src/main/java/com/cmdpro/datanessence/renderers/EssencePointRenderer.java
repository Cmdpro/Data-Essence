package com.cmdpro.datanessence.renderers;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.block.EnderTransporter;
import com.cmdpro.runology.block.entity.EnderTransporterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;


public class EssencePointRenderer extends GeoBlockRenderer<EnderTransporterBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public EssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
        renderDispatcher = rendererProvider.getEntityRenderer();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EnderTransporterBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("color")) {
            Color color = Color.ofOpaque(animatable.color.getTextColor());
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }

    }


    @Override
    public void preRender(PoseStack poseStack, EnderTransporterBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        Entity entity = Minecraft.getInstance().getCameraEntity();
        double d0 = (double)Minecraft.getInstance().gameMode.getPickRange();
        double entityReach = Minecraft.getInstance().player.getEntityReach();
        HitResult hitResult = entity.pick(Math.max(d0, entityReach), 1, false);
        if (hitResult instanceof BlockHitResult result) {
            if (result.getBlockPos().equals(animatable.getBlockPos())) {
                poseStack.pushPose();
                poseStack.translate(0.5F, 0.5f, 0.5F);
                if (!result.getDirection().equals(animatable.getDirection())) {
                    Vec3i normal = result.getDirection().getNormal();
                    poseStack.translate(normal.getX(), normal.getY(), normal.getZ());
                }
                poseStack.mulPose(this.renderDispatcher.cameraOrientation());
                poseStack.scale(-0.025F, -0.025F, 0.025F);
                Font font = Minecraft.getInstance().font;
                Component text = animatable.transportType == EnderTransporterBlockEntity.TransportType.ITEM ? Component.translatable("block.runology.endertransporter.item") : Component.translatable("block.runology.endertransporter.fluid");
                float f2 = (float) (-font.width(text) / 2);
                int j = 0x00000000;
                font.drawInBatch(text, f2, 0f, 0xffffffff, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, j, packedLight);
                text = animatable.mode == EnderTransporterBlockEntity.Mode.EXTRACT ? Component.translatable("block.runology.endertransporter.extract") : Component.translatable("block.runology.endertransporter.insert");
                f2 = (float) (-font.width(text) / 2);
                poseStack.translate(0F, -10f, 0F);
                font.drawInBatch(text, f2, 0f, 0xffffffff, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, j, packedLight);
                poseStack.popPose();
            }
        }
        bufferSource.getBuffer(getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick));
        poseStack.translate(0.5, 0.5, 0.5);
        AttachFace face = animatable.getBlockState().getValue(EnderTransporter.FACE);
        Direction facing = animatable.getBlockState().getValue(EnderTransporter.FACING);
        if (face.equals(AttachFace.CEILING)) {
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
        }
        if (face.equals(AttachFace.WALL)) {
            if (facing.equals(Direction.NORTH)) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }
            if (facing.equals(Direction.SOUTH)) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            if (facing.equals(Direction.EAST)) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            }
            if (facing.equals(Direction.WEST)) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
        }
        poseStack.translate(-0.5, -0.5, -0.5);
    }

    @Override
    public RenderType getRenderType(EnderTransporterBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    public static class Model extends GeoModel<EnderTransporterBlockEntity> {
        @Override
        public ResourceLocation getModelResource(EnderTransporterBlockEntity object) {
            return new ResourceLocation(Runology.MOD_ID, "geo/endertransporter.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(EnderTransporterBlockEntity object) {
            return new ResourceLocation(Runology.MOD_ID, "textures/block/endertransporter.png");
        }

        @Override
        public ResourceLocation getAnimationResource(EnderTransporterBlockEntity animatable) {
            return new ResourceLocation(Runology.MOD_ID, "animations/endertransporter.animation.json");
        }
    }
}