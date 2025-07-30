package com.cmdpro.datanessence.client.particle;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

import javax.annotation.Nullable;

public class CircleParticle extends TextureSheetParticle {
    public float startQuadSize;
    public CircleParticleOptions options;
    protected CircleParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                             SpriteSet spriteSet, double xd, double yd, double zd, CircleParticleOptions options) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 0.85F;
        startQuadSize = this.quadSize;
        this.setSpriteFromAge(spriteSet);

        this.rCol = (float)options.color.getRed()/255f;
        this.gCol = (float)options.color.getGreen()/255f;
        this.bCol = (float)options.color.getBlue()/255f;
        this.alpha = (float)options.color.getAlpha()/255f;
        this.friction = options.friction;
        this.lifetime = options.lifetime;
        this.gravity = options.gravity;
        this.hasPhysics = options.physics;
        this.options = options;
    }
    @Override
    protected int getLightColor(float partialTick) {
        if (options.fullbright) {
            return LightTexture.FULL_BRIGHT;
        }
        return super.getLightColor(partialTick);
    }
    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(RenderHandler.createBufferSource().getBuffer(options.additive ? RenderTypeHandler.ADDITIVE_PARTICLE : RenderTypeHandler.TRANSPARENT_PARTICLE), pRenderInfo, pPartialTicks);
    }
    @Override
    public ParticleRenderType getRenderType() {
        return options.additive ? ADDITIVE : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void fadeOut() {
        this.quadSize = (-(1/(float)lifetime) * age + 1)*startQuadSize;
    }

    public static final ParticleRenderType ADDITIVE = new ParticleRenderType() {
        @Nullable
        @Override
        public BufferBuilder begin(Tesselator pBuilder, TextureManager pTextureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.enableBlend();
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
            return pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "datanessence:additive";
        }
    };
    public static class Provider implements ParticleProvider<CircleParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(CircleParticleOptions particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new CircleParticle(level, x, y, z, this.sprites, dx, dy, dz, particleType);
        }
    }
}
