package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.block.logic.DisplaySignalBlockEntity;
import com.cmdpro.datanessence.block.logic.DisplaySignalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DisplaySignalScreen extends AbstractContainerScreen<DisplaySignalMenu> {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("datanessence", "textures/gui/display_signal.png");

    public DisplaySignalScreen(DisplaySignalMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 120;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top = this.topPos;

        addRenderableWidget(Button.builder(
                        Component.literal("<"),
                        b -> sendButton(DisplaySignalMenu.BTN_PREV))
                .pos(left + 20, top + 80)
                .size(20, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal(">"),
                        b -> sendButton(DisplaySignalMenu.BTN_NEXT))
                .pos(left + 60, top + 80)
                .size(20, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal("0"),
                        b -> sendButton(DisplaySignalMenu.BTN_RESET))
                .pos(left + 100, top + 80)
                .size(20, 20)
                .build());
    }

    private void sendButton(int id) {
        Minecraft.getInstance().gameMode
                .handleInventoryButtonClick(this.menu.containerId, id);
    }

    private DisplaySignalBlockEntity getBE() {
        Level level = minecraft.level;
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(menu.getPos());
        return be instanceof DisplaySignalBlockEntity d ? d : null;
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

        DisplaySignalBlockEntity be = getBE();
        if (be != null) {
            String id = be.getDisplayId();
            int amount = be.getDisplayAmount();
            int idx = menu.getSelectedIndex();
            int count = be.getSignalCount();

            int x = leftPos + 8;
            int y = topPos + 8;

            gfx.drawString(this.font,
                    "Index: " + idx + " / " + (count - 1),
                    x, y, 0xFFFFFF, false);
            gfx.drawString(this.font,
                    "Id: " + (id.isEmpty() ? "-" : id),
                    x, y + 10, 0xFFFFFF, false);
            gfx.drawString(this.font,
                    "Amount: " + amount,
                    x, y + 20, 0xFFFFFF, false);
        }

        this.renderTooltip(gfx, mouseX, mouseY);
    }
}
