package com.cmdpro.datanessence.client.renderers.layer;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.ModelPose;
import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.model.entity.DatabankEntityModel;
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
    public static final ResourceLocation wingsTexture = DataNEssence.locate("textures/entity/wings.png");
    private final WingsModel wingsModel;
    public WingsLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.wingsModel = new WingsModel();
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.hasData(AttachmentTypeRegistry.HAS_WINGS) && pLivingEntity.getData(AttachmentTypeRegistry.HAS_WINGS)) {
            pPoseStack.pushPose();
            this.wingsModel.setupPose(pLivingEntity, pPartialTick, this.getParentModel().body);
            this.wingsModel.render(pLivingEntity, pPartialTick, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF, false);
            pPoseStack.popPose();
        }
    }
    public class WingsModel extends DatabankEntityModel<Player> {
        public DatabankAnimationState animState = new DatabankAnimationState("idle")
                .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
                .addAnim(new DatabankAnimationReference("fly", (state, anim) -> {}, (state, anim) -> {}));
        public DatabankModel model;

        @Override
        public RenderType getRenderType(net.minecraft.world.entity.player.Player obj) {
            return RenderType.armorCutoutNoCull(wingsTexture);
        }

        @Override
        public ResourceLocation getTextureLocation() {
            return wingsTexture;
        }

        @Override
        public void setupModelPose(Player player, float partialTick) {
            if (player.getAbilities().flying) {
                animState.setAnim("fly");
            } else {
                animState.setAnim("idle");
            }
            animate(animState);
        }
        public void setupPose(Player player, float partialTick, ModelPart connectedTo) {
            setupModelPose(player, partialTick);
            ModelPose.ModelPosePart root = modelPose.stringToPart.get("root");
            root.scale = new Vector3f(connectedTo.xScale, connectedTo.yScale, connectedTo.zScale);
            root.rotation = new Vector3f(connectedTo.xRot, connectedTo.yRot, connectedTo.zRot);
            root.pos = new Vector3f(connectedTo.x, connectedTo.y, connectedTo.z);
        }

        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("wings"));
                animState.updateAnimDefinitions(getModel());
            }
            return model;
        }
    }
}
