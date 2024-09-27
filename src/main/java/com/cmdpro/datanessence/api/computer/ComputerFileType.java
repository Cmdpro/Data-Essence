package com.cmdpro.datanessence.api.computer;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public abstract class ComputerFileType<T extends ComputerFile> {

    public abstract Codec<T> getCodec();
    public abstract StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    public abstract ComputerFileIcon getIcon(T file);
    public abstract void renderScreen(T file, GuiGraphics graphics, double mouseX, double mouseY, float delta, int xOffset, int yOffset);
    public void mouseClicked(T file, double pMouseX, double pMouseY, int pButton) {}
    public void mouseReleased(T file, double pMouseX, double pMouseY, int pButton) {}
    public void mouseDragged(T file, double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {}
    public void tick(T file) {}
    public void keyPressed(T file, int pKeyCode, int pScanCode, int pModifiers) {}
    public void keyReleased(T file, int pKeyCode, int pScanCode, int pModifiers) {}
    public class ComputerFileIcon {
        public ComputerFileIcon(ResourceLocation location, int u, int v, int width, int height) {
            this.location = location;
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }
        public ResourceLocation location;
        public int u, v, width, height;
    }
}
