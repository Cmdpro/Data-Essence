package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import net.minecraft.world.item.Item;

public class FilterNodeUpgrade extends Item implements INodeUpgrade {
    public FilterNodeUpgrade(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Type getType() {
        return Type.UNIQUE;
    }
}
