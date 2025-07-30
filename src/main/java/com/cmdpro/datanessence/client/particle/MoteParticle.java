package com.cmdpro.datanessence.client.particle;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;

public class MoteParticle extends TextureSheetParticle {
    public float maxQuadSize;
    public MoteParticleOptions options;
    protected MoteParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                           SpriteSet spriteSet, double xd, double yd, double zd, MoteParticleOptions options) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 0.85F;
        maxQuadSize = this.quadSize;
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
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(RenderHandler.createBufferSource().getBuffer(options.additive ? RenderTypeHandler.ADDITIVE_PARTICLE : RenderTypeHandler.TRANSPARENT_PARTICLE), pRenderInfo, pPartialTicks);
    }
    @Override
    protected int getLightColor(float partialTick) {
        if (options.fullbright) {
            return LightTexture.FULL_BRIGHT;
        }
        return super.getLightColor(partialTick);
    }
    @Override
    public ParticleRenderType getRenderType() {
        return options.additive ? CircleParticle.ADDITIVE : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        this.quadSize = (-(1/(float)lifetime) * age + 1)* maxQuadSize;
    }

    public static class Provider implements ParticleProvider<MoteParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(MoteParticleOptions particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new MoteParticle(level, x, y, z, this.sprites, dx, dy, dz, particleType);
        }
    }
}
