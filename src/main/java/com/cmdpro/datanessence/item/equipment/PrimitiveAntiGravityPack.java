package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.registry.ArmorMaterialRegistry;
import net.minecraft.world.entity.EquipmentSlotGroup;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.UUID;

public class PrimitiveAntiGravityPack extends ArmorItem {
    public static final UUID GRAVITY_ATTRIBUTE = UUID.fromString("1f0e6199-12e0-47a8-beb9-8d43414a2542");
    public static final UUID FALL_HEIGHT_ATTRIBUTE = UUID.fromString("5217a4e6-bba4-4301-bc17-8274673714cc");
    public static final AttributeModifier GRAVITY_MODIFIER = new AttributeModifier(GRAVITY_ATTRIBUTE, "Anti-Gravity Pack Gravity", -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public static final AttributeModifier FALL_HEIGHT_MODIFIER = new AttributeModifier(FALL_HEIGHT_ATTRIBUTE, "Anti-Gravity Pack Fall Height", 0.8, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public PrimitiveAntiGravityPack(Properties pProperties) {
        super(ArmorMaterialRegistry.PRIMITIVE_ANTI_GRAVITY_PACK, Type.CHESTPLATE, pProperties.component(DataComponentRegistry.MAX_ESSENCE, 1000f));
    }

    @Override
    public ItemAttributeModifiers getAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry i : super.getAttributeModifiers(stack).modifiers()) {
            builder.add(i.attribute(), i.modifier(), i.slot());
        }
        if (DataNEssenceUtil.ItemUtil.EssenceChargeableItemUtil.getEssence(stack) >= 1) {
            builder.add(Attributes.GRAVITY, GRAVITY_MODIFIER, EquipmentSlotGroup.CHEST);
            builder.add(Attributes.SAFE_FALL_DISTANCE, FALL_HEIGHT_MODIFIER, EquipmentSlotGroup.CHEST);
        }
        return builder.build();
    }
}
