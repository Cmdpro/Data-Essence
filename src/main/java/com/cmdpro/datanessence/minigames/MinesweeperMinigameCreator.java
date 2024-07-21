package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperMinigameCreator extends MinigameCreator<MinesweeperMinigame> {
    public int bombs;
    public int size;
    public MinesweeperMinigameCreator(int bombs, int size) {
        this.bombs = bombs;
        this.size = size;
    }
    @Override
    public MinesweeperMinigame createMinigame() {
        return new MinesweeperMinigame(bombs, size);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.MINESWEEPER.get();
    }

    public static class MinesweeperMinigameSerializer extends MinigameSerializer<MinesweeperMinigameCreator> {
        @Override
        public MinesweeperMinigameCreator fromJson(JsonObject json) {
            int bombs = json.get("bombs").getAsInt();
            int size = json.get("size").getAsInt();
            return new MinesweeperMinigameCreator(bombs, size);
        }

        @Override
        public MinesweeperMinigameCreator fromNetwork(FriendlyByteBuf buf) {
            int bombs = buf.readInt();
            int size = buf.readInt();
            return new MinesweeperMinigameCreator(bombs, size);
        }

        @Override
        public void toNetwork(MinesweeperMinigameCreator creator, FriendlyByteBuf buf) {
            buf.writeInt(creator.bombs);
            buf.writeInt(creator.size);
        }
    }
}
