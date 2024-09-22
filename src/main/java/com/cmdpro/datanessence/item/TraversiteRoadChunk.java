package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.registry.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class TraversiteRoadChunk extends Item {
    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder().alwaysEdible().effect(() -> new MobEffectInstance(MobEffectRegistry.GENDER_EUPHORIA, 20*60), 1).build();
    public TraversiteRoadChunk(Properties pProperties) {
        super(pProperties.food(FOOD_PROPERTIES));
    }
}
