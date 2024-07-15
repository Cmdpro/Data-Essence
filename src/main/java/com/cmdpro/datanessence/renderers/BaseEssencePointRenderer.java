package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.block.EssencePoint;
import com.cmdpro.datanessence.api.BaseEssencePointBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.awt.*;


public abstract class BaseEssencePointRenderer<T extends BaseEssencePointBlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {

    public BaseEssencePointRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (animatable.link != null) {
            Vec3 pos = animatable.getBlockPos().getCenter();
            poseStack.pushPose();
            poseStack.translate(-pos.x, -pos.y, -pos.z);
            poseStack.translate(0.5, 0.5, 0.5);
            ClientDataNEssenceUtil.renderBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0f, animatable.getLevel().getGameTime(), animatable.getBlockPos().getCenter(), animatable.link.getCenter(), animatable.linkColor(), 0.025f, 0.03f);
            poseStack.popPose();
        }
        bufferSource.getBuffer(getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick));
        poseStack.translate(0.5, 0.5, 0.5);
        AttachFace face = animatable.getBlockState().getValue(EssencePoint.FACE);
        Direction facing = animatable.getBlockState().getValue(EssencePoint.FACING);
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
    public static class Model<T extends GeoAnimatable> extends GeoModel<T> {
        public Model(ResourceLocation texture) {
            this.texture = texture;
        }
        public ResourceLocation texture;
        @Override
        public ResourceLocation getModelResource(T object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essence_point.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(T object) {
            return texture;
        }

        @Override
        public ResourceLocation getAnimationResource(T animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essence_point.animation.json");
        }
    }
}