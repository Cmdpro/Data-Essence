package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.block.logic.AbstractSignalConditionBlockEntity;
import com.cmdpro.datanessence.block.logic.SignalConditionMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SignalConditionScreen extends AbstractContainerScreen<SignalConditionMenu> {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("datanessence", "textures/gui/signal_condition.png");

    private Button modeIdButton;
    private Button modeAnyButton;
    private Button modeAllButton;
    private Button ltButton;
    private Button eqButton;
    private Button gtButton;

    private final List<Button> charButtons = new ArrayList<>();

    public SignalConditionScreen(SignalConditionMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top = this.topPos;

        int btnSize = 16;
        int pad = 2;
        int cols = 10;

        int startX = left + 8;
        int startY = top + 20;

        // digits
        for (int i = 0; i < 10; i++) {
            int x = startX + (i % cols) * (btnSize + pad);
            int y = startY;
            int buttonId = SignalConditionMenu.BTN_DIGIT_BASE + i;
            char symbol = (char) ('0' + i);
            Button b = addRenderableWidget(Button.builder(
                            Component.literal(String.valueOf(symbol)),
                            btn -> sendButtonPress(buttonId))
                    .pos(x, y)
                    .size(btnSize, btnSize)
                    .build());
            charButtons.add(b);
        }

        startY += btnSize + 6;

        // letters
        for (int i = 0; i < 26; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (btnSize + pad);
            int y = startY + row * (btnSize + pad);
            int buttonId = SignalConditionMenu.BTN_LETTER_BASE + i;
            char symbol = (char) ('A' + i);
            Button b = addRenderableWidget(Button.builder(
                            Component.literal(String.valueOf(symbol)),
                            btn -> sendButtonPress(buttonId))
                    .pos(x, y)
                    .size(btnSize, btnSize)
                    .build());
            charButtons.add(b);
        }

        // mode buttons: ID / ∃ / ∀
        int modeY = top + 20;
        int modeX = left + 8 + cols * (btnSize + pad) + 4;

        modeIdButton = addRenderableWidget(Button.builder(
                        Component.literal("ID"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_MODE_ID))
                .pos(modeX, modeY)
                .size(24, 16)
                .build());

        modeAnyButton = addRenderableWidget(Button.builder(
                        Component.literal("∃"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_MODE_ANY))
                .pos(modeX, modeY + 20)
                .size(24, 16)
                .build());

        modeAllButton = addRenderableWidget(Button.builder(
                        Component.literal("∀"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_MODE_ALL))
                .pos(modeX, modeY + 40)
                .size(24, 16)
                .build());

        // comparator buttons: < = >
        int compY = top + this.imageHeight - 60;
        int compX = left + 8;

        ltButton = addRenderableWidget(Button.builder(
                        Component.literal("<"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_COMP_LT))
                .pos(compX, compY)
                .size(18, 18)
                .build());

        eqButton = addRenderableWidget(Button.builder(
                        Component.literal("="),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_COMP_EQ))
                .pos(compX + 20, compY)
                .size(18, 18)
                .build());

        gtButton = addRenderableWidget(Button.builder(
                        Component.literal(">"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_COMP_GT))
                .pos(compX + 40, compY)
                .size(18, 18)
                .build());

        // amount controls
        int thrY = compY + 22;
        int thrX = left + 8;

        addRenderableWidget(Button.builder(
                        Component.literal("-"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_DEC))
                .pos(thrX, thrY)
                .size(20, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal("+"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_INC))
                .pos(thrX + 24, thrY)
                .size(20, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal("-10"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_DEC10))
                .pos(thrX + 52, thrY)
                .size(26, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal("+10"),
                        btn -> sendButtonPress(SignalConditionMenu.BTN_INC10))
                .pos(thrX + 82, thrY)
                .size(26, 20)
                .build());
    }

    private void sendButtonPress(int buttonId) {
        Minecraft.getInstance().gameMode
                .handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        gfx.blit(BG_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gfx, mouseX, mouseY, partialTicks);
        super.render(gfx, mouseX, mouseY, partialTicks);

        int x = leftPos + 8;
        int y = topPos + 6;

        int idMode = menu.getIdMode();
        String idStr;
        if (idMode == AbstractSignalConditionBlockEntity.ID_MODE_ANY) {
            idStr = "Input: ∃";
        } else if (idMode == AbstractSignalConditionBlockEntity.ID_MODE_ALL) {
            idStr = "Input: ∀";
        } else {
            idStr = "Input: " + menu.getIdChar();
        }

        String compStr = switch (menu.getComparison()) {
            case AbstractSignalConditionBlockEntity.COMP_LESS -> "<";
            case AbstractSignalConditionBlockEntity.COMP_EQUAL -> "=";
            case AbstractSignalConditionBlockEntity.COMP_GREATER -> ">";
            default -> "?";
        };

        String summary = idStr + "  " + compStr + "  " + menu.getThreshold();
        gfx.drawString(this.font, summary, x, y, 0xFFFFFF, false);

        this.renderTooltip(gfx, mouseX, mouseY);
    }
}
