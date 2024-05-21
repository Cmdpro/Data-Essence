package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.EssencePoint;
import com.cmdpro.datanessence.block.entity.EssencePointBlockEntity;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import javax.xml.crypto.Data;
import java.awt.*;


public class EssencePointRenderer extends GeoBlockRenderer<EssencePointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public EssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
        renderDispatcher = rendererProvider.getEntityRenderer();
    }

    @Override
    public void postRender(PoseStack poseStack, EssencePointBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public boolean shouldRender(EssencePointBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return true;
    }

    @Override
    public void preRender(PoseStack poseStack, EssencePointBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (animatable.link != null) {
            poseStack.pushPose();
            VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
            LodestoneRenderType renderType = LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyAndCache(new ResourceLocation(DataNEssence.MOD_ID, "textures/vfx/beam.png"));
            poseStack.translate(-animatable.getBlockPos().getX(), -animatable.getBlockPos().getY(), -animatable.getBlockPos().getZ());
            builder.setColor(Color.MAGENTA)
                    .setRenderType(renderType)
                    .renderBeam(poseStack.last().pose(), animatable.getBlockPos().getCenter(), animatable.link.getCenter(), 0.025f);
            poseStack.translate(animatable.getBlockPos().getX(), animatable.getBlockPos().getY(), animatable.getBlockPos().getZ());
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

    @Override
    public RenderType getRenderType(EssencePointBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    public static class Model extends GeoModel<EssencePointBlockEntity> {
        @Override
        public ResourceLocation getModelResource(EssencePointBlockEntity object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essencepoint.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(EssencePointBlockEntity object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/essencepoint.png");
        }

        @Override
        public ResourceLocation getAnimationResource(EssencePointBlockEntity animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essencepoint.animation.json");
        }
    }
}