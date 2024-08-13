package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.api.block.EssenceContainer;
import com.cmdpro.datanessence.block.transmission.EssenceBufferBlockEntity;
import com.cmdpro.datanessence.block.transmission.FluidBufferBlockEntity;
import com.cmdpro.datanessence.block.transmission.ItemBufferBlockEntity;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Math;

public class EssenceUtil {
    public static void transferEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxEssence() > 0 && to.getMaxEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxEssence()-to.getEssence(), Math.min(from.getEssence(), amount));
            from.setEssence(from.getEssence()-trueAmount);
            to.setEssence(to.getEssence()+trueAmount);
        }
    }
    public static void transferLunarEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxLunarEssence() > 0 && to.getMaxLunarEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxLunarEssence()-to.getLunarEssence(), Math.min(from.getLunarEssence(), amount));
            from.setLunarEssence(from.getLunarEssence()-trueAmount);
            to.setLunarEssence(to.getLunarEssence()+trueAmount);
        }
    }
    public static void transferNaturalEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxNaturalEssence() > 0 && to.getMaxNaturalEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxNaturalEssence()-to.getNaturalEssence(), Math.min(from.getNaturalEssence(), amount));
            from.setNaturalEssence(from.getNaturalEssence()-trueAmount);
            to.setNaturalEssence(to.getNaturalEssence()+trueAmount);
        }
    }
    public static void transferExoticEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxExoticEssence() > 0 && to.getMaxExoticEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxExoticEssence()-to.getExoticEssence(), Math.min(from.getExoticEssence(), amount));
            from.setExoticEssence(from.getExoticEssence()-trueAmount);
            to.setExoticEssence(to.getExoticEssence()+trueAmount);
        }
    }
}
