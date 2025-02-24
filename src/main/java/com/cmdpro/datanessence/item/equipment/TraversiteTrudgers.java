package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.registry.ArmorMaterialRegistry;

import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public class TraversiteTrudgers extends ArmorItem implements IItemExtension {

    public TraversiteTrudgers(Properties properties) {
        super(ArmorMaterialRegistry.TRAVERSITE_TRUDGERS, Type.BOOTS, properties);
    }

    /**
     * Returns true if the Traversite Trudgers are currently equipped as boots on this entity.
     */
    public static boolean areTrudgersEquipped(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.TRAVERSITE_TRUDGERS.get());
    }
    
    @Override
    public boolean canWalkOnPowderedSnow(ItemStack is, LivingEntity entity) {
        return is.is(ItemRegistry.TRAVERSITE_TRUDGERS.get());
    }
}
