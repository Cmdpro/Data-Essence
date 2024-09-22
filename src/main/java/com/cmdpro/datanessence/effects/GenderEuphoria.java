package com.cmdpro.datanessence.effects;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.particle.MoteParticleOptions;
import com.cmdpro.datanessence.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.particle.SmallCircleParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;
import java.util.Random;

public class GenderEuphoria extends MobEffect {
    public GenderEuphoria(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "effect.gender_euphoria"), 0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
