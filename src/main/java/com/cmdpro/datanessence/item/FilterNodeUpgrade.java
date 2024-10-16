package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.INodeUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FilterNodeUpgrade extends Item implements INodeUpgrade {
    public FilterNodeUpgrade(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Type getType() {
        return Type.UNIQUE;
    }
}
