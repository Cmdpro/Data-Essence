package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.init.MinigameInit;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2i;

public class TestMinigame extends Minigame {
    public Vector2i playerPos;
    public Vector2i startPos;
    public TestMinigame(Vector2i playerPos) {
        this.playerPos = playerPos;
        startPos = playerPos;
    }
    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public MinigameCreator getCreator() {
        return MinigameInit.TESTMINIGAME.get();
    }

    @Override
    public void reset() {
        playerPos = startPos;
    }

    @Override
    public void render(DataBankScreen screen, GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int x, int y) {
        pGuiGraphics.fill(playerPos.x-4, playerPos.y-4, playerPos.x+4, playerPos.y+4, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        if (Minecraft.getInstance().options.keyJump.isDown()) {
            finished = true;
        }
        if (Minecraft.getInstance().options.keyUp.isDown()) {
            playerPos.y -= 2;
        }
        if (Minecraft.getInstance().options.keyDown.isDown()) {
            playerPos.y += 2;
        }
        if (Minecraft.getInstance().options.keyLeft.isDown()) {
            playerPos.x -= 2;
        }
        if (Minecraft.getInstance().options.keyRight.isDown()) {
            playerPos.x += 2;
        }
    }
    public boolean finished;
}
