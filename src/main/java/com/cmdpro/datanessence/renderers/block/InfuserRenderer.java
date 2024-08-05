package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.InfuserBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;


public class InfuserRenderer extends GeoBlockRenderer<InfuserBlockEntity> {

    public InfuserRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public RenderType getRenderType(InfuserBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void postRender(PoseStack poseStack, InfuserBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.pushPose();
        poseStack.translate(0D, 0.5D, 0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getLevel().getLevelData().getGameTime() % 360));
        poseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable.item, ItemDisplayContext.GUI, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
        poseStack.popPose();
    }
    public static class Model extends GeoModel<InfuserBlockEntity> {
        @Override
        public ResourceLocation getModelResource(InfuserBlockEntity object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/infuser.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(InfuserBlockEntity object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/infuser.png");
        }

        @Override
        public ResourceLocation getAnimationResource(InfuserBlockEntity animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/infuser.animation.json");
        }
    }
}