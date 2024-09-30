package com.cmdpro.datanessence.renderers.entity;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SnifferRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class AncientSentinelRenderer extends MobRenderer<AncientSentinel, AncientSentinelRenderer.Model<AncientSentinel>> {
    public static final ModelLayerLocation ancientSentinelLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "ancient_sentinel"), "main");
    public AncientSentinelRenderer(EntityRendererProvider.Context p_272933_) {
        super(p_272933_, new Model<>(p_272933_.bakeLayer(ancientSentinelLocation)), 0.5F);
    }
    @Override
    public ResourceLocation getTextureLocation(AncientSentinel instance) {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/entity/ancient_sentinel.png");
    }

    public static class Model<T extends AncientSentinel> extends HierarchicalModel<T> {
        public static AnimationDefinition idle;
        public static final AnimationState animState = new AnimationState();
        private final ModelPart root;
        private final ModelPart head;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
            this.head = root.getChild("body").getChild("head");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "ancient_sentinel"));
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
            animState.startIfStopped(pEntity.tickCount);
            this.animate(animState, idle, pAgeInTicks, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
