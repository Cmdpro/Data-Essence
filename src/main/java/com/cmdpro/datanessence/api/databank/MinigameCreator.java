package com.cmdpro.datanessence.api.databank;

public abstract class MinigameCreator<T extends Minigame> {
    public abstract T createMinigame();
    public abstract MinigameSerializer getSerializer();
}
