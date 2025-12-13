package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.block.logic.SignalMathBlockEntity;
import com.cmdpro.datanessence.block.logic.SignalMathMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SignalMathScreen extends AbstractSignalScreen<SignalMathMenu> {

    private Button forEachButton;

    public SignalMathScreen(SignalMathMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top  = this.topPos;

        int opsY = top + this.imageHeight - 52;
        int x    = left + 8;
        int w    = 30;
        int h    = 16;
        int pad  = 2;

        String[] labels = new String[] {
                "+", "-", "×", "÷", "^", "√",
                "&", "|", "^b", "<<", ">>", ">>>"
        };

        for (int i = 0; i < labels.length; i++) {
            final int buttonId = SignalMathMenu.BTN_OP_BASE + i;
            int bx = x + (w + pad) * (i % 6);
            int by = opsY + (h + pad) * (i / 6);

            addRenderableWidget(Button.builder(Component.literal(labels[i]),
                            btn -> sendButtonPress(buttonId))
                    .pos(bx, by)
                    .size(w, h)
                    .build());
        }

        forEachButton = addRenderableWidget(Button.builder(
                        Component.literal(menu.isForEach() ? "∀" : "∃"),
                        btn -> {
                            sendButtonPress(SignalMathMenu.BTN_TOGGLE_FOREACH);
                        })
                .pos(left + 8, opsY - 18)
                .size(24, 16)
                .build());
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (forEachButton != null) {
            forEachButton.setMessage(Component.literal(menu.isForEach() ? "∀" : "∃"));
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        super.render(gfx, mouseX, mouseY, partialTicks);

        // Extra text: show which op and that "Amount" is operand
        String opText = "Op: " + menu.getOperationIndex() + " (Amount = operand)";
        gfx.drawString(this.font, opText, leftPos + 8, topPos + 16, 0xFFFFFF, false);
    }
}
