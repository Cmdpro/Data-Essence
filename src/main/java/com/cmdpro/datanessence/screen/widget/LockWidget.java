package com.cmdpro.datanessence.screen.widget;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LockWidget extends AbstractWidget {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/widgets.png");
    public LockableItemHandler handler;
    public LockWidget(LockableItemHandler handler, int pX, int pY) {
        super(pX-1, pY-1, 18, 18, Component.empty());
        this.handler = handler;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (isHovered) {
            if (handler.locked) {
                pGuiGraphics.blit(TEXTURE, getX(), getY(), 0, 18, 18, 18);
            } else {
                pGuiGraphics.blit(TEXTURE, getX(), getY(), 18, 18, 18, 18);
            }
        } else {
            if (handler.locked) {
                pGuiGraphics.blit(TEXTURE, getX(), getY(), 0, 0, 18, 18);
            } else {
                pGuiGraphics.blit(TEXTURE, getX(), getY(), 18, 0, 18, 18);
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        //Probably will need to use a custom packet but im taking a break
        handler.locked = !handler.locked;
        if (handler.locked) {
            handler.setLockedSlots();
        } else {
            handler.clearLockedSlots();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
