package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.moddata.LockableItemHandler;

import java.util.List;

public interface ILockableContainer {
    public List<LockableItemHandler> getLockable();
}
