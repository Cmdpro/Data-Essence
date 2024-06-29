package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.google.common.primitives.Chars;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2i;

public class TestMinigame extends Minigame {
    public Vector2i playerPos;
    public TestMinigame(Vector2i playerPos) {
        this.playerPos = playerPos;
    }
    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        pGuiGraphics.fill(x+playerPos.x-4, y+playerPos.y-4, x+playerPos.x+4, y+playerPos.y+4, 0xFFFFFFFF);
    }


    @Override
    public void keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        super.keyPressed(pKeyCode, pScanCode, pModifiers);
        if (pKeyCode == Chars.hashCode(' ')) {
            finished = true;
        }
    }

    @Override
    public void mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        playerPos.x = (int)pMouseX+(int)pDragX;
        playerPos.y = (int)pMouseY+(int)pDragX;
    }


    public boolean finished;
}
