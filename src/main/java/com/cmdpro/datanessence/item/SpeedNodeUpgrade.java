package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpeedNodeUpgrade extends Item implements INodeUpgrade {
    public float multiplierAddition;
    public SpeedNodeUpgrade(Properties pProperties, float multiplierAddition) {
        super(pProperties);
        this.multiplierAddition = multiplierAddition;
    }

    @Override
    public Object getValue(ResourceLocation id, Object originalValue, BlockEntity node) {
        if (id.equals(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "speed_multiplier"))) {
            return ((float)originalValue)+multiplierAddition;
        }
        return originalValue;
    }

    @Override
    public Type getType() {
        return Type.UNIVERSAL;
    }
}
