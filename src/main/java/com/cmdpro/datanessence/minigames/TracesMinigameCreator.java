package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class TracesMinigameCreator extends MinigameCreator {
    public Map<Vector2i, TracesMinigame.Tile> tiles;
    public int size;
    public TracesMinigameCreator(Map<Vector2i, TracesMinigame.Tile> tiles, int size) {
        this.tiles = tiles;
        this.size = size;
    }
    @Override
    public Minigame createMinigame() {
        return new TracesMinigame(tiles, size);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.TRACES.get();
    }

    public static class TracesMinigameSerializer extends MinigameSerializer<TracesMinigameCreator> {
        public static final MapCodec<TracesMinigame.Tile> TILE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("x").forGetter((obj) -> obj.pos.x),
                Codec.INT.fieldOf("y").forGetter((obj) -> obj.pos.y),
                Codec.INT.fieldOf("essence").forGetter((obj) -> obj.essence),
                Codec.INT.fieldOf("type").forGetter((obj) -> obj.type)
        ).apply(instance, (x, y, essence, type) ->  {
            TracesMinigame.Tile tile = new TracesMinigame.Tile();
            tile.pos = new Vector2i(x, y);
            tile.essence = essence;
            tile.type = type;
            return tile;
        }));
        public static final StreamCodec<RegistryFriendlyByteBuf, TracesMinigame.Tile> TILE_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) ->  {
            pBuffer.writeInt(pValue.pos.x);
            pBuffer.writeInt(pValue.pos.y);
            pBuffer.writeInt(pValue.essence);
            pBuffer.writeInt(pValue.type);
        }, pBuffer -> {
            TracesMinigame.Tile tile = new TracesMinigame.Tile();
            int x = pBuffer.readInt();
            int y = pBuffer.readInt();
            tile.pos = new Vector2i(x, y);
            tile.essence = pBuffer.readInt();
            tile.type = pBuffer.readInt();
            return tile;
        });
        public static final StreamCodec<RegistryFriendlyByteBuf, TracesMinigameCreator> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeMap(pValue.tiles, (pBuffer2, pValue2) -> {
                pBuffer2.writeInt(pValue2.x);
                pBuffer2.writeInt(pValue2.y);
            }, (pBuffer2, pValue2) -> TILE_STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer2, pValue2));
            pBuffer.writeInt(pValue.size);
        }, (pBuffer) -> {
            Map<Vector2i, TracesMinigame.Tile> tiles = pBuffer.readMap((pBuffer2) -> {
                int x = pBuffer2.readInt();
                int y = pBuffer2.readInt();
                return new Vector2i(x, y);
            }, (pBuffer2) -> TILE_STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer2));
            int size = pBuffer.readInt();
            return new TracesMinigameCreator(tiles, size);
        });
        public static final MapCodec<TracesMinigameCreator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                TILE_CODEC.codec().listOf().fieldOf("tiles").xmap((a) -> {
                    Map<Vector2i, TracesMinigame.Tile> map = new HashMap<>();
                    for (TracesMinigame.Tile i : a) {
                        map.put(i.pos, i);
                    }
                    return map;
                }, (a) -> a.values().stream().toList()).forGetter(minigame -> minigame.tiles),
                Codec.INT.fieldOf("size").forGetter(minigame -> minigame.size)
        ).apply(instance, TracesMinigameCreator::new));
        @Override
        public MapCodec<TracesMinigameCreator> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TracesMinigameCreator> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
