package com.cmdpro.datanessence.api.pearlnetwork;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public interface PearlNetworkBlockRenderHelper {
    default void renderPearlConnections(PearlNetworkBlockEntity entity, PoseStack stack) {
        if (entity.link != null) {
            Vec3 pos = entity.getBlockPos().getCenter();
            stack.pushPose();
            stack.translate(-pos.x, -pos.y, -pos.z);
            stack.translate(0.5, 0.5, 0.5);
            Vec3 origin = entity.getBlockPos().getCenter();
            for (BlockPos i : entity.link) {
                Vec3 currentPos = origin;
                Vec3 target = i.getCenter();
                VertexConsumer vertexConsumer = RenderHandler.createBufferSource().getBuffer(DataNEssenceRenderTypes.WIRES);
                Color color = PearlNetworkBlock.getColor();
                Vec3 normal = currentPos.subtract(target).normalize();
                vertexConsumer.addVertex(stack.last(), (float) currentPos.x, (float) currentPos.y, (float) currentPos.z).setColor(color.getRGB()).setNormal(stack.last(), (float) normal.x, (float) normal.y, (float) normal.z);
                currentPos = target;
                vertexConsumer.addVertex(stack.last(), (float) currentPos.x, (float) currentPos.y, (float) currentPos.z).setColor(color.getRGB()).setNormal(stack.last(), (float) normal.x, (float) normal.y, (float) normal.z);
            }
            stack.popPose();
        }
    }
}
