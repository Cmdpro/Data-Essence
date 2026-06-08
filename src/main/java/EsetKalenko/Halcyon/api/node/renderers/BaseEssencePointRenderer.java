package EsetKalenko.Halcyon.api.node.renderers;

import EsetKalenko.Halcyon.api.misc.BlockPosNetworks;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.rendering.RenderHandler;
import EsetKalenko.Halcyon.DataNEssence;
import EsetKalenko.Halcyon.api.util.client.ClientRenderingUtil;
import EsetKalenko.Halcyon.block.transmission.EssencePoint;
import EsetKalenko.Halcyon.api.node.block.BaseEssencePointBlockEntity;
import EsetKalenko.Halcyon.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
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
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

import java.awt.*;


public abstract class BaseEssencePointRenderer<T extends BaseEssencePointBlockEntity> extends DatabankBlockEntityRenderer<T> {
    public Model<T> model;
    public RelayModel<T> relayModel;

    public BaseEssencePointRenderer(Model<T> model, RelayModel<T> relayModel) {
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
        BlockPosNetworks network = pBlockEntity.getLevel().getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
        BlockPos blockPos = pBlockEntity.getBlockPos();

        if (network.graph.vertices().contains(blockPos) && !network.graph.outEdges(blockPos).isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 0.5, 0.5);

            ClientSubLevelAccess sublevel = SableCompanion.INSTANCE.getContainingClient(blockPos);
            if (sublevel != null) {
                Quaterniondc sublevelOrientation = sublevel.renderPose().orientation();
                Quaternionf worldOrientation = sublevelOrientation.get(new Quaternionf()).conjugate();
                pPoseStack.mulPose(worldOrientation);
            }
            Vec3 origin = ClientRenderingUtil.transformPosition(blockPos.getCenter());

            pPoseStack.translate(-origin.x, -origin.y, -origin.z);
            for (var edge : network.graph.outEdges(blockPos)) {
                Vec3 target = ClientRenderingUtil.transformPosition(edge.target().getCenter());
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
                }, target.subtract(origin).horizontalDistance() < 0.3 ? 0 : 0.3);
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

    public static class Model<T extends BaseEssencePointBlockEntity> extends DatabankBlockEntityModel<T> {
        public ResourceLocation texture;

        public Model(ResourceLocation texture) {
            this.texture = texture;
        }

        @Override
        public ResourceLocation getTextureLocation() {
            return texture;
        }

        @Override
        public void setupModelPose(BaseEssencePointBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            animate(pEntity.animState);
        }

        @Override
        public DatabankModel getModel() {
            return DatabankModels.models.get(DataNEssence.locate("essence_point"));
        }
    }

    public static class RelayModel<T extends BaseEssencePointBlockEntity> extends Model<T> {

        public RelayModel(ResourceLocation texture) {
            super(texture);
        }

        @Override
        public DatabankModel getModel() {
            return DatabankModels.models.get(DataNEssence.locate("node_relay"));
        }
    }

    @Override
    public boolean shouldRenderOffScreen(BaseEssencePointBlockEntity pBlockEntity) {
        return true;
    }
}