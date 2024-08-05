package com.cmdpro.datanessence.computers.files.types;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.computers.ComputerFileType;
import com.cmdpro.datanessence.computers.files.TextFile;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class TextFileType extends ComputerFileType<TextFile> {
    @Override
    public void toNetwork(FriendlyByteBuf buf, TextFile data) {
        buf.writeWithCodec(NbtOps.INSTANCE, ComponentSerialization.CODEC, data.text);
    }

    @Override
    public TextFile fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readWithCodecTrusted(NbtOps.INSTANCE, ComponentSerialization.CODEC);
        return new TextFile(text);
    }
    @Override
    public TextFile fromJson(JsonObject obj) {
        return new TextFile(ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, obj.get("text")).result().get());
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
