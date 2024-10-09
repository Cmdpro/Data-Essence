package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRenderingUtil extends com.cmdpro.datanessence.api.util.client.ClientProgressionUtil {
    public static void renderLockedSlotBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y, NonNullList<Slot> slots) {
        for (Slot i : slots) {
            if (i instanceof SlotItemHandler handlerSlot) {
                if (handlerSlot.getItemHandler() instanceof LockableItemHandler lockable) {
                    if (i.getItem().isEmpty()) {
                        if (lockable.lockedSlots.containsKey(i.getContainerSlot())) {
                            pGuiGraphics.pose().pushPose();
                            pGuiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                            pGuiGraphics.renderFakeItem(lockable.lockedSlots.get(i.getContainerSlot()), (int)((x + i.x)/0.5f), (int)((y + i.y)/0.5f));
                            pGuiGraphics.pose().popPose();
                        }
                    }
                }
            }
        }
    }
    public static void renderBlackHole(PoseStack stack, Vec3 pos, MultiBufferSource source, float radius, int longs, int lats) {
        stack.pushPose();
        stack.translate(pos.x, pos.y, pos.z);
        Matrix4f last = stack.last().pose();
        ShaderInstance shader = DataNEssenceCoreShaders.WARPING_POINT;
        shader.safeGetUniform("CameraPosition").set(new Vector3f(0, 0, 0));
        shader.safeGetUniform("ObjectPosition").set(pos.toVector3f().sub(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f()));
        shader.safeGetUniform("Radius").set(Math.abs(radius));
        shader.safeGetUniform("ModelViewMatrix").set(PostShaderManager.viewStackMatrix);
        shader.safeGetUniform("lookVector").set(Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector());
        shader.safeGetUniform("FOV").set((float)Math.toRadians(Minecraft.getInstance().gameRenderer.getFov(Minecraft.getInstance().gameRenderer.getMainCamera(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), true)));
        VertexConsumer consumer = source.getBuffer(DataNEssenceRenderTypes.WARPING_POINT);
        float startU = 0;
        float startV = 0;
        float endU = Mth.PI * 2 * 1;
        float endV = Mth.PI * 1;
        float stepU = (endU - startU) / longs;
        float stepV = (endV - startV) / lats;
        for (int i = 0; i < longs; ++i) {
            // U-points
            for (int j = 0; j < lats; ++j) {
                // V-points
                float u = i * stepU + startU;
                float v = j * stepV + startV;
                float un = (i + 1 == longs) ? endU : (i + 1) * stepU + startU;
                float vn = (j + 1 == lats) ? endV : (j + 1) * stepV + startV;
                Vector3f p0 = parametricSphere(u, v, radius);
                Vector3f p1 = parametricSphere(u, vn, radius);
                Vector3f p2 = parametricSphere(un, v, radius);
                Vector3f p3 = parametricSphere(un, vn, radius);

                consumer.addVertex(last, p0.x(), p0.y(), p0.z());
                consumer.addVertex(last, p2.x(), p2.y(), p2.z());
                consumer.addVertex(last, p1.x(), p1.y(), p1.z());
                consumer.addVertex(last, p1.x(), p1.y(), p1.z());

                consumer.addVertex(last, p3.x(), p3.y(), p3.z());
                consumer.addVertex(last, p1.x(), p1.y(), p1.z());
                consumer.addVertex(last, p2.x(), p2.y(), p2.z());
                consumer.addVertex(last, p2.x(), p2.y(), p2.z());
            }
        }
        ((MultiBufferSource.BufferSource)source).endLastBatch();
        stack.popPose();
    }
    public static double getFractionalLerp(int current, int max) {
        return (double) current / (double) max;
    }

    public static double getYLerp(double lerp, double dY) {
        return Math.pow(lerp, Math.log(Math.abs(dY) + 3));
    }
    public static void renderLine(VertexConsumer consumer, PoseStack stack, Vec3 pointA, Vec3 pointB, Color color) {
        int segmentCount = 32;
        List<Vec3> segments = new ArrayList<>();
        double sag = 0.3;
        Vec3 sagOrigin = pointA.y < pointB.y ? pointA : pointB;
        Vec3 sagTarget = pointA.y < pointB.y ? pointB : pointA;
        Vec3 diff = sagTarget.subtract(sagOrigin);
        for (int i = 0; i < segmentCount; i++) {
            double startLerp = getFractionalLerp(i, segmentCount - 1);
            double startYLerp = getYLerp(startLerp, diff.y);
            double sagFactor = sag * (1 - (4 * Math.pow(startLerp - 0.5, 2)));
            double y = diff.y != 0 ? (startYLerp - sagFactor) * diff.y : -sagFactor;
            segments.add(new Vec3(startLerp * diff.x, y, startLerp * diff.z).add(sagOrigin));
        }
        if (!segments.isEmpty()) {
            Vec3 currentPos = segments.get(0);
            for (int i = 1; i < segments.size(); i++) {
                Vec3 targetPos = segments.get(i);
                Vec3 normal = currentPos.subtract(targetPos).normalize();
                consumer.addVertex(stack.last(), (float) currentPos.x, (float) currentPos.y, (float) currentPos.z).setColor(color.getRGB()).setNormal(stack.last(), (float) normal.x, (float) normal.y, (float) normal.z);
                currentPos = targetPos;
                consumer.addVertex(stack.last(), (float) currentPos.x, (float) currentPos.y, (float) currentPos.z).setColor(color.getRGB()).setNormal(stack.last(), (float) normal.x, (float) normal.y, (float) normal.z);
            }
        }
    }
    public static Vector3f parametricSphere(float u, float v, float r) {
        return new Vector3f(Mth.cos(u) * Mth.sin(v) * r, Mth.cos(v) * r, Mth.sin(u) * Mth.sin(v) * r);
    }
}
