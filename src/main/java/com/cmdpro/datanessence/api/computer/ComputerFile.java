package com.cmdpro.datanessence.api.computer;

public abstract class ComputerFile<T> {
    public abstract ComputerFileType getType();
    public T getTempData() {
        if (tempData == null) {
            tempData = createTempData();
        }
        return tempData;
    }
    public void resetTempData() {
        tempData = null;
    }
    protected abstract T createTempData();
    protected T tempData;
}
