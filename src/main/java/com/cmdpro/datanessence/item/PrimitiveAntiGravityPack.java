package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.registry.ArmorMaterialRegistry;
import net.minecraft.world.entity.EquipmentSlotGroup;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.UUID;

public class PrimitiveAntiGravityPack extends ArmorItem {
    public static final UUID GRAVITY_ATTRIBUTE = UUID.fromString("1f0e6199-12e0-47a8-beb9-8d43414a2542");
    public static final AttributeModifier GRAVITY_MODIFIER = new AttributeModifier(GRAVITY_ATTRIBUTE, "Anti-Gravity Pack Gravity", 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public PrimitiveAntiGravityPack(Properties pProperties) {
        super(ArmorMaterialRegistry.PRIMITIVE_ANTI_GRAVITY_PACK, Type.BODY, pProperties);
    }

    @Override
    public ItemAttributeModifiers getAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry i : super.getAttributeModifiers(stack).modifiers()) {
            builder.add(i.attribute(), i.modifier(), i.slot());
        }
        if (DataNEssenceUtil.ItemUtil.EssenceChargeableItemUtil.getEssence(stack) >= 1) {
            builder.add(Attributes.GRAVITY, GRAVITY_MODIFIER, EquipmentSlotGroup.BODY);
        }
        return builder.build();
    }
}
