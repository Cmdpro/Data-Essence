package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.block.logic.SignalEmitterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SignalEmitterScreen extends AbstractContainerScreen<SignalEmitterMenu> {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("datanessence", "textures/gui/signal_emitter.png");

    private static final char[] DIGITS  = "0123456789".toCharArray();
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    // blocks grid config
    private static final int BLOCK_COLS = 9;
    private static final int BLOCK_ROWS = 5;
    private static final int BLOCK_SLOT_SIZE = 18; // 16x16 + 2px padding

    private int blockScrollOffset = 0;

    private Button charsTabButton;
    private Button blocksTabButton;

    private final java.util.List<Button> charButtons = new java.util.ArrayList<>();

    private ItemStack hoveredBlockStack = ItemStack.EMPTY;

    public SignalEmitterScreen(SignalEmitterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top  = this.topPos;

        // tabs
        int tabY = top - 20; // above the frame
        charsTabButton = addRenderableWidget(Button.builder(Component.literal("Chars"),
                        btn -> sendButtonPress(SignalEmitterMenu.BTN_TAB_CHARS))
                .pos(left + 8, tabY)
                .size(50, 18)
                .build());

        blocksTabButton = addRenderableWidget(Button.builder(Component.literal("Blocks"),
                        btn -> sendButtonPress(SignalEmitterMenu.BTN_TAB_BLOCKS))
                .pos(left + 62, tabY)
                .size(50, 18)
                .build());

        // digits row (top)
        int btnSize = 16;
        int pad = 2;
        int cols = 10;

        int startX = left + 8;
        int startY = top + 20;

        for (int i = 0; i < DIGITS.length; i++) {
            int x = startX + (i % cols) * (btnSize + pad);
            int y = startY;
            final int buttonId = SignalEmitterMenu.DIGIT_BASE + i;
            char symbol = DIGITS[i];

            Button digitButton = addRenderableWidget(Button.builder(
                            Component.literal(String.valueOf(symbol)),
                            btn -> sendButtonPress(buttonId))
                    .pos(x, y)
                    .size(btnSize, btnSize)
                    .build());

            charButtons.add(digitButton); //mark as char-tab-only
        }


        //letters grid under the digits
        startY += btnSize + 6; // small gap

        for (int i = 0; i < LETTERS.length; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (btnSize + pad);
            int y = startY + row * (btnSize + pad);
            final int buttonId = SignalEmitterMenu.LETTER_BASE + i;
            char symbol = LETTERS[i];

            Button letterButton = addRenderableWidget(Button.builder(
                            Component.literal(String.valueOf(symbol)),
                            btn -> sendButtonPress(buttonId))
                    .pos(x, y)
                    .size(btnSize, btnSize)
                    .build());

            charButtons.add(letterButton); //mark as char-tab-only
        }


        //amount controls at bottom
        int amountY = top + this.imageHeight - 32;
        int amountX = left + 8;

        addRenderableWidget(Button.builder(Component.literal("-"),
                        btn -> sendButtonPress(SignalEmitterMenu.BTN_DEC))
                .pos(amountX, amountY)
                .size(20, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("+"),
                        btn -> sendButtonPress(SignalEmitterMenu.BTN_INC))
                .pos(amountX + 24, amountY)
                .size(20, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("-10"),
                        btn -> sendButtonPress(SignalEmitterMenu.BTN_DEC10))
                .pos(amountX + 52, amountY)
                .size(26, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("+10"),
                        btn -> sendButtonPress(SignalEmitterMenu.BTN_INC10))
                .pos(amountX + 82, amountY)
                .size(26, 20)
                .build());
    }

    private void sendButtonPress(int buttonId) {
        Minecraft.getInstance().gameMode
                .handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        boolean blockMode = menu.isBlockMode();

        // Both tabs remain clickable / maybe change this, but it was buggy and im lazy
        if (charsTabButton != null)  charsTabButton.active  = true;
        if (blocksTabButton != null) blocksTabButton.active = true;

        //hide letters/digits in block mode
        for (Button btn : charButtons) {
            btn.visible = !blockMode;
            btn.active  = !blockMode;
        }
    }



    //scrolling for blocks tab

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // vertical scroll amount is scrollY
        double delta = scrollY;

        if (menu.isBlockMode() && isMouseOverBlockArea(mouseX, mouseY)) {
            List<Block> allBlocks = SignalEmitterMenu.getAllBlocks();
            int visibleCount = BLOCK_COLS * BLOCK_ROWS;
            int maxOffset = Math.max(0, allBlocks.size() - visibleCount);

            int step = BLOCK_COLS; // scroll by one row
            int dir  = (int) Math.signum(delta) * step * -1; // wheel up = previous row

            blockScrollOffset = Mth.clamp(blockScrollOffset + dir, 0, maxOffset);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu.isBlockMode() && isMouseOverBlockArea(mouseX, mouseY)) {
            // handle block selection click before buttons
            int index = getBlockIndexAt(mouseX, mouseY);
            List<Block> allBlocks = SignalEmitterMenu.getAllBlocks();
            if (index >= 0 && index < allBlocks.size()) {
                sendButtonPress(SignalEmitterMenu.BLOCK_BASE + index);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverBlockArea(double mouseX, double mouseY) {
        int areaX = leftPos + 8;
        int areaY = topPos + 20;
        int w = BLOCK_COLS * BLOCK_SLOT_SIZE;
        int h = BLOCK_ROWS * BLOCK_SLOT_SIZE;
        return mouseX >= areaX && mouseX < areaX + w
                && mouseY >= areaY && mouseY < areaY + h;
    }

    private int getBlockIndexAt(double mouseX, double mouseY) {
        int areaX = leftPos + 8;
        int areaY = topPos + 20;

        int relX = (int) (mouseX - areaX);
        int relY = (int) (mouseY - areaY);

        int col = relX / BLOCK_SLOT_SIZE;
        int row = relY / BLOCK_SLOT_SIZE;

        if (col < 0 || col >= BLOCK_COLS || row < 0 || row >= BLOCK_ROWS) return -1;

        int idx = blockScrollOffset + row * BLOCK_COLS + col;
        return idx;
    }

    //rendering

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        gfx.blit(BG_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gfx, mouseX, mouseY, partialTicks);
        super.render(gfx, mouseX, mouseY, partialTicks);

        // summary line
        String sig = menu.getSignalId();
        int amount = menu.getAmount();
        String text = "Signal: " + sig + "  Amount: " + amount;
        gfx.drawString(this.font, text, leftPos + 8, topPos + 6, 0xFFFFFF, false);

        hoveredBlockStack = ItemStack.EMPTY;

        if (menu.isBlockMode()) {
            renderBlocksTab(gfx, mouseX, mouseY);
        }

        // default tooltip (buttons etc.)
        this.renderTooltip(gfx, mouseX, mouseY);

        // block tooltip
        if (!hoveredBlockStack.isEmpty()) {
            gfx.renderTooltip(this.font, hoveredBlockStack, mouseX, mouseY);
        }
    }

    private void renderBlocksTab(GuiGraphics gfx, int mouseX, int mouseY) {
        List<Block> allBlocks = SignalEmitterMenu.getAllBlocks();
        int selectedIndex = menu.isBlockSignal() ? menu.getBlockIndex() : -1;


        int areaX = leftPos + 8;
        int areaY = topPos + 20;

        hoveredBlockStack = ItemStack.EMPTY;

        for (int row = 0; row < BLOCK_ROWS; row++) {
            for (int col = 0; col < BLOCK_COLS; col++) {
                int index = blockScrollOffset + row * BLOCK_COLS + col;
                if (index < 0 || index >= allBlocks.size()) continue;

                int x = areaX + col * BLOCK_SLOT_SIZE;
                int y = areaY + row * BLOCK_SLOT_SIZE;

                Block block = allBlocks.get(index);
                ItemStack stack = new ItemStack(block.asItem());

                // slot background highlight if selected
                if (index == selectedIndex) {
                    // simple colored rect behind item
                    gfx.fill(x - 1, y - 1, x + 17, y + 17, 0x80FFFFFF);
                }

                // draw item icon
                gfx.renderItem(stack, x, y);
                gfx.renderItemDecorations(this.font, stack, x, y);

                // hover detection for tooltip
                if (isPointInRect(mouseX, mouseY, x, y, 16, 16)) {
                    hoveredBlockStack = stack;
                }
            }
        }
    }

    private boolean isPointInRect(double mouseX, double mouseY,
                                  int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w &&
                mouseY >= y && mouseY < y + h;
    }
}
