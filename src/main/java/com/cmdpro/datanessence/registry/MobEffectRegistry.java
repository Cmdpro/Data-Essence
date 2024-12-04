package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.effects.GenderEuphoria;
import com.cmdpro.datanessence.effects.Shrunken;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT,
            DataNEssence.MOD_ID);

    public static DeferredHolder<MobEffect, GenderEuphoria> GENDER_EUPHORIA = register("gender_euphoria", () ->
            new GenderEuphoria(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static DeferredHolder<MobEffect, Shrunken> SHRUNKEN = register("shrunken", () ->
            new Shrunken(MobEffectCategory.NEUTRAL, 0xFFFFFF));
    private static <T extends MobEffect> DeferredHolder<MobEffect, T> register(final String name, final Supplier<T> effect) {
        return MOB_EFFECTS.register(name, effect);
    }
}
