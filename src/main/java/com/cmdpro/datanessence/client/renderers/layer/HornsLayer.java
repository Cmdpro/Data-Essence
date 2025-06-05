package com.cmdpro.datanessence.client.renderers.layer;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.ModelPose;
import com.cmdpro.databank.model.entity.DatabankEntityModel;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class HornsLayer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation hornsTexture = DataNEssence.locate("textures/entity/horns.png");
    private final HornsModel hornsModel;
    public HornsLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.hornsModel = new HornsModel();
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.hasData(AttachmentTypeRegistry.HAS_HORNS) && pLivingEntity.getData(AttachmentTypeRegistry.HAS_HORNS)) {
            pPoseStack.pushPose();
            this.hornsModel.setupPose(pLivingEntity, pPartialTick, this.getParentModel().head);
            this.hornsModel.render(pLivingEntity, pPartialTick, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            pPoseStack.popPose();
        }
    }
    public class HornsModel extends DatabankEntityModel<Player> {
        public DatabankModel model;

        @Override
        public RenderType getRenderType(Player obj) {
            return RenderType.armorCutoutNoCull(hornsTexture);
        }

        @Override
        public ResourceLocation getTextureLocation() {
            return hornsTexture;
        }

        @Override
        public void setupModelPose(Player player, float partialTick) {
            modelPose = getModel().createModelPose();
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
                model = DatabankModels.models.get(DataNEssence.locate("horns"));
            }
            return model;
        }
    }
}
