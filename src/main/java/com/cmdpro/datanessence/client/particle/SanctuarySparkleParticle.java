package com.cmdpro.datanessence.client.particle;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SanctuarySparkleParticle extends TextureSheetParticle {
    public SpriteSet spriteSet;
    protected SanctuarySparkleParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                                       SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 0.8F;
        quadSize *= Mth.nextFloat(level.random, 0.85f, 1.25f);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = 14;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
        this.alpha = 1f;
        this.hasPhysics = false;
        roll = Mth.nextFloat(level.random, 0, 360);
        this.oRoll = roll;
        this.spriteSet = spriteSet;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSprite(spriteSet.get(Math.clamp(this.age, 0, lifetime), this.lifetime));
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new SanctuarySparkleParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880; // fullbright
    }
}