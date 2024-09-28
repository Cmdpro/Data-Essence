package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
        public static final MapCodec<WireMinigame.Tile> TILE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("x").forGetter((obj) -> obj.pos.x),
                Codec.INT.fieldOf("y").forGetter((obj) -> obj.pos.y),
                Codec.INT.fieldOf("essence").forGetter((obj) -> obj.essence),
                Codec.INT.fieldOf("type").forGetter((obj) -> obj.type)
        ).apply(instance, (x, y, essence, type) ->  {
            WireMinigame.Tile tile = new WireMinigame.Tile();
            tile.pos = new Vector2i(x, y);
            tile.essence = essence;
            tile.type = type;
            return tile;
        }));
        public static final StreamCodec<RegistryFriendlyByteBuf, WireMinigame.Tile> TILE_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) ->  {
            pBuffer.writeInt(pValue.pos.x);
            pBuffer.writeInt(pValue.pos.y);
            pBuffer.writeInt(pValue.essence);
            pBuffer.writeInt(pValue.type);
        }, pBuffer -> {
            WireMinigame.Tile tile = new WireMinigame.Tile();
            int x = pBuffer.readInt();
            int y = pBuffer.readInt();
            tile.pos = new Vector2i(x, y);
            tile.essence = pBuffer.readInt();
            tile.type = pBuffer.readInt();
            return tile;
        });
        public static final StreamCodec<RegistryFriendlyByteBuf, WireMinigameCreator> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeMap(pValue.tiles, (pBuffer2, pValue2) -> {
                pBuffer2.writeInt(pValue2.x);
                pBuffer2.writeInt(pValue2.y);
            }, (pBuffer2, pValue2) -> TILE_STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer2, pValue2));
        }, (pBuffer) -> {
            Map<Vector2i, WireMinigame.Tile> tiles = pBuffer.readMap((pBuffer2) -> {
                int x = pBuffer2.readInt();
                int y = pBuffer2.readInt();
                return new Vector2i(x, y);
            }, (pBuffer2) -> TILE_STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer2));
            return new WireMinigameCreator(tiles);
        });
        public static final MapCodec<WireMinigameCreator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                TILE_CODEC.codec().listOf().fieldOf("tiles").xmap((a) -> {
                    Map<Vector2i, WireMinigame.Tile> map = new HashMap<>();
                    for (WireMinigame.Tile i : a) {
                        map.put(i.pos, i);
                    }
                    return map;
                }, (a) -> a.values().stream().toList()).forGetter(minigame -> minigame.tiles)
        ).apply(instance, WireMinigameCreator::new));
        @Override
        public MapCodec<WireMinigameCreator> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WireMinigameCreator> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
