package com.cmdpro.datanessence.renderers.layer;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.shaders.DataNEssenceRenderTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.definitions.SnifferAnimation;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class WingsLayer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelLayerLocation wingsLocation = new ModelLayerLocation(new ResourceLocation(DataNEssence.MOD_ID, "wings"), "main");
    public static final ResourceLocation wingsTexture = new ResourceLocation(DataNEssence.MOD_ID, "textures/entity/wings.png");
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
                    pBuffer, RenderType.armorCutoutNoCull(wingsTexture), false, false
            );
            this.wingsModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            pPoseStack.popPose();
        }
    }
    public class WingsModel<T extends Player> extends HierarchicalModel<T> {
        public static final AnimationState animState = new AnimationState();
        public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(2.0F).looping()
                .addAnimation("left_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -30.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(-1.3707F, -32.4654F, 15.4578F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -30.0F, 12.5F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_wing_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(14.0657F, -20.8813F, -87.6045F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(23.2427F, -18.9743F, -92.5215F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(14.0657F, -20.8813F, -87.6045F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_wingtip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 62.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 57.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 62.5F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 30.0F, -12.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(-1.4813F, 32.4677F, -15.4607F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 30.0F, -12.5F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_wing_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0853F, 22.6277F, 87.9132F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(18.7009F, 18.7123F, 92.1768F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(7.0853F, 22.6277F, 87.9132F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_wingtip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -62.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -57.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -62.5F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();

        public static final AnimationDefinition fly = AnimationDefinition.Builder.withLength(0.5F).looping()
                .addAnimation("left_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-26.0334F, -50.3315F, 32.3977F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(-17.4048F, -30.226F, 18.5371F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(-26.0334F, -50.3315F, 32.3977F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_wing_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-0.8704F, 4.9238F, -25.0374F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(-0.8974F, 14.9227F, -25.1938F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(-0.8704F, 4.9238F, -25.0374F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_wingtip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 12.5F, -12.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 25.0F, -12.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 12.5F, -12.5F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-26.0334F, 50.3315F, -32.3977F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(-20.0771F, 28.6182F, -23.9734F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(-26.0334F, 50.3315F, -32.3977F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_wing_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-0.8704F, -4.9238F, 25.0374F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(-0.8974F, -14.9227F, 25.1938F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(-0.8704F, -4.9238F, 25.0374F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_wingtip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -12.5F, 12.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, -25.0F, 12.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, -12.5F, 12.5F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();
        private final ModelPart root;
        private final ModelPart wings;
        private final ModelPart left_wing;
        private final ModelPart left_wing_upper_arm;
        private final ModelPart left_wing_lower_arm;
        private final ModelPart left_wingtip;
        private final ModelPart right_wing;
        private final ModelPart right_wing_upper_arm;
        private final ModelPart right_wing_lower_arm;
        private final ModelPart right_wingtip;

        public WingsModel(ModelPart rootPart) {
            this.root = rootPart.getChild("root");
            this.wings = root.getChild("wings");
            this.left_wing = wings.getChild("left_wing");
            this.left_wing_upper_arm = left_wing.getChild("left_wing_upper_arm");
            this.left_wing_lower_arm = left_wing_upper_arm.getChild("left_wing_lower_arm");
            this.left_wingtip = left_wing_lower_arm.getChild("left_wingtip");
            this.right_wing = wings.getChild("right_wing");
            this.right_wing_upper_arm = right_wing.getChild("right_wing_upper_arm");
            this.right_wing_lower_arm = right_wing_upper_arm.getChild("right_wing_lower_arm");
            this.right_wingtip = right_wing_lower_arm.getChild("right_wingtip");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(1.0F, 23.0F, 2.0F));

            PartDefinition wings = root.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

            PartDefinition left_wing = wings.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

            PartDefinition left_wing_upper_arm = left_wing.addOrReplaceChild("left_wing_upper_arm", CubeListBuilder.create().texOffs(16, 39).addBox(0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition left_membrane_start_r1 = left_wing_upper_arm.addOrReplaceChild("left_membrane_start_r1", CubeListBuilder.create().texOffs(36, 18).addBox(-4.0F, -1.0F, 1.0F, 9.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 2.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

            PartDefinition left_wing_lower_arm = left_wing_upper_arm.addOrReplaceChild("left_wing_lower_arm", CubeListBuilder.create().texOffs(36, 25).addBox(-1.0F, -1.0F, 0.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(28, 28).addBox(-6.0F, 1.0F, 0.75F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 0.0F, 0.0F));

            PartDefinition left_wingtip = left_wing_lower_arm.addOrReplaceChild("left_wingtip", CubeListBuilder.create(), PartPose.offset(9.0F, 0.0F, 0.0F));

            PartDefinition left_wingtip_membrane_r1 = left_wingtip.addOrReplaceChild("left_wingtip_membrane_r1", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -1.0F, 1.0F, 18.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

            PartDefinition finger_r1 = left_wingtip.addOrReplaceChild("finger_r1", CubeListBuilder.create().texOffs(36, 39).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0436F, 0.0F, 0.0873F));

            PartDefinition finger_r2 = left_wingtip.addOrReplaceChild("finger_r2", CubeListBuilder.create().texOffs(36, 2).addBox(-0.2936F, -0.2929F, -0.0308F, 17.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, 0.0F, -0.0436F, 0.7854F));

            PartDefinition finger_r3 = left_wingtip.addOrReplaceChild("finger_r3", CubeListBuilder.create().texOffs(36, 6).addBox(-1.0F, -1.0F, 0.0F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

            PartDefinition right_wing = wings.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition right_wing_upper_arm = right_wing.addOrReplaceChild("right_wing_upper_arm", CubeListBuilder.create().texOffs(0, 39).addBox(-7.0F, -1.0F, 0.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition right_membrane_start_r1 = right_wing_upper_arm.addOrReplaceChild("right_membrane_start_r1", CubeListBuilder.create().texOffs(36, 11).addBox(-5.0F, -1.0F, 1.0F, 9.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

            PartDefinition right_wing_lower_arm = right_wing_upper_arm.addOrReplaceChild("right_wing_lower_arm", CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, 1.0F, 0.75F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
                    .texOffs(36, 8).addBox(-9.0F, -1.0F, 0.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.0F, 0.0F));

            PartDefinition right_wingtip = right_wing_lower_arm.addOrReplaceChild("right_wingtip", CubeListBuilder.create(), PartPose.offset(-9.0F, 0.0F, 0.0F));

            PartDefinition finger_r4 = right_wingtip.addOrReplaceChild("finger_r4", CubeListBuilder.create().texOffs(36, 4).addBox(-14.0F, -1.0F, 0.0F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

            PartDefinition finger_r5 = right_wingtip.addOrReplaceChild("finger_r5", CubeListBuilder.create().texOffs(36, 0).addBox(-16.7064F, -0.2929F, -0.0308F, 17.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0F, 0.0436F, -0.7854F));

            PartDefinition finger_r6 = right_wingtip.addOrReplaceChild("finger_r6", CubeListBuilder.create().texOffs(32, 39).addBox(0.0F, 0.0F, 0.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0436F, 0.0F, -0.0873F));

            PartDefinition right_wingtip_membrane_r1 = right_wingtip.addOrReplaceChild("right_wingtip_membrane_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-15.0F, -1.0F, 1.0F, 18.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 128, 128);
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
