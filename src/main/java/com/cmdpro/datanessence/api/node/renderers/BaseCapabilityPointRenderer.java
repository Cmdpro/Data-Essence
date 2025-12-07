package com.cmdpro.datanessence.api.node.renderers;

import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.block.transmission.EssencePoint;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;


public abstract class BaseCapabilityPointRenderer<T extends BaseCapabilityPointBlockEntity> extends DatabankBlockEntityRenderer<T> {
    public Model<T> model;
    public RelayModel<T> relayModel;

    public BaseCapabilityPointRenderer(Model<T> model, RelayModel<T> relayModel) {
        super(model);
        this.model = model;
        this.relayModel = relayModel;
    }

    @Override
    public DatabankBlockEntityModel<T> getModel(T pBlockEntity) {
        return pBlockEntity.isRelay ? relayModel : model;
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.link != null) {
            BlockPos blockPos = pBlockEntity.getBlockPos();
            Vec3 pos = blockPos.getCenter();
            pPoseStack.pushPose();
            pPoseStack.translate(-pos.x, -pos.y, -pos.z);
            pPoseStack.translate(0.5, 0.5, 0.5);
            Vec3 origin = blockPos.getCenter();
            for (BlockPos i : pBlockEntity.link) {
                Vec3 target = i.getCenter();
                VertexConsumer vertexConsumer = RenderHandler.createBufferSource().getBuffer(DataNEssenceRenderTypes.WIRES);
                Color segColor1 = pBlockEntity.linkColor()[0];
                Color segColor2 = pBlockEntity.linkColor()[1];
                Color segColor3 = ColorUtil.blendColors(segColor1, segColor2, 0.35f);
                float ticks = (Minecraft.getInstance().level.getGameTime() % 8)+pPartialTick;
                int currentSeg = (int)(ticks % 8);
                ClientRenderingUtil.renderLine(vertexConsumer, pPoseStack, origin, target, (seg) -> {
                    if (7-(seg % 8) == getSegWithOffset(currentSeg, -2) || 7-(seg % 8) == getSegWithOffset(currentSeg, 1) % 8) {
                        return segColor3;
                    }
                    return 7-(seg % 8) == currentSeg || 7-(seg % 8) == getSegWithOffset(currentSeg, -1) ? segColor1 : segColor2;
                }, blockPos.getX() == i.getX() && blockPos.getZ() == i.getZ() ? 0 : 0.3);
            }
            pPoseStack.popPose();
        }
        Color color = pBlockEntity.linkColor()[0];
        pBufferSource.getBuffer(getModel(pBlockEntity).getRenderType(pBlockEntity));
        AttachFace face = pBlockEntity.getBlockState().getValue(EssencePoint.FACE);
        Direction facing = pBlockEntity.getBlockState().getValue(EssencePoint.FACING);
        rotateStack(face, facing, pPoseStack);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        pPoseStack.translate(0, 0, -0.15);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees((pBlockEntity.getLevel().getLevelData().getGameTime() % 360)+pPartialTick));
        pPoseStack.scale(0.75F, 0.75F, 0.75F);
        RenderingUtil.renderItemWithColor(pBlockEntity.uniqueUpgrade.getStackInSlot(0), ItemDisplayContext.FIXED, false, pPoseStack, pBufferSource, LightTexture.FULL_BRIGHT, pPackedOverlay, color, pBlockEntity.getLevel());
        pPoseStack.popPose();
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        pPoseStack.translate(0, 0, -0.3);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-((pBlockEntity.getLevel().getLevelData().getGameTime() % 360)+pPartialTick)));
        pPoseStack.scale(0.5F, 0.5F, 0.5F);
        RenderingUtil.renderItemWithColor(pBlockEntity.universalUpgrade.getStackInSlot(0), ItemDisplayContext.FIXED, false, pPoseStack, pBufferSource, LightTexture.FULL_BRIGHT, pPackedOverlay, color, pBlockEntity.getLevel());
        pPoseStack.popPose();
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    private int getSegWithOffset(int seg, int offset) {
        int segment = seg+offset;
        if (segment < 0) {
            segment = segment + 8;
        }
        segment = segment % 8;
        return segment;
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

    public static class Model<T extends BaseCapabilityPointBlockEntity> extends DatabankBlockEntityModel<T> {
        public ResourceLocation texture;

        public Model(ResourceLocation texture) {
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation() {
            return texture;
        }

        @Override
        public void setupModelPose(BaseCapabilityPointBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            animate(pEntity.animState);
        }

        public DatabankModel getModel() {
            return DatabankModels.models.get(DataNEssence.locate("essence_point"));
        }
    }

    public static class RelayModel<T extends BaseCapabilityPointBlockEntity> extends Model<T> {

        public RelayModel(ResourceLocation texture) {
            super(texture);
        }

        @Override
        public DatabankModel getModel() {
            return DatabankModels.models.get(DataNEssence.locate("node_relay"));
        }
    }
}