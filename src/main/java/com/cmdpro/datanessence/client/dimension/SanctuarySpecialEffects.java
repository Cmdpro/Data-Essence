package com.cmdpro.datanessence.client.dimension;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.awt.*;

public class SanctuarySpecialEffects extends DimensionSpecialEffects {
    private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

    private static final VertexBuffer starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private static final VertexBuffer skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private static final VertexBuffer darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
    static {
        darkBuffer.bind();
        darkBuffer.upload(buildSkyDisc(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
        skyBuffer.bind();
        skyBuffer.upload(buildSkyDisc(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
        starBuffer.bind();
        starBuffer.upload(drawStars(Tesselator.getInstance()));
        VertexBuffer.unbind();
    }

    private static MeshData drawStars(Tesselator tesselator) {
        RandomSource randomsource = RandomSource.create(10842L);
        int i = 1500;
        float f = 100.0F;
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int j = 0; j < 3000; j++) {
            float h = randomsource.nextFloat();
            Color color = Color.getHSBColor(h, 0.8f, 0.9f);
            Color color2 = Color.getHSBColor(h, 0.5f, 0.75f);
            color2 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), 255/2);
            float f1 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f2 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f3 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f4 = (0.15F + randomsource.nextFloat() * 0.1F)/2f;
            float f5 = Mth.lengthSquared(f1, f2, f3);
            if (!(f5 <= 0.010000001F) && !(f5 >= 1.0F)) {
                Vector3f vector3f = new Vector3f(f1, f2, f3).normalize(100.0F);
                float f6 = (float)(randomsource.nextDouble() * (float) Math.PI * 2.0);
                Quaternionf quaternionf = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(f6);

                float xMult = 5;
                float yMult = 5;

                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(f4*xMult, -f4, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(f4*xMult, f4, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(-f4*xMult, f4, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(-f4*xMult, -f4, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());

                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(f4, -f4*yMult, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(f4, f4*yMult, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(-f4, f4*yMult, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(-f4, -f4*yMult, 0.0F).rotate(quaternionf))).setColor(color2.getRGB());

                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(f4, -f4, 0.0F).rotate(quaternionf))).setColor(color.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(f4, f4, 0.0F).rotate(quaternionf))).setColor(color.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(-f4, f4, 0.0F).rotate(quaternionf))).setColor(color.getRGB());
                bufferbuilder.addVertex(new Vector3f(vector3f).add(new Vector3f(-f4, -f4, 0.0F).rotate(quaternionf))).setColor(color.getRGB());
            }
        }

        return bufferbuilder.buildOrThrow();
    }

    private static MeshData buildSkyDisc(Tesselator tesselator, float y) {
        float f = Math.signum(y) * 512.0F;
        float f1 = 512.0F;
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex(0.0F, y, 0.0F);

        for (int i = -180; i <= 180; i += 45) {
            bufferbuilder.addVertex(f * Mth.cos((float)i * (float) (Math.PI / 180.0)), y, 512.0F * Mth.sin((float)i * (float) (Math.PI / 180.0)));
        }

        return bufferbuilder.buildOrThrow();
    }

    public SanctuarySpecialEffects() {
        super(192.0F, true, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 p_108908_, float p_108909_) {
        return p_108908_.multiply((double)(p_108909_ * 0.94F + 0.06F), (double)(p_108909_ * 0.94F + 0.06F), (double)(p_108909_ * 0.91F + 0.09F));
    }

    @Override
    public boolean isFoggyAt(int p_108905_, int p_108906_) {
        return false;
    }

    public static final Color NIGHT_COLOR = new Color(0x05000C);

    public Vec3 getSkyColor(ClientLevel level, Vec3 pos, float partialTick) {
        float f = level.getTimeOfDay(partialTick);
        Vec3 vec3 = pos.subtract(2.0, 2.0, 2.0).scale(0.25);
        BiomeManager biomemanager = level.getBiomeManager();
        Vec3 vec31 = CubicSampler.gaussianSampleVec3(
                vec3, (p_194161_, p_194162_, p_194163_) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_194161_, p_194162_, p_194163_).value().getSkyColor())
        );
        float f1 = Mth.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.0F, 1.0F);
        float f2 = Mth.lerp(f1, (float)NIGHT_COLOR.getRed()/255f, (float)vec31.x);
        float f3 = Mth.lerp(f1, (float)NIGHT_COLOR.getGreen()/255f, (float)vec31.y);
        float f4 = Mth.lerp(f1, (float)NIGHT_COLOR.getBlue()/255f, (float)vec31.z);
        float f5 = level.getRainLevel(partialTick);
        if (f5 > 0.0F) {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.75F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        float f9 = level.getThunderLevel(partialTick);
        if (f9 > 0.0F) {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.75F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }

        int i = level.getSkyFlashTime();
        if (i > 0) {
            float f11 = (float)i - partialTick;
            if (f11 > 1.0F) {
                f11 = 1.0F;
            }

            f11 *= 0.45F;
            f2 = f2 * (1.0F - f11) + 0.8F * f11;
            f3 = f3 * (1.0F - f11) + 0.8F * f11;
            f4 = f4 * (1.0F - f11) + 1.0F * f11;
        }

        return new Vec3((double)f2, (double)f3, (double)f4);
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelViewMatrix);

        Vec3 vec3 = getSkyColor(level, mc.gameRenderer.getMainCamera().getPosition(), partialTick);
        float f = (float)vec3.x;
        float f1 = (float)vec3.y;
        float f2 = (float)vec3.z;
        FogRenderer.levelFogColor();
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.depthMask(false);

        RenderSystem.setShaderColor(f, f1, f2, 1.0F);
        ShaderInstance shaderinstance = RenderSystem.getShader();
        skyBuffer.bind();
        skyBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        float[] afloat = level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
        if (afloat != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            float f3 = Mth.sin(level.getSunAngle(partialTick)) < 0.0F ? 180.0F : 0.0F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f3));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            float f4 = afloat[0];
            float f5 = afloat[1];
            float f6 = afloat[2];
            Matrix4f matrix4f = poseStack.last().pose();
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.addVertex(matrix4f, 0.0F, 100.0F, 0.0F).setColor(f4, f5, f6, afloat[3]);
            int i = 16;

            for (int j = 0; j <= 16; j++) {
                float f7 = (float)j * (float) (Math.PI * 2) / 16.0F;
                float f8 = Mth.sin(f7);
                float f9 = Mth.cos(f7);
                bufferbuilder.addVertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3])
                        .setColor(afloat[0], afloat[1], afloat[2], 0.0F);
            }

            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            poseStack.popPose();
        }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        poseStack.pushPose();
        float f11 = 1.0F - level.getRainLevel(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
        Matrix4f matrix4f1 = poseStack.last().pose();
        float f12 = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferBuilder bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, -f12).setUv(0.0F, 0.0F);
        bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, -f12).setUv(1.0F, 0.0F);
        bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, f12).setUv(1.0F, 1.0F);
        bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, f12).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
        f12 = 20.0F;
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        int k = level.getMoonPhase();
        int l = k % 4;
        int i1 = k / 4 % 2;
        float f13 = (float)(l + 0) / 4.0F;
        float f14 = (float)(i1 + 0) / 2.0F;
        float f15 = (float)(l + 1) / 4.0F;
        float f16 = (float)(i1 + 1) / 2.0F;
        bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder1.addVertex(matrix4f1, -f12, -100.0F, f12).setUv(f15, f16);
        bufferbuilder1.addVertex(matrix4f1, f12, -100.0F, f12).setUv(f13, f16);
        bufferbuilder1.addVertex(matrix4f1, f12, -100.0F, -f12).setUv(f13, f14);
        bufferbuilder1.addVertex(matrix4f1, -f12, -100.0F, -f12).setUv(f15, f14);
        BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());

        float f10 = level.getStarBrightness(partialTick) * f11;
        if (f10 > 0.0F) {
            RenderSystem.setShaderColor(f10, f10, f10, f10);
            FogRenderer.setupNoFog();
            starBuffer.bind();
            starBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();
            setupFog.run();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.mulPose(Axis.ZP.rotationDegrees(55));
        poseStack.mulPose(Axis.YP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
        poseStack.translate(0, -25, 0);
        poseStack.pushPose();
        poseStack.scale(150, 150, 150);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DataNEssence.locate("textures/vfx/sanctuary_ring.png"));
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        renderQuad(bufferbuilder, poseStack, -1, 0, -1, 1, 0, 1, 0, 0, 1, 1);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        poseStack.popPose();
        poseStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double d0 = mc.player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level);
        if (d0 < 0.0) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 12.0F, 0.0F);
            darkBuffer.bind();
            darkBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
            VertexBuffer.unbind();
            poseStack.popPose();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        return true;
    }

    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v) {
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setUv(u, v);
    }

    private static void renderQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0);
/*
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1);
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0);*/
    }
}
