package com.cmdpro.datanessence.client.particle;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class MoteParticle extends TextureSheetParticle {
    public float startQuadSize;
    public MoteParticleOptions options;
    protected MoteParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                           SpriteSet spriteSet, double xd, double yd, double zd, MoteParticleOptions options) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 0.8F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 0.85F;
        startQuadSize = this.quadSize;
        this.lifetime = options.lifetime;
        this.setSpriteFromAge(spriteSet);

        this.rCol = (float)options.color.getRed()/255f;
        this.gCol = (float)options.color.getGreen()/255f;
        this.bCol = (float)options.color.getBlue()/255f;
        this.alpha = (float)options.color.getAlpha()/255f;
        this.hasPhysics = true;
        this.options = options;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(RenderHandler.createBufferSource().getBuffer(options.additive ? RenderTypeHandler.ADDITIVE_PARTICLE : RenderTypeHandler.TRANSPARENT_PARTICLE), pRenderInfo, pPartialTicks);
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
        return CircleParticleAdditive.ADDITIVE;
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
