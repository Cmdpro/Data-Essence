package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.BlockEntityKeyframeAnimations;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.auxiliary.ChargerBlockEntity;
import com.cmdpro.datanessence.block.processing.AutoFabricatorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;


public class ChargerRenderer extends DatabankBlockEntityRenderer<ChargerBlockEntity> {
    public static final ModelLayerLocation chargerLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "charger"), "main");
    public ChargerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(chargerLocation)));
    }

    @Override
    public void render(ChargerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.5D, 0.5D);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getLevel().getLevelData().getGameTime() % 360));
        pPoseStack.scale(0.25F, 0.25F, 0.25F);
        Minecraft.getInstance().getItemRenderer().renderStatic(pBlockEntity.item, ItemDisplayContext.GUI, pPackedLight, pPackedOverlay, pPoseStack, pBufferSource, pBlockEntity.getLevel(), 0);
        pPoseStack.popPose();
    }
    @Override
    public ResourceLocation getTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/charger.png");
    }

    public static class Model extends DatabankBlockEntityModel<ChargerBlockEntity> {
        public static AnimationDefinition idle_empty;
        public static AnimationDefinition orb_spin;
        public static AnimationDefinition orb_rise;
        public static AnimationDefinition extend_exciters;
        public static AnimationDefinition retract_exciters;
        public static AnimationDefinition idle_exciters_out;
        public static AnimationDefinition orb_fall;
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "charger"));
                idle_empty = model.animations.get("idle_empty").createAnimationDefinition();
                orb_spin = model.animations.get("orb_spin").createAnimationDefinition();
                retract_exciters = model.animations.get("retract_exciters").createAnimationDefinition();
                idle_exciters_out = model.animations.get("idle_exciters_out").createAnimationDefinition();
                extend_exciters = model.animations.get("extend_exciters").createAnimationDefinition();
                orb_rise = model.animations.get("orb_rise").createAnimationDefinition();
                orb_fall = model.animations.get("orb_fall").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(ChargerBlockEntity pEntity) {
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            AnimationDefinition tempAnim = null;
            AnimationDefinition anim = pEntity.anim;
            if (!pEntity.item.isEmpty()) {
                if (anim == (idle_empty)) {
                    tempAnim = extend_exciters;
                }
                if (anim == (extend_exciters) && BlockEntityKeyframeAnimations.isDone(anim, pEntity.animState.getAccumulatedTime())) {
                    tempAnim = idle_exciters_out;
                }
                if (pEntity.charging) {
                    if (anim == (idle_exciters_out)) {
                        tempAnim = orb_rise;
                    }
                    if (anim == (orb_rise) && BlockEntityKeyframeAnimations.isDone(anim, pEntity.animState.getAccumulatedTime())) {
                        tempAnim = orb_spin;
                    }
                } else {
                    if (anim == (orb_spin) || anim == (orb_rise)) {
                        tempAnim = orb_fall;
                    }
                    if (anim == (orb_fall) && BlockEntityKeyframeAnimations.isDone(anim, pEntity.animState.getAccumulatedTime())) {
                        tempAnim = idle_exciters_out;
                    }
                }
            } else {
                if (anim == (orb_spin) || anim == (orb_rise)) {
                    tempAnim = orb_fall;
                } else if (anim == (idle_exciters_out) || anim == (extend_exciters) || (anim == (orb_fall) && BlockEntityKeyframeAnimations.isDone(anim, pEntity.animState.getAccumulatedTime()))) {
                    tempAnim = retract_exciters;
                } else if ((anim != (retract_exciters) && anim != (orb_fall)) || BlockEntityKeyframeAnimations.isDone(anim, pEntity.animState.getAccumulatedTime())) {
                    tempAnim = idle_empty;
                }
            }
            if (tempAnim != null) {
                this.updateAnim(pEntity, pEntity.animState, tempAnim);
                this.animate(pEntity.animState, tempAnim);
            } else if (anim != null) {
                this.updateAnim(pEntity, pEntity.animState, anim);
                this.animate(pEntity.animState, anim);
            }
        }

        protected void updateAnim(ChargerBlockEntity entity, AnimationState pAnimationState, AnimationDefinition pAnimationDefinition) {
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