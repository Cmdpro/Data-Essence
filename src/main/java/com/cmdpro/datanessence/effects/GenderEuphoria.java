package com.cmdpro.datanessence.effects;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class GenderEuphoria extends MobEffect {
    public GenderEuphoria(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED, DataNEssence.locate("effect.gender_euphoria"), 0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
