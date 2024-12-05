package com.cmdpro.datanessence.effects;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Shrunken extends MobEffect {
    public Shrunken(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        addAttributeModifier(
                Attributes.SCALE, ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "effect.shrunken"), -0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
