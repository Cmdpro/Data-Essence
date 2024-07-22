package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public class WireMinigameCreator extends MinigameCreator<WireMinigame> {
    public int bombs;
    public int size;
    public WireMinigameCreator(int bombs, int size) {
        this.bombs = bombs;
        this.size = size;
    }
    @Override
    public WireMinigame createMinigame() {
        return new WireMinigame(bombs, size);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.WIRE.get();
    }

    public static class WireMinigameSerializer extends MinigameSerializer<WireMinigameCreator> {
        @Override
        public WireMinigameCreator fromJson(JsonObject json) {
            int bombs = json.get("bombs").getAsInt();
            int size = json.get("size").getAsInt();
            return new WireMinigameCreator(bombs, size);
        }

        @Override
        public WireMinigameCreator fromNetwork(FriendlyByteBuf buf) {
            int bombs = buf.readInt();
            int size = buf.readInt();
            return new WireMinigameCreator(bombs, size);
        }

        @Override
        public void toNetwork(WireMinigameCreator creator, FriendlyByteBuf buf) {
            buf.writeInt(creator.bombs);
            buf.writeInt(creator.size);
        }
    }
}
