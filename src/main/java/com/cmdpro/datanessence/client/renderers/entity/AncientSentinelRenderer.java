package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.AncientSentinel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;


public class AncientSentinelRenderer extends MobRenderer<AncientSentinel, AncientSentinelRenderer.Model<AncientSentinel>> {
    public static final ModelLayerLocation ancientSentinelLocation = new ModelLayerLocation(DataNEssence.locate("ancient_sentinel"), "main");
    public AncientSentinelRenderer(EntityRendererProvider.Context p_272933_) {
        super(p_272933_, new Model<>(p_272933_.bakeLayer(ancientSentinelLocation)), 0.5F);
    }
    @Override
    public ResourceLocation getTextureLocation(AncientSentinel instance) {
        return DataNEssence.locate("textures/entity/ancient_sentinel.png");
    }

    public static class Model<T extends AncientSentinel> extends HierarchicalModel<T> {
        public static AnimationDefinition idle;
        private final ModelPart root;
        private final ModelPart head;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
            this.head = root.getChild("body").getChild("head");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("ancient_sentinel"));
                idle = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
            this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
            pEntity.animState.startIfStopped(pEntity.tickCount);
            this.animate(pEntity.animState, idle, pAgeInTicks, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
