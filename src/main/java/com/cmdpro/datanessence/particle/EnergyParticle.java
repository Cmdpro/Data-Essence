package com.cmdpro.datanessence.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class EnergyParticle extends TextureSheetParticle {

    protected EnergyParticle(ClientLevel level, double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Color (cyan/blue)
        this.rCol = 1F;
        this.gCol = 0F;
        this.bCol = 0F;

        this.quadSize = 0.15F; // Size
        this.lifetime = 20; // Lives for 1 second

        this.hasPhysics = false;
        this.friction = 0.96F;
    }

    @Override
    public void tick() {
        super.tick();
        // Fade out over time
        this.alpha = 1.0F - ((float) this.age / (float) this.lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            EnergyParticle particle = new EnergyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}