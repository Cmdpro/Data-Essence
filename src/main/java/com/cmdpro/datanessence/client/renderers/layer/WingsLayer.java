package com.cmdpro.datanessence.client.renderers.layer;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class WingsLayer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelLayerLocation wingsLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "wings"), "main");
    public static final ResourceLocation wingsTexture = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/entity/wings.png");
    private final WingsModel<T> wingsModel;
    public WingsLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.wingsModel = new WingsModel<>(pModelSet.bakeLayer(wingsLocation));
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.hasData(AttachmentTypeRegistry.HAS_WINGS) && pLivingEntity.getData(AttachmentTypeRegistry.HAS_WINGS)) {
            pPoseStack.pushPose();
            this.wingsModel.root().getAllParts().forEach(ModelPart::resetPose);
            this.wingsModel.root.copyFrom(this.getParentModel().body);
            this.wingsModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(
                    pBuffer, RenderType.armorCutoutNoCull(wingsTexture), false
            );
            this.wingsModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();
        }
    }
    public class WingsModel<T extends Player> extends HierarchicalModel<T> {
        public AnimationState animState = new AnimationState();
        public static AnimationDefinition idle;

        public static AnimationDefinition fly;
        private final ModelPart root;

        public WingsModel(ModelPart rootPart) {
            this.root = rootPart.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "wings"));
                fly = model.animations.get("fly").createAnimationDefinition();
                idle = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }
        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            animState.startIfStopped(pEntity.tickCount);
            if (pEntity.getAbilities().flying) {
                this.animate(animState, fly, pAgeInTicks, 1.0f);
            } else {
                this.animate(animState, idle, pAgeInTicks, 1.0f);
            }
        }
        private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

        @Override
        public ModelPart root() {
            return root;
        }

        protected void animate(AnimationState pAnimationState, AnimationDefinition pAnimationDefinition, float pAgeInTicks, float pSpeed) {
            pAnimationState.updateTime(pAgeInTicks, pSpeed);
            pAnimationState.ifStarted(p_233392_ -> KeyframeAnimations.animate(this, pAnimationDefinition, p_233392_.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
        }
    }
}
