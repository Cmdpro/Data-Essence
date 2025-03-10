package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
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

public class MetalShaperRenderer extends DatabankBlockEntityRenderer<MetalShaperBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("metal_shaper"), "main");

    public MetalShaperRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/metal_shaper.png");
    }

    @Override
    public void render(MetalShaperBlockEntity metalShaper, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Direction facing = metalShaper.getBlockState().getValue(MetalShaper.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);
        ItemStack item = metalShaper.getItemHandler().getStackInSlot(0);

        if (facing.equals(Direction.NORTH)) {
            poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        }
        if (facing.equals(Direction.SOUTH)) {
            poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        }
        if (facing.equals(Direction.EAST)) {
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        }
        if (facing.equals(Direction.WEST)) {
            poseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        }

        poseStack.pushPose();
        poseStack.translate(0.5d, 0.2d, 0.5d);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GUI, pPackedLight, pPackedOverlay, poseStack, pBufferSource, metalShaper.getLevel(), 0);
        poseStack.popPose();

        super.render(metalShaper, pPartialTick, poseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    public static class Model extends DatabankBlockEntityModel<MetalShaperBlockEntity> {
        public static DatabankEntityModel model;
        public static AnimationDefinition idle;
        public static AnimationDefinition raisePress;
        public static AnimationDefinition lowerPress;
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("metal_shaper"));
                idle = model.animations.get("idle").createAnimationDefinition();
                raisePress = model.animations.get("raise press").createAnimationDefinition();
                lowerPress = model.animations.get("lower press").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }

        public void setupAnim(MetalShaperBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            AnimationDefinition anim;
            if (pEntity.workTime >= 0 && pEntity.workTime <= pEntity.maxWorkTime-15) {
                anim = lowerPress;
            } else {
                anim = raisePress;
            }
            if (anim != pEntity.anim) {
                this.updateAnim(pEntity, pEntity.animState, anim);
            }
            this.animate(pEntity.animState, pEntity.anim, 1.0f);
        }
        protected void updateAnim(MetalShaperBlockEntity entity, AnimationState pAnimationState, AnimationDefinition pAnimationDefinition) {
            if (entity.anim != pAnimationDefinition) {
                pAnimationState.stop();
                pAnimationState.start((int)getAgeInTicks());
                entity.anim = pAnimationDefinition;
            }
        }
        @Override
        public ModelPart root() {
            return root;
        }
    }
}
