package com.cmdpro.datanessence.computers.files.types;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.cmdpro.datanessence.computers.files.TextFile;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextFileType extends ComputerFileType<TextFile> {
    public static final MapCodec<TextFile> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter((file) -> file.text)
    ).apply(instance, TextFile::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TextFile> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.text);
    }, pBuffer -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        return new TextFile(text);
    });

    @Override
    public MapCodec<TextFile> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TextFile> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public ComputerFileIcon getIcon(TextFile file) {
        return new ComputerFileIcon(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/computer.png"), 0, 166, 16, 16);
    }

    @Override
    public void renderScreen(TextFile file, GuiGraphics graphics, double mouseX, double mouseY, float delta, int xOffset, int yOffset) {
        MutableComponent component = file.text.copy();
        List<FormattedCharSequence> text = Minecraft.getInstance().font.split(component, DataTabletScreen.imageWidth - 8);
        int offsetY = 0;
        for (FormattedCharSequence i : text) {
            graphics.drawString(Minecraft.getInstance().font, i, xOffset + 4, yOffset + 4 + offsetY, 0xFFFFFFFF);
            offsetY += Minecraft.getInstance().font.lineHeight+2;
        }
    }
}
