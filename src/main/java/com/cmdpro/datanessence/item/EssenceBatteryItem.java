package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class EssenceBatteryItem extends BlockItem {

    public EssenceBatteryItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("EssenceStorage")) {
                CompoundTag storageTag = tag.getCompound("EssenceStorage");
                float amount = storageTag.getFloat("Amount");
                float max = DataNEssenceConfig.essenceBatteryMax;
                return Math.round(13.0f * (amount / max));
            }
        }
        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return EssenceTypeRegistry.ESSENCE.get().getColor();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("EssenceStorage")) {
                CompoundTag storageTag = tag.getCompound("EssenceStorage");
                float amount = storageTag.getFloat("Amount");
                float max = DataNEssenceConfig.essenceBatteryMax;

                tooltipComponents.add(Component.translatable("gui.essence_bar.essence_with_max",
                                (int)amount,
                                (int)max)
                        .withStyle(Style.EMPTY.withColor(EssenceTypeRegistry.ESSENCE.get().getColor())));
            }
        } else {
            // Empty battery
            tooltipComponents.add(Component.translatable("gui.essence_bar.essence_with_max",
                            0,
                            (int)DataNEssenceConfig.essenceBatteryMax)
                    .withStyle(Style.EMPTY.withColor(EssenceTypeRegistry.ESSENCE.get().getColor())));
        }
    }
}