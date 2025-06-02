package com.cmdpro.datanessence.client.particle;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.RandomUtils;

public class EssenceSparkleParticle extends TextureSheetParticle {
    public float startQuadSize;
    public float rot;
    protected EssenceSparkleParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                                     SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 0.8F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 0.85F;
        startQuadSize = this.quadSize;
        this.lifetime = 20;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
        this.alpha = 1f;
        this.hasPhysics = false;
        rot = Mth.nextFloat(level.random, -1f, 1f);
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(RenderHandler.createBufferSource().getBuffer(RenderTypeHandler.TRANSPARENT_PARTICLE), pRenderInfo, pPartialTicks);
    }
    @Override
    public void tick() {
        super.tick();
        fadeOut();
        this.oRoll = this.roll;
        roll += rot/20f;
    }

    private void fadeOut() {
        this.quadSize = (-(1/(float)lifetime) * age + 1)*startQuadSize;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new EssenceSparkleParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880; // fullbright
    }
}