package com.cmdpro.datanessence.renderers.layer;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
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

        public HornsModel(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }

        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "horns"));
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
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
