package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class WireMinigameCreator extends MinigameCreator<WireMinigame> {
    public Map<Vector2i, WireMinigame.Tile> tiles;
    public WireMinigameCreator(Map<Vector2i, WireMinigame.Tile> tiles) {
        this.tiles = tiles;
    }
    @Override
    public WireMinigame createMinigame() {
        return new WireMinigame(tiles);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.WIRE.get();
    }

    public static class WireMinigameSerializer extends MinigameSerializer<WireMinigameCreator> {
        @Override
        public WireMinigameCreator fromJson(JsonObject json) {
            HashMap<Vector2i, WireMinigame.Tile> tiles = new HashMap<>();
            for (JsonElement i : json.get("tiles").getAsJsonArray()) {
                JsonObject obj = i.getAsJsonObject();
                Vector2i pos = new Vector2i(obj.get("x").getAsInt(), obj.get("y").getAsInt());
                WireMinigame.Tile tile = new WireMinigame.Tile();
                tile.pos = pos;
                int essence = obj.get("essence").getAsInt();
                tile.essence = essence;
                int type = obj.get("type").getAsInt();
                tile.type = type;
                tiles.put(pos, tile);
            }
            return new WireMinigameCreator(tiles);
        }

        @Override
        public WireMinigameCreator fromNetwork(FriendlyByteBuf buf) {
            Map<Vector2i, WireMinigame.Tile> tiles = buf.readMap(WireMinigameSerializer::readVector2i, WireMinigameSerializer::readTile);
            return new WireMinigameCreator(tiles);
        }

        @Override
        public void toNetwork(WireMinigameCreator creator, FriendlyByteBuf buf) {
            buf.writeMap(creator.tiles, WireMinigameSerializer::writeVector2i, WireMinigameSerializer::writeTile);
        }
        public static void writeTile(FriendlyByteBuf buf, WireMinigame.Tile tile) {
            buf.writeInt(tile.pos.x);
            buf.writeInt(tile.pos.y);
            buf.writeInt(tile.essence);
            buf.writeInt(tile.type);
        }
        public static WireMinigame.Tile readTile(FriendlyByteBuf buf) {
            WireMinigame.Tile tile = new WireMinigame.Tile();
            int x = buf.readInt();
            int y = buf.readInt();
            tile.pos = new Vector2i(x, y);
            tile.essence = buf.readInt();
            tile.type = buf.readInt();
            return tile;
        }
        public static void writeVector2i(FriendlyByteBuf buf, Vector2i pos) {
            buf.writeInt(pos.x);
            buf.writeInt(pos.y);
        }
        public static Vector2i readVector2i(FriendlyByteBuf buf) {
            int x = buf.readInt();
            int y = buf.readInt();
            return new Vector2i(x, y);
        }
    }
}
