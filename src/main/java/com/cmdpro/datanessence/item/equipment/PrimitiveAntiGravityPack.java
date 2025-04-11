package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.registry.ArmorMaterialRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;

public class PrimitiveAntiGravityPack extends ArmorItem {
    public static final ResourceLocation GRAVITY_ATTRIBUTE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "primitive_anti_gravity_pack_gravity");
    public static final ResourceLocation FALL_HEIGHT_ATTRIBUTE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "primitive_anti_gravity_pack_fall_height");
    public static final AttributeModifier GRAVITY_MODIFIER = new AttributeModifier(GRAVITY_ATTRIBUTE, -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public static final AttributeModifier FALL_HEIGHT_MODIFIER = new AttributeModifier(FALL_HEIGHT_ATTRIBUTE, 0.8, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public static ResourceLocation FUEL_ESSENCE_TYPE = DataNEssence.locate("essence");
    public PrimitiveAntiGravityPack(Properties pProperties) {
        super(ArmorMaterialRegistry.PRIMITIVE_ANTI_GRAVITY_PACK, Type.CHESTPLATE, pProperties.component(DataComponentRegistry.ESSENCE_STORAGE, new ItemEssenceContainer(List.of(FUEL_ESSENCE_TYPE), 1000)));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pEntity instanceof LivingEntity ent && ent.fallDistance > 0f) {
            if (ent.getItemBySlot(EquipmentSlot.CHEST).equals(pStack)) {
                if (ItemEssenceContainer.getEssence(pStack, FUEL_ESSENCE_TYPE) >= 0.1) {
                    ItemEssenceContainer.removeEssence(pStack, FUEL_ESSENCE_TYPE, 0.1f);
                }
            }
        }
    }
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry i : super.getDefaultAttributeModifiers(stack).modifiers()) {
            builder.add(i.attribute(), i.modifier(), i.slot());
        }
        if (ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) > 0) {
            builder.add(Attributes.GRAVITY, GRAVITY_MODIFIER, EquipmentSlotGroup.CHEST);
            builder.add(Attributes.SAFE_FALL_DISTANCE, FALL_HEIGHT_MODIFIER, EquipmentSlotGroup.CHEST);
        }
        return builder.build();
    }
}
