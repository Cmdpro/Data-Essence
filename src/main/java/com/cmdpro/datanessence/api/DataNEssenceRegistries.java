package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class DataNEssenceRegistries {
    public static Supplier<IForgeRegistry<PageSerializer>> PAGE_TYPE_REGISTRY = null;
    public static Supplier<IForgeRegistry<MinigameCreator>> MINIGAME_TYPE_REGISTRY = null;
}
