package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.misc.SymbolType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.HashMap;

public class DataTabletPuzzleScreen extends Screen {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/data_tablet_puzzle.png");
    public HashMap<Vector2i, Alcove> alcoves;
    public int offsetX;
    public int offsetY;
    public SymbolType selectedSymbol;
    public int selectedNumeral;
    public int introTicks = 200;

    public DataTabletPuzzleScreen(Component title) {
        super(title);
        offsetX = (width / 256) / 2;
        offsetY = (height / 166) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (introTicks > 0) {
            // startup thing here
            introTicks--;
        } else {
            // real rendering
        }
    }

    public void renderPuzzleElements(GuiGraphics graphics, int mouseX, int mouseY, float delta, int offsetX, int offsetY) {
//        drawAlcove(graphics, offsetX+68, offsetY+34);
//        drawAlcove(graphics, offsetX+68, offsetY+96);
//        drawAlcove(graphics, offsetX+119, offsetY+11);
//        drawAlcove(graphics, offsetX+119, offsetY+65);
//        drawAlcove(graphics, offsetX+119, offsetY+119);
//        drawAlcove(graphics, offsetX+170, offsetY+34);
//        drawAlcove(graphics, offsetX+170, offsetY+96);
    }

    public class Alcove {
        int x;
        int y;
        SymbolType symbol;
        int numeral;

        public Alcove(int x, int y) {
            this.x = x;
            this.y = y;
            this.symbol = null;
            this.numeral = 0;
        }

        public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.blit(TEXTURE, offsetX+x, offsetY+y, 0, 166, 18, 36);
            // draw numeral and symbol if set, too
        }
    }
}
