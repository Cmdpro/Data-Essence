package com.cmdpro.datanessence.renderers.layer;

import com.cmdpro.datanessence.DataNEssence;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
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

public class TailLayer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelLayerLocation tailLocation = new ModelLayerLocation(new ResourceLocation(DataNEssence.MOD_ID, "tail"), "main");
    public static final ResourceLocation tailTexture = new ResourceLocation(DataNEssence.MOD_ID, "textures/entity/tail.png");
    private final TailModel<T> tailModel;
    public TailLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.tailModel = new TailModel<>(pModelSet.bakeLayer(tailLocation));
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        pPoseStack.pushPose();
        this.tailModel.root().getAllParts().forEach(ModelPart::resetPose);
        this.tailModel.root.copyFrom(this.getParentModel().body);
        this.tailModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(
                pBuffer, RenderType.armorCutoutNoCull(tailTexture), false, false
        );
        this.tailModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
    public class TailModel<T extends Player> extends HierarchicalModel<T> {
        public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(2.0F).looping()
                .addAnimation("root", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("part1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("part2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();
        public static final AnimationState animState = new AnimationState();
        private final ModelPart root;
        private final ModelPart tail;
        private final ModelPart part1;
        private final ModelPart part2;

        public TailModel(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
            this.tail = root.getChild("tail");
            this.part1 = tail.getChild("part1");
            this.part2 = part1.getChild("part2");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 3.0F));

            PartDefinition part1 = tail.addOrReplaceChild("part1", CubeListBuilder.create().texOffs(0, 11).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 5.0F));

            PartDefinition part2 = part1.addOrReplaceChild("part2", CubeListBuilder.create().texOffs(12, 14).addBox(-1.5F, -0.5F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -0.5F, 6.0F));

            return LayerDefinition.create(meshdefinition, 32, 32);
        }
        public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            animState.startIfStopped(pEntity.tickCount);
            this.animate(animState, idle, pAgeInTicks, 1.0f);
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
