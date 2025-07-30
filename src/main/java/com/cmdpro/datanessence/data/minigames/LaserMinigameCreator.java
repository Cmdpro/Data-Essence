package com.cmdpro.datanessence.data.minigames;

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

public class LaserMinigameCreator extends MinigameCreator {
    public Map<Vector2i, LaserMinigame.Tile> tiles;
    public LaserMinigameCreator(Map<Vector2i, LaserMinigame.Tile> tiles) {
        this.tiles = tiles;
    }
    @Override
    public Minigame createMinigame() {
        return new LaserMinigame(tiles);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.LASER.get();
    }

    public static class LaserMinigameSerializer extends MinigameSerializer<LaserMinigameCreator> {
        public static final MapCodec<LaserMinigame.Tile> TILE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("x").forGetter((obj) -> obj.pos.x),
                Codec.INT.fieldOf("y").forGetter((obj) -> obj.pos.y),
                Codec.STRING.optionalFieldOf("color", "red").xmap((i) -> LaserMinigame.BeamColor.get(i).getIndex(), (i) -> LaserMinigame.BeamColor.getColor(i).name).forGetter((obj) -> obj.color),
                Codec.STRING.fieldOf("type").xmap((i) -> LaserMinigame.Tile.TileType.get(i).getIndex(), (i) -> LaserMinigame.Tile.TileType.getType(i).name).forGetter((obj) -> obj.type),
                Codec.STRING.optionalFieldOf("rotation", "up").xmap((i) -> LaserMinigame.Tile.TileRotation.get(i).getIndex(), (i) -> LaserMinigame.Tile.TileRotation.getRotation(i).name).forGetter((obj) -> obj.rotation)
        ).apply(instance, (x, y, color, type, rotation) -> new LaserMinigame.Tile(new Vector2i(x, y), color, type, rotation)));
        public static final StreamCodec<RegistryFriendlyByteBuf, LaserMinigame.Tile> TILE_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) ->  {
            pBuffer.writeInt(pValue.pos.x);
            pBuffer.writeInt(pValue.pos.y);
            pBuffer.writeInt(pValue.color);
            pBuffer.writeInt(pValue.type);
            pBuffer.writeInt(pValue.rotation);
        }, pBuffer -> {
            int x = pBuffer.readInt();
            int y = pBuffer.readInt();
            Vector2i pos = new Vector2i(x, y);
            int color = pBuffer.readInt();
            int type = pBuffer.readInt();
            int rotation = pBuffer.readInt();
            return new LaserMinigame.Tile(pos, color, type, rotation);
        });
        public static final StreamCodec<RegistryFriendlyByteBuf, LaserMinigameCreator> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeMap(pValue.tiles, (pBuffer2, pValue2) -> {
                pBuffer2.writeInt(pValue2.x);
                pBuffer2.writeInt(pValue2.y);
            }, (pBuffer2, pValue2) -> TILE_STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer2, pValue2));
        }, (pBuffer) -> {
            Map<Vector2i, LaserMinigame.Tile> tiles = pBuffer.readMap((pBuffer2) -> {
                int x = pBuffer2.readInt();
                int y = pBuffer2.readInt();
                return new Vector2i(x, y);
            }, (pBuffer2) -> TILE_STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer2));
            return new LaserMinigameCreator(tiles);
        });
        public static final MapCodec<LaserMinigameCreator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                TILE_CODEC.codec().listOf().fieldOf("tiles").xmap((a) -> {
                    Map<Vector2i, LaserMinigame.Tile> map = new HashMap<>();
                    for (LaserMinigame.Tile i : a) {
                        map.put(i.pos, i);
                    }
                    return map;
                }, (a) -> a.values().stream().toList()).forGetter(minigame -> minigame.tiles)
        ).apply(instance, LaserMinigameCreator::new));
        @Override
        public MapCodec<LaserMinigameCreator> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LaserMinigameCreator> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
