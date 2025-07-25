package com.cmdpro.datanessence.api.misc;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SymbolType {
    public static final ResourceLocation SYMBOL_LOCATION = DataNEssence.locate("textures/gui/symbols.png");
    public final SymbolLocation largeSymbol, normalSymbol, smallSymbol;
    public SymbolType(SymbolLocation largeSymbol, SymbolLocation normalSymbol, SymbolLocation smallSymbol) {
        this.largeSymbol = largeSymbol;
        this.normalSymbol = normalSymbol;
        this.smallSymbol = smallSymbol;
    }
    public void blitSmall(GuiGraphics graphics, int x, int y) {
        blit(smallSymbol, graphics, x, y, 8, 8);
    }
    public void blitNormal(GuiGraphics graphics, int x, int y) {
        blit(normalSymbol, graphics, x, y, 16, 16);
    }
    public void blitLarge(GuiGraphics graphics, int x, int y) {
        blit(largeSymbol, graphics, x, y, 32, 32);
    }
    private void blit(SymbolLocation symbolLocation, GuiGraphics graphics, int x, int y, int width, int height) {
        if (symbolLocation == null) {
            return;
        }
        graphics.blit(symbolLocation.texture, x, y, symbolLocation.x, symbolLocation.y, width, height);
    }
    public static SymbolLocation getSymbolLocation(ResourceLocation texture, int x, int y) {
        return new SymbolLocation(texture, x, y);
    }
    public static class SymbolLocation {
        protected SymbolLocation(ResourceLocation texture, int x, int y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
        }
        public ResourceLocation texture;
        public int x;
        public int y;
    }
}
