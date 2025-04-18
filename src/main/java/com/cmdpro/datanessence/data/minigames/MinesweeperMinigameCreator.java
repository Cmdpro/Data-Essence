package com.cmdpro.datanessence.data.minigames;

import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class MinesweeperMinigameCreator extends MinigameCreator {
    public int bombs;
    public int size;
    public MinesweeperMinigameCreator(int bombs, int size) {
        this.bombs = bombs;
        this.size = size;
    }
    @Override
    public Minigame createMinigame() {
        return new MinesweeperMinigame(bombs, size);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.MINESWEEPER.get();
    }

    public static class MinesweeperMinigameSerializer extends MinigameSerializer<MinesweeperMinigameCreator> {
        public static final StreamCodec<RegistryFriendlyByteBuf, MinesweeperMinigameCreator> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeInt(pValue.bombs);
            pBuffer.writeInt(pValue.size);
        }, (pBuffer) -> {
            int bombs = pBuffer.readInt();
            int size = pBuffer.readInt();
            return new MinesweeperMinigameCreator(bombs, size);
        });
        public static final MapCodec<MinesweeperMinigameCreator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("bombs").forGetter(minigame -> minigame.bombs),
                Codec.INT.fieldOf("size").forGetter(minigame -> minigame.size)
        ).apply(instance, MinesweeperMinigameCreator::new));
        @Override
        public MapCodec<MinesweeperMinigameCreator> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MinesweeperMinigameCreator> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
