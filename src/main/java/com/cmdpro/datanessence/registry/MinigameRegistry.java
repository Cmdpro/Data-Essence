package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.minigames.MinesweeperMinigameCreator;
import com.cmdpro.datanessence.minigames.WireMinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MinigameRegistry {
    public static final DeferredRegister<MinigameSerializer> MINIGAME_TYPES = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "minigames"), DataNEssence.MOD_ID);

    public static final Supplier<MinigameSerializer> MINESWEEPER = register("minesweeper", () -> new MinesweeperMinigameCreator.MinesweeperMinigameSerializer());
    public static final Supplier<MinigameSerializer> WIRE = register("wire", () -> new WireMinigameCreator.WireMinigameSerializer());
    private static <T extends MinigameSerializer> Supplier<T> register(final String name, final Supplier<T> item) {
        return MINIGAME_TYPES.register(name, item);
    }
}
