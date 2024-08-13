package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.util.EssenceUtil;
import com.cmdpro.datanessence.registry.ArmorMaterialRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class PrimitiveAntiGravityPack extends ArmorItem {
    public static final ResourceLocation GRAVITY_ATTRIBUTE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "primitive_anti_gravity_pack_gravity");
    public static final ResourceLocation FALL_HEIGHT_ATTRIBUTE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "primitive_anti_gravity_pack_fall_height");
    public static final AttributeModifier GRAVITY_MODIFIER = new AttributeModifier(GRAVITY_ATTRIBUTE, -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public static final AttributeModifier FALL_HEIGHT_MODIFIER = new AttributeModifier(FALL_HEIGHT_ATTRIBUTE, 0.8, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public PrimitiveAntiGravityPack(Properties pProperties) {
        super(ArmorMaterialRegistry.PRIMITIVE_ANTI_GRAVITY_PACK, Type.CHESTPLATE, pProperties.component(DataComponentRegistry.MAX_ESSENCE, 1000f));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry i : super.getDefaultAttributeModifiers(stack).modifiers()) {
            builder.add(i.attribute(), i.modifier(), i.slot());
        }
        if (EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getEssence(stack) >= 1) {
            builder.add(Attributes.GRAVITY, GRAVITY_MODIFIER, EquipmentSlotGroup.CHEST);
            builder.add(Attributes.SAFE_FALL_DISTANCE, FALL_HEIGHT_MODIFIER, EquipmentSlotGroup.CHEST);
        }
        return builder.build();
    }
}
