package com.cmdpro.datanessence.api.misc;

import com.cmdpro.datanessence.api.LockableItemHandler;

import java.util.List;

public interface ILockableContainer {
    public List<LockableItemHandler> getLockable();
}
