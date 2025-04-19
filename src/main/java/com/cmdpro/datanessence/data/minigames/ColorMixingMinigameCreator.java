package com.cmdpro.datanessence.data.minigames;

import com.cmdpro.datanessence.api.databank.Minigame;
import com.cmdpro.datanessence.api.databank.MinigameCreator;
import com.cmdpro.datanessence.api.databank.MinigameSerializer;
import com.cmdpro.datanessence.registry.MinigameRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.awt.*;
import java.util.List;

public class ColorMixingMinigameCreator extends MinigameCreator {
    public List<ColorMixingMinigameSerializer.ColorManipulation> manipulations;
    public Color startColor;
    public int maxManipulations;
    public int colorLevels;
    public ColorMixingMinigameCreator(List<ColorMixingMinigameSerializer.ColorManipulation> manipulations, Color startColor, int maxManipulations, int colorLevels) {
        this.manipulations = manipulations;
        this.startColor = startColor;
        this.maxManipulations = maxManipulations;
        this.colorLevels = colorLevels;
    }
    @Override
    public Minigame createMinigame() {
        return new ColorMixingMinigame(manipulations, startColor, maxManipulations, colorLevels);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameRegistry.COLOR_MIXING.get();
    }

    public static class ColorMixingMinigameSerializer extends MinigameSerializer<ColorMixingMinigameCreator> {
        public static final Codec<ColorManipulation> COLOR_MANIPULATION_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                        Codec.INT.fieldOf("maxIntensity").forGetter(colorManipulation -> colorManipulation.maxIntensity),
                        Codec.INT.fieldOf("intensity").forGetter(colorManipulation -> colorManipulation.intensity),
                        Codec.INT.listOf().comapFlatMap(
                                integer -> Util.fixedSize(integer, 3)
                                        .map(integers -> new Color(integers.get(0), integers.get(1), integers.get(2))),
                                color -> List.of(color.getRed(), color.getGreen(), color.getBlue())).fieldOf("color").forGetter(colorManipulation -> colorManipulation.color)

                ).apply(instance, ColorManipulation::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ColorManipulation> COLOR_MANIPULATION_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeInt(pValue.maxIntensity);
            pBuffer.writeInt(pValue.intensity);
            pBuffer.writeInt(pValue.color.getRed());
            pBuffer.writeInt(pValue.color.getGreen());
            pBuffer.writeInt(pValue.color.getBlue());
        }, (pBuffer) -> {
            int maxIntensity = pBuffer.readInt();
            int intensity = pBuffer.readInt();
            int R = pBuffer.readInt();
            int G = pBuffer.readInt();
            int B = pBuffer.readInt();
            Color color = new Color(R, G, B);
            return new ColorManipulation(maxIntensity, intensity, color);
        });
        public static final StreamCodec<RegistryFriendlyByteBuf, ColorMixingMinigameCreator> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
            pBuffer.writeCollection(pValue.manipulations, (buf, value) -> ColorMixingMinigameCreator.ColorMixingMinigameSerializer.COLOR_MANIPULATION_STREAM_CODEC.encode(((RegistryFriendlyByteBuf)buf), value));
            pBuffer.writeInt(pValue.startColor.getRed());
            pBuffer.writeInt(pValue.startColor.getGreen());
            pBuffer.writeInt(pValue.startColor.getBlue());
            pBuffer.writeInt(pValue.maxManipulations);
            pBuffer.writeInt(pValue.colorLevels);
        }, (pBuffer) -> {
            List<ColorManipulation> manipulations = pBuffer.readList((buf) -> ColorMixingMinigameCreator.ColorMixingMinigameSerializer.COLOR_MANIPULATION_STREAM_CODEC.decode(((RegistryFriendlyByteBuf)buf)));
            int startR = pBuffer.readInt();
            int startG = pBuffer.readInt();
            int startB = pBuffer.readInt();
            Color startColor = new Color(startR, startG, startB);
            int maxManipulations = pBuffer.readInt();
            int colorLevels = pBuffer.readInt();
            return new ColorMixingMinigameCreator(manipulations, startColor, maxManipulations, colorLevels);
        });
        public static final MapCodec<ColorMixingMinigameCreator> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                ColorMixingMinigameCreator.ColorMixingMinigameSerializer.COLOR_MANIPULATION_CODEC.listOf().fieldOf("colorManipulations").forGetter(minigame -> minigame.manipulations),
                Codec.INT.listOf().comapFlatMap(
                        integer -> Util.fixedSize(integer, 3)
                                .map(integers -> new Color(integers.get(0), integers.get(1), integers.get(2))),
                        color -> List.of(color.getRed(), color.getGreen(), color.getBlue())).fieldOf("startColor").forGetter(minigame -> minigame.startColor),
                Codec.INT.fieldOf("maxManipulations").forGetter(minigame -> minigame.maxManipulations),
                Codec.INT.fieldOf("colorLevels").forGetter(minigame -> minigame.colorLevels)
        ).apply(instance, ColorMixingMinigameCreator::new));
        @Override
        public MapCodec<ColorMixingMinigameCreator> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ColorMixingMinigameCreator> getStreamCodec() {
            return STREAM_CODEC;
        }
        public static class ColorManipulation {
            public ColorManipulation(int maxIntensity, int intensity, Color color) {
                this.maxIntensity = maxIntensity;
                this.intensity = intensity;
                this.color = color;
            }
            public int maxIntensity;
            public int intensity;
            public Color color;
            public float getIntensity() {
                return ((float)intensity)/((float)maxIntensity);
            }
        }
    }
}
