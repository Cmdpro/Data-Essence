package com.cmdpro.datanessence.api.node;

import net.neoforged.neoforge.items.IItemHandler;

public interface ICustomItemPointBehaviour {
    default boolean canExtractItem(IItemHandler from, IItemHandler to) {
        return true;
    }
    default boolean canInsertItem(IItemHandler from, IItemHandler to) {
        return true;
    }
}
