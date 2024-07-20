package com.cmdpro.datanessence.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class CircleParticleAdditive extends TextureSheetParticle {
    public float startQuadSize;
    public CircleParticleOptionsAdditive options;
    protected CircleParticleAdditive(ClientLevel level, double xCoord, double yCoord, double zCoord,
                                     SpriteSet spriteSet, double xd, double yd, double zd, CircleParticleOptionsAdditive options) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 0.8F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 0.85F;
        startQuadSize = this.quadSize;
        this.lifetime = 20;
        this.setSpriteFromAge(spriteSet);

        this.rCol = (float)options.color.getRed()/255f;
        this.gCol = (float)options.color.getGreen()/255f;
        this.bCol = (float)options.color.getBlue()/255f;
        this.alpha = (float)options.color.getAlpha()/255f;
        this.hasPhysics = true;
        this.options = options;
    }
    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        this.quadSize = (-(1/(float)lifetime) * age + 1)*startQuadSize;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ADDITIVE;
    }
    public static final ParticleRenderType ADDITIVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.enableBlend();
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE.value);
            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }

        @Override
        public String toString() {
            return "datanessence:additive";
        }
    };
    public static class Provider implements ParticleProvider<CircleParticleOptionsAdditive> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(CircleParticleOptionsAdditive particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new CircleParticleAdditive(level, x, y, z, this.sprites, dx, dy, dz, particleType);
        }
    }
}
