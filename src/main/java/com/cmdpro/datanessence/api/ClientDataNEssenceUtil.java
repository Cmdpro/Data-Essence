package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.ClientModEvents;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.shaders.DataNEssenceRenderTypes;
import com.cmdpro.datanessence.toasts.CriticalDataToast;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ClientDataNEssenceUtil {
    public static class EssenceBarRendering {
        public static void drawEssenceBarTiny(GuiGraphics graphics, int x, int y, int type, float amount, float full) {
            ResourceLocation fill = DataTabletScreen.TEXTURECRAFTING;
            int u = type == 0 || type == 2 ? 6 : 1;
            int v = type == 0 || type == 1 ? 202 : 226;
            if (amount > 0) {
                graphics.blit(fill, x, y+22 - (int) Math.ceil(22f * (amount / full)), u, v + 22 - (int) Math.ceil(22f * (amount / full)), 3, (int) Math.ceil(22f * (amount / full)));
            }
        }
        public static Component getEssenceBarTooltipTiny(double mouseX, double mouseY, int x, int y, int type, float amount) {
            if (amount > 0) {
                if (mouseX <= x+3 && mouseY <= y+22 && mouseX >= x && mouseY >= y) {
                    if (ClientPlayerData.getUnlockedEssences()[type]) {
                        return Component.translatable("gui.essence_bar." + (type == 0 ? "essence" : type == 1 ? "lunar_essence" : type == 2 ? "natural_essence" : "exotic_essence"), amount);
                    } else {
                        return Component.translatable("gui.essence_bar.unknown", amount);
                    }
                }
            }
            return null;
        }

        public static void drawEssenceBar(GuiGraphics graphics, int x, int y, int type, float amount, float full) {
            if (amount > 0) {
                ResourceLocation fill = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/essence_bars.png");
                int u = type*11;
                int v = 0;
                graphics.blit(fill, x, y + 52 - (int) Math.ceil(52f * (amount / full)), u, v+52 - (int) Math.ceil(52f * (amount / full)), 7, (int) Math.ceil(52f * (amount / full)));
            }
        }
        public static Component getEssenceBarTooltip(double mouseX, double mouseY, int x, int y, int type, float amount) {
            if (amount > 0) {
                if (mouseX >= x && mouseY >= y) {
                    if (mouseX <= x+7 && mouseY <= y + 52) {
                        if (ClientPlayerData.getUnlockedEssences()[type]) {
                            return Component.translatable("gui.essence_bar." + (type == 0 ? "essence" : type == 1 ? "lunar_essence" : type == 2 ? "natural_essence" : "exotic_essence"), amount);
                        } else {
                            return Component.translatable("gui.essence_bar.unknown", amount);
                        }
                    }
                }
            }
            return null;
        }
    }
    public static BlockState getHiddenBlock(Block block) {
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                if (!ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry)) {
                    return i.hiddenAs;
                }
                break;
            }
        }
        return null;
    }
    public static Matrix4f viewStackMatrix;
    public static void renderBlackHole(PoseStack stack, Vec3 pos, MultiBufferSource source, float radius, int longs, int lats) {
        stack.pushPose();
        stack.translate(pos.x, pos.y, pos.z);
        Matrix4f last = stack.last().pose();
        ShaderInstance shader = DataNEssenceCoreShaders.WARPINGPOINT.instance;
        shader.safeGetUniform("CameraPosition").set(new Vector3f(0, 0, 0));
        shader.safeGetUniform("ObjectPosition").set(pos.toVector3f().sub(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f()));
        shader.safeGetUniform("Radius").set(Math.abs(radius));
        shader.safeGetUniform("ModelViewMatrix").set(viewStackMatrix);
        shader.safeGetUniform("lookVector").set(Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector());
        shader.safeGetUniform("FOV").set((float)Math.toRadians(Minecraft.getInstance().gameRenderer.getFov(Minecraft.getInstance().gameRenderer.getMainCamera(), Minecraft.getInstance().getFrameTime(), true)));
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

                consumer.vertex(last, p0.x(), p0.y(), p0.z())
                        .endVertex();
                consumer.vertex(last, p2.x(), p2.y(), p2.z())
                        .endVertex();
                consumer.vertex(last, p1.x(), p1.y(), p1.z())
                        .endVertex();
                consumer.vertex(last, p1.x(), p1.y(), p1.z())
                        .endVertex();

                consumer.vertex(last, p3.x(), p3.y(), p3.z())
                        .endVertex();
                consumer.vertex(last, p1.x(), p1.y(), p1.z())
                        .endVertex();
                consumer.vertex(last, p2.x(), p2.y(), p2.z())
                        .endVertex();
                consumer.vertex(last, p2.x(), p2.y(), p2.z())
                        .endVertex();
            }
        }
        ((MultiBufferSource.BufferSource)source).endLastBatch();
        stack.popPose();
    }
    public static Vector3f parametricSphere(float u, float v, float r) {
        return new Vector3f(Mth.cos(u) * Mth.sin(v) * r, Mth.cos(v) * r, Mth.sin(u) * Mth.sin(v) * r);
    }
    public static void renderBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, long pGameTime, Vec3 pointA, Vec3 pointB, Color color, float pBeamRadius, float pGlowRadius) {
        float height = (float)pointA.distanceTo(pointB);
        float i = height;
        pPoseStack.pushPose();
        pPoseStack.translate(pointA.x, pointA.y, pointA.z);
        float f = (float)Math.floorMod(pGameTime, 40) + pPartialTick;
        float f1 = height < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float)Mth.floor(f1 * 0.1F));
        float f3 = (float)color.getRed()/255f;
        float f4 = (float)color.getGreen()/255f;
        float f5 = (float)color.getBlue()/255f;
        pPoseStack.pushPose();
        rotateStackToPoint(pPoseStack, pointA, pointB);
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float f6 = 0.0F;
        float f8 = 0.0F;
        float f9 = -pBeamRadius;
        float f10 = 0.0F;
        float f11 = 0.0F;
        float f12 = -pBeamRadius;
        float f13 = 0.0F;
        float f14 = 1.0F;
        float f15 = -1.0F + f2;
        float f16 = (float)height * pTextureScale * (0.5F / pBeamRadius) + f15;
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)), f3, f4, f5, 1.0F, 0, i, 0.0F, pBeamRadius, pBeamRadius, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        f6 = -pGlowRadius;
        float f7 = -pGlowRadius;
        f8 = -pGlowRadius;
        f9 = -pGlowRadius;
        f13 = 0.0F;
        f14 = 1.0F;
        f15 = -1.0F + f2;
        f16 = (float)height * pTextureScale + f15;
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)), f3, f4, f5, 0.125F, 0, i, f6, f7, pGlowRadius, f8, f9, pGlowRadius, pGlowRadius, pGlowRadius, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        pPoseStack.popPose();
    }

    public static void rotateStackToPoint(PoseStack pPoseStack, Vec3 pointA, Vec3 pointB) {
        double dX = pointA.x - pointB.x;
        double dY = pointA.y - pointB.y;
        double dZ = pointA.z - pointB.z;
        double yAngle = Math.atan2(0 - dX, 0 - dZ);
        yAngle = yAngle * (180 / Math.PI);
        yAngle = yAngle < 0 ? 360 - (-yAngle) : yAngle;
        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) yAngle + 90));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));

        double angle = Math.atan2(dY, Math.sqrt(dX * dX + dZ * dZ));
        angle = angle * (180 / Math.PI);
        angle = angle < 0 ? 360 - (-angle) : angle;
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90 - (float) angle));
    }
    public static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pMinY, float pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX0, pZ0, pX1, pZ1, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX1, pZ1, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX2, pZ2, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV);
    }

    public static void renderQuad(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pMinY, float pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMinX, pMinZ, pMaxU, pMinV);
        addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMinX, pMinZ, pMaxU, pMaxV);
        addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxX, pMaxZ, pMinU, pMaxV);
        addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMaxX, pMaxZ, pMinU, pMinV);
    }

    public static void addVertex(PoseStack.Pose pose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pY, float pX, float pZ, float pU, float pV) {
        pConsumer.vertex(pose, pX, (float)pY, pZ).color(pRed, pGreen, pBlue, pAlpha).uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(pose, 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void drawLine(ParticleOptions particle, Vec3 point1, Vec3 point2, Level level, double space) {
        double distance = point1.distanceTo(point2);
        Vec3 vector = point2.subtract(point1).normalize().multiply(space, space, space);
        double length = 0;
        for (Vec3 point = point1; length < distance; point = point.add(vector)) {
            level.addParticle(particle, point.x, point.y, point.z, 0, 0, 0);
            length += space;
        }
    }
    public static void unlockedCriticalData(Entry entry) {
        Minecraft.getInstance().getToasts().addToast(new CriticalDataToast(entry));
        progressionShader();
    }
    public static void progressionShader() {
        ClientModEvents.progressionShader.time = 0;
        ClientModEvents.progressionShader.setActive(true);
    }
    public static void updateWorld() {
        for (SectionRenderDispatcher.RenderSection i : Minecraft.getInstance().levelRenderer.viewArea.sections) {
            i.setDirty(false);
        }
    }
}
