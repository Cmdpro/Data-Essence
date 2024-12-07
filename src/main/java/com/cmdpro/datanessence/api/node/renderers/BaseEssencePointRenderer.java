package com.cmdpro.datanessence.api.node.renderers;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.block.transmission.EssencePoint;
import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;


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
            for (BlockPos i : pBlockEntity.link) {
                Vec3 target = i.getCenter();
                VertexConsumer vertexConsumer = pBufferSource.getBuffer(DataNEssenceRenderTypes.WIRES);
                ClientRenderingUtil.renderLine(vertexConsumer, pPoseStack, origin, target, pBlockEntity.linkColor());
            }
            pPoseStack.popPose();
        }
        Color color = pBlockEntity.linkColor();
        pBufferSource.getBuffer(getModel().renderType.apply(getTextureLocation()));
        AttachFace face = pBlockEntity.getBlockState().getValue(EssencePoint.FACE);
        Direction facing = pBlockEntity.getBlockState().getValue(EssencePoint.FACING);
        rotateStack(face, facing, pPoseStack);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        pPoseStack.translate(0, 0, -0.15);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(pBlockEntity.getLevel().getLevelData().getGameTime() % 360));
        pPoseStack.scale(0.75F, 0.75F, 0.75F);
        ClientDatabankUtils.renderItemWithColor(pBlockEntity.uniqueUpgrade.getStackInSlot(0), ItemDisplayContext.GUI, false, pPoseStack, pBufferSource, LightTexture.FULL_BRIGHT, pPackedOverlay, color, pBlockEntity.getLevel());
        pPoseStack.popPose();
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        pPoseStack.translate(0, 0, -0.3);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-(pBlockEntity.getLevel().getLevelData().getGameTime() % 360)));
        pPoseStack.scale(0.5F, 0.5F, 0.5F);
        ClientDatabankUtils.renderItemWithColor(pBlockEntity.universalUpgrade.getStackInSlot(0), ItemDisplayContext.GUI, false, pPoseStack, pBufferSource, LightTexture.FULL_BRIGHT, pPackedOverlay, color, pBlockEntity.getLevel());
        pPoseStack.popPose();
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }
    public void rotateStack(AttachFace face, Direction facing, PoseStack poseStack) {
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);
        if (face.equals(AttachFace.CEILING)) {
            poseStack.rotateAround(Axis.XP.rotationDegrees(180), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
        }
        if (face.equals(AttachFace.WALL)) {
            if (facing.equals(Direction.NORTH)) {
                poseStack.rotateAround(Axis.XP.rotationDegrees(-90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if (facing.equals(Direction.SOUTH)) {
                poseStack.rotateAround(Axis.XP.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if (facing.equals(Direction.EAST)) {
                poseStack.rotateAround(Axis.ZP.rotationDegrees(-90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if (facing.equals(Direction.WEST)) {
                poseStack.rotateAround(Axis.ZP.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
        }
    }
    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return AABB.INFINITE;
    }

    public static class Model<T extends BaseEssencePointBlockEntity> extends DatabankBlockEntityModel<T> {
        public static AnimationDefinition idle;
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
            pEntity.animState.startIfStopped((int)getAgeInTicks());
            this.animate(pEntity.animState, idle, 1.0f);
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