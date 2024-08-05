package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.auxiliary.ChargerBlockEntity;
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


public class ChargerRenderer extends GeoBlockRenderer<ChargerBlockEntity> {

    public ChargerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public RenderType getRenderType(ChargerBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void postRender(PoseStack poseStack, ChargerBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.pushPose();
        poseStack.translate(0D, 0.5D, 0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getLevel().getLevelData().getGameTime() % 360));
        poseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable.item, ItemDisplayContext.GUI, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
        poseStack.popPose();
    }

    public static class Model extends GeoModel<ChargerBlockEntity> {
        @Override
        public ResourceLocation getModelResource(ChargerBlockEntity object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/charger.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ChargerBlockEntity object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/charger.png");
        }

        @Override
        public ResourceLocation getAnimationResource(ChargerBlockEntity animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/charger.animation.json");
        }
    }
}