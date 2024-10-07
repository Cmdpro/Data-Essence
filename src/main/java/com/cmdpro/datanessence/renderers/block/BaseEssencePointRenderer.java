package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.block.processing.AutoFabricatorBlockEntity;
import com.cmdpro.datanessence.block.transmission.EssencePoint;
import com.cmdpro.datanessence.api.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.block.transmission.EssencePointBlockEntity;
import com.cmdpro.datanessence.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseEssencePointRenderer<T extends BaseEssencePointBlockEntity> extends DatabankBlockEntityRenderer<T> {
    public BaseEssencePointRenderer(DatabankBlockEntityModel<T> model) {
        super(model);
    }
    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.link != null) {
            Vec3 pos = pBlockEntity.getBlockPos().getCenter();
            pPoseStack.pushPose();
            pPoseStack.translate(-pos.x, -pos.y, -pos.z);
            pPoseStack.translate(0.5, 0.5, 0.5);
            Vec3 origin = pBlockEntity.getBlockPos().getCenter();
            Vec3 target = pBlockEntity.link.getCenter();
            VertexConsumer vertexConsumer = pBufferSource.getBuffer(DataNEssenceRenderTypes.WIRES);
            ClientRenderingUtil.renderLine(vertexConsumer, pPoseStack, origin, target, pBlockEntity.linkColor());
            pPoseStack.popPose();
        }
        pBufferSource.getBuffer(getModel().renderType.apply(getTextureLocation()));
        AttachFace face = pBlockEntity.getBlockState().getValue(EssencePoint.FACE);
        Direction facing = pBlockEntity.getBlockState().getValue(EssencePoint.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);
        if (face.equals(AttachFace.CEILING)) {
            pPoseStack.rotateAround(Axis.XP.rotationDegrees(180), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
        }
        if (face.equals(AttachFace.WALL)) {
            if (facing.equals(Direction.NORTH)) {
                pPoseStack.rotateAround(Axis.XP.rotationDegrees(-90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if (facing.equals(Direction.SOUTH)) {
                pPoseStack.rotateAround(Axis.XP.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if (facing.equals(Direction.EAST)) {
                pPoseStack.rotateAround(Axis.ZP.rotationDegrees(-90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if (facing.equals(Direction.WEST)) {
                pPoseStack.rotateAround(Axis.ZP.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
        }
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }
    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return AABB.INFINITE;
    }

    public static class Model<T extends BaseEssencePointBlockEntity> extends DatabankBlockEntityModel<T> {
        public static AnimationDefinition idle;
        public static final AnimationState animState = new AnimationState();
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "essence_point"));
                idle = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(T pEntity) {
            animState.startIfStopped((int)getAgeInTicks());
            this.animate(animState, idle, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }

    @Override
    public boolean shouldRenderOffScreen(BaseEssencePointBlockEntity pBlockEntity) {
        return true;
    }
}