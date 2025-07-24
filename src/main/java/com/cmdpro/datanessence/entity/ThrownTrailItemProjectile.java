package com.cmdpro.datanessence.entity;

import com.cmdpro.databank.misc.ColorGradient;
import com.cmdpro.databank.misc.TrailLeftoverHandler;
import com.cmdpro.databank.misc.TrailRender;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;

public abstract class ThrownTrailItemProjectile extends ThrowableItemProjectile {
    public ThrownTrailItemProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownTrailItemProjectile(EntityType<? extends ThrowableItemProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    public ThrownTrailItemProjectile(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientRemoval() {
        super.onClientRemoval();
        TrailRender render = getTrail();
        if (render != null) {
            TrailLeftoverHandler.addTrail(render, RenderHandler.createBufferSource(), LightTexture.FULL_BRIGHT, getGradient());
        }
    }

    private TrailRender trail;
    public ColorGradient getGradient() {
        return new ColorGradient(
                new Color(255, 255, 255),
                new Color(255, 255, 255)
        ).fadeAlpha(1, 0).fadeAlpha(0, 0, 1, 0.05f);
    }
    public TrailRender getTrail() {
        if (trail == null) {
            trail = new TrailRender(position(), 20, 20, 0.15f, DataNEssence.locate("textures/vfx/trail.png"),
                    RenderTypeHandler::transparent
            ).setShrink(true).startTicking();
        }
        return trail;
    }
}
