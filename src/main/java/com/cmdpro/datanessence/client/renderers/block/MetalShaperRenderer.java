package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.production.MetalShaper;
import com.cmdpro.datanessence.block.production.MetalShaperBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.common.value.qual.IntVal;

public class MetalShaperRenderer extends DatabankBlockEntityRenderer<MetalShaperBlockEntity> {
    public MetalShaperRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model());
    }

    @Override
    public void render(MetalShaperBlockEntity metalShaper, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Direction facing = metalShaper.getBlockState().getValue(MetalShaper.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);
        ItemStack item = metalShaper.getItemHandler().getStackInSlot(0);

        if (facing.equals(Direction.NORTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.SOUTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.EAST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.WEST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);


        poseStack.pushPose();
        poseStack.translate(0.5d, 0.2d, 0.5d);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, poseStack, pBufferSource, metalShaper.getLevel(), 0);
        poseStack.popPose();

        super.render(metalShaper, pPartialTick, poseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    public static class Model extends DatabankBlockEntityModel<MetalShaperBlockEntity> {
        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("metal_shaper"));
            }
            return model;
        }
        @Override
        public void setupModelPose(MetalShaperBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            if (pEntity.workTime >= 0 && pEntity.workTime <= pEntity.maxWorkTime-15) {
                pEntity.animState.setAnim("lower_press");
            } else {
                pEntity.animState.setAnim("raise_press");
            }
            animate(pEntity.animState);
        }
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/metal_shaper.png");
        }

    }
}
