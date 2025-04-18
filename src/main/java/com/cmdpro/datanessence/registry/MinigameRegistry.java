package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.data.minigames.ColorMixingMinigameCreator;
import com.cmdpro.datanessence.data.minigames.MinesweeperMinigameCreator;
import com.cmdpro.datanessence.data.minigames.TracesMinigameCreator;
import com.cmdpro.datanessence.data.minigames.WireMinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MinigameRegistry {
    public static final DeferredRegister<MinigameSerializer> MINIGAME_TYPES = DeferredRegister.create(DataNEssence.locate("minigames"), DataNEssence.MOD_ID);

    public static final Supplier<MinigameSerializer> MINESWEEPER = register("minesweeper", () -> new MinesweeperMinigameCreator.MinesweeperMinigameSerializer());
    public static final Supplier<MinigameSerializer> WIRE = register("wire", () -> new WireMinigameCreator.WireMinigameSerializer());
    public static final Supplier<MinigameSerializer> TRACES = register("traces", () -> new TracesMinigameCreator.TracesMinigameSerializer());
    public static final Supplier<MinigameSerializer> COLOR_MIXING = register("color_mixing", () -> new ColorMixingMinigameCreator.ColorMixingMinigameSerializer());
    private static <T extends MinigameSerializer> Supplier<T> register(final String name, final Supplier<T> item) {
        return MINIGAME_TYPES.register(name, item);
    }
}
