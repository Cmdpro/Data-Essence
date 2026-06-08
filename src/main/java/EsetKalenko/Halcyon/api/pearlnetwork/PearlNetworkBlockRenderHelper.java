package EsetKalenko.Halcyon.api.pearlnetwork;

import EsetKalenko.Halcyon.api.misc.BlockPosNetworks;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import com.cmdpro.databank.rendering.RenderHandler;
import EsetKalenko.Halcyon.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public interface PearlNetworkBlockRenderHelper {
    default void renderPearlConnections(PearlNetworkBlockEntity entity, PoseStack stack) {
        BlockPosNetworks network = entity.getLevel().getData(AttachmentTypeRegistry.ENDER_PEARL_NETWORKS);
        BlockPos blockPos = entity.getBlockPos();

        if (network.graph.vertices().contains(blockPos) && !network.graph.outEdges(blockPos).isEmpty()) {
            Vec3 pos = blockPos.getCenter();
            stack.pushPose();
            stack.translate(-pos.x, -pos.y, -pos.z);
            stack.translate(0.5, 0.5, 0.5);
            Vec3 origin = entity.getBlockPos().getCenter().add(entity.getLinkShift());
            for (var edge : network.graph.outEdges(blockPos)) {
                BlockEntity otherEnt = entity.getLevel().getBlockEntity(edge.target());
                Vec3 target = edge.target().getCenter();
                if (otherEnt instanceof PearlNetworkBlockEntity ent) {
                    target = target.add(ent.getLinkShift());
                }
                VertexConsumer vertexConsumer = RenderHandler.createBufferSource().getBuffer(DataNEssenceRenderTypes.WIRES);
                Color color = PearlNetworkBlock.getColor();
                Vec3 normal = origin.subtract(target).normalize();
                vertexConsumer.addVertex(stack.last(), (float) origin.x, (float) origin.y, (float) origin.z).setColor(color.getRGB()).setNormal(stack.last(), (float) normal.x, (float) normal.y, (float) normal.z);
                vertexConsumer.addVertex(stack.last(), (float) target.x, (float) target.y, (float) target.z).setColor(color.getRGB()).setNormal(stack.last(), (float) normal.x, (float) normal.y, (float) normal.z);
            }
            stack.popPose();
        }
    }
}
