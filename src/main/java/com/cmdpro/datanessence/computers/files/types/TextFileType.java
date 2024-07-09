package com.cmdpro.datanessence.computers.files.types;

import com.cmdpro.datanessence.computers.ComputerFileType;
import com.cmdpro.datanessence.computers.files.TextFile;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.GsonHelper;

public class TextFileType extends ComputerFileType<TextFile> {
    @Override
    public void toNetwork(FriendlyByteBuf buf, TextFile data) {
        buf.writeComponent(data.text);
    }

    @Override
    public TextFile fromNetwork(FriendlyByteBuf buf) {
        Component text = buf.readComponent();
        return new TextFile(text);
    }

    @Override
    public TextFile fromJson(JsonObject obj) {
        return new TextFile(ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, obj.get("text")).get().left().get());
    }

    @Override
    public void renderScreen(TextFile file, GuiGraphics graphics, double mouseX, double mouseY, float delta, int xOffset, int yOffset) {

    }
}
