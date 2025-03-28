package com.cmdpro.datanessence.computers.files.types;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.cmdpro.datanessence.computers.files.TextFile;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.ibm.icu.lang.UCharacter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.Language;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextFileType extends ComputerFileType<TextFile> {
    public static final MapCodec<TextFile> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter((file) -> file.text),
            Codec.BOOL.optionalFieldOf("rtl", false).forGetter(page -> page.rtl)
    ).apply(instance, TextFile::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TextFile> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        ComponentSerialization.STREAM_CODEC.encode(pBuffer, pValue.text);
        pBuffer.writeBoolean(pValue.rtl);
    }, pBuffer -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(pBuffer);
        boolean rtl = pBuffer.readBoolean();
        return new TextFile(text, rtl);
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
        List<FormattedText> text = Minecraft.getInstance().font.getSplitter().splitLines(component, DataTabletScreen.imageWidth - 8, Style.EMPTY);
        int offsetY = 0;
        for (FormattedText i : text) {
            int x = xOffset + 4;
            FormattedCharSequence formattedCharSequence = FormattedBidiReorder.reorder(i, Language.getInstance().isDefaultRightToLeft());
            if (file.rtl) {
                x = xOffset+((DataTabletScreen.imageWidth - 4)-Minecraft.getInstance().font.width(i));
                SubStringSource substr = SubStringSource.create(i, UCharacter::getMirror, (str) -> str);
                formattedCharSequence = FormattedCharSequence.composite(substr.substring(0, substr.getPlainText().length(), true));
            }
            graphics.drawString(Minecraft.getInstance().font, formattedCharSequence, x, yOffset + 4 + offsetY, 0xFFFFFFFF);
            offsetY += Minecraft.getInstance().font.lineHeight+2;
        }
    }
}
