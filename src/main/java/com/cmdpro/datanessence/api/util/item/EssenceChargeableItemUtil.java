package com.cmdpro.datanessence.api.util.item;

import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

public class EssenceChargeableItemUtil {
    public static float getEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.ESSENCE, 0f);
    }

    public static void setEssence(ItemStack stack, float amount) {
        stack.set(DataComponentRegistry.ESSENCE, amount);
    }

    public static float getLunarEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.LUNAR_ESSENCE, 0f);
    }

    public static void setLunarEssence(ItemStack stack, float amount) {
        stack.set(DataComponentRegistry.LUNAR_ESSENCE, amount);
    }

    public static float getNaturalEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.NATURAL_ESSENCE, 0f);
    }

    public static void setNaturalEssence(ItemStack stack, float amount) {
        stack.set(DataComponentRegistry.NATURAL_ESSENCE, amount);
    }

    public static float getExoticEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.EXOTIC_ESSENCE, 0f);
    }

    public static void setExoticEssence(ItemStack stack, float amount) {
        stack.set(DataComponentRegistry.EXOTIC_ESSENCE, amount);
    }

    public static float getMaxEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.MAX_ESSENCE, 0f);
    }

    public static float getMaxLunarEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.MAX_LUNAR_ESSENCE, 0f);
    }

    public static float getMaxNaturalEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.MAX_NATURAL_ESSENCE, 0f);
    }

    public static float getMaxExoticEssence(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.MAX_EXOTIC_ESSENCE, 0f);
    }

    public static boolean hasEssence(ItemStack stack, float amount) {
        return EssenceChargeableItemUtil.getEssence(stack) >= amount;
    }

    public static boolean hasLunarEssence(ItemStack stack, float amount) {
        return EssenceChargeableItemUtil.getLunarEssence(stack) >= amount;
    }

    public static boolean hasNaturalEssence(ItemStack stack, float amount) {
        return EssenceChargeableItemUtil.getNaturalEssence(stack) >= amount;
    }

    public static boolean hasExoticEssence(ItemStack stack, float amount) {
        return EssenceChargeableItemUtil.getExoticEssence(stack) >= amount;
    }

    public static void drainEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxEssence(stack), EssenceChargeableItemUtil.getEssence(stack) - amount));
    }

    public static void drainLunarEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setLunarEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxLunarEssence(stack), EssenceChargeableItemUtil.getLunarEssence(stack) - amount));
    }

    public static void drainNaturalEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setNaturalEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxNaturalEssence(stack), EssenceChargeableItemUtil.getNaturalEssence(stack) - amount));
    }

    public static void drainExoticEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setExoticEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxExoticEssence(stack), EssenceChargeableItemUtil.getExoticEssence(stack) - amount));
    }

    public static void fillEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxEssence(stack), EssenceChargeableItemUtil.getEssence(stack) + amount));
    }

    public static void fillLunarEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setLunarEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxLunarEssence(stack), EssenceChargeableItemUtil.getLunarEssence(stack) + amount));
    }

    public static void fillNaturalEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setNaturalEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxNaturalEssence(stack), EssenceChargeableItemUtil.getNaturalEssence(stack) + amount));
    }

    public static void fillExoticEssence(ItemStack stack, float amount) {
        EssenceChargeableItemUtil.setExoticEssence(stack, Math.clamp(0f, EssenceChargeableItemUtil.getMaxExoticEssence(stack), EssenceChargeableItemUtil.getExoticEssence(stack) + amount));
    }
}
