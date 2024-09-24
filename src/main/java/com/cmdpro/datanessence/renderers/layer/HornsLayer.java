package com.cmdpro.datanessence.renderers.layer;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
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

public class HornsLayer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelLayerLocation hornsLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "horns"), "main");
    public static final ResourceLocation hornsTexture = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/entity/horns.png");
    private final HornsModel<T> hornsModel;
    public HornsLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.hornsModel = new HornsModel<>(pModelSet.bakeLayer(hornsLocation));
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.hasData(AttachmentTypeRegistry.HAS_HORNS) && pLivingEntity.getData(AttachmentTypeRegistry.HAS_HORNS)) {
            pPoseStack.pushPose();
            this.hornsModel.root.copyFrom(this.getParentModel().head);
            this.hornsModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(
                    pBuffer, RenderType.armorCutoutNoCull(hornsTexture), false
            );
            this.hornsModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();
        }
    }
    public class HornsModel<T extends Player> extends HierarchicalModel<T> {
        private final ModelPart root;
        private final ModelPart horns;

        public HornsModel(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
            this.horns = root.getChild("horns");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition horns = root.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 1.0F));

            PartDefinition cube_r1 = horns.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -1.85F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.5F, 0.75F, -0.4702F, -0.1001F, -0.1942F));

            PartDefinition cube_r2 = horns.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 17).addBox(-1.0F, -1.85F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -8.5F, 0.75F, -0.4702F, 0.1001F, 0.1942F));

            PartDefinition cube_r3 = horns.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 0).addBox(-0.85F, -2.1F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.75F, -6.5F, 1.0F, 0.2535F, 0.1095F, -0.0848F));

            PartDefinition cube_r4 = horns.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 12).addBox(-1.15F, -2.1F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.75F, -6.5F, 1.0F, 0.2535F, -0.1095F, 0.0848F));

            PartDefinition cube_r5 = horns.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -2.05F, -1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.25F, -7.0F, -2.5F, -0.2421F, -0.2399F, -0.0868F));

            PartDefinition cube_r6 = horns.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.05F, -1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.25F, -7.0F, -2.5F, -0.2421F, 0.2399F, 0.0868F));

            PartDefinition cube_r7 = horns.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(10, 12).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -7.5F, -4.0F, -0.215F, -0.0376F, -0.1705F));

            PartDefinition cube_r8 = horns.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(12, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -7.5F, -4.0F, -0.215F, 0.0376F, 0.1705F));

            return LayerDefinition.create(meshdefinition, 32, 32);
        }

        @Override
        public ModelPart root() {
            return root;
        }

        @Override
        public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        }
    }
}
