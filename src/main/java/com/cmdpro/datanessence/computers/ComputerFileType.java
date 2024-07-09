package com.cmdpro.datanessence.computers;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;

public abstract class ComputerFileType<T extends ComputerFile> {
    
    public abstract void toNetwork(FriendlyByteBuf buf, T data);
    public abstract T fromNetwork(FriendlyByteBuf buf);
    public abstract T fromJson(JsonObject obj);
    public abstract void renderScreen(T file, GuiGraphics graphics, double mouseX, double mouseY, float delta, int xOffset, int yOffset);
}
