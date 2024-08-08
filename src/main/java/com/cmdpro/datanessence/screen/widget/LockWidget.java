package com.cmdpro.datanessence.screen.widget;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ILockableContainer;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.PlayerSetItemHandlerLockedC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LockWidget extends AbstractWidget {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/widgets.png");
    public BlockEntity entity;
    public LockableItemHandler handler;
    public LockWidget(BlockEntity entity, LockableItemHandler handler, int pX, int pY) {
        super(pX, pY, 18, 18, Component.empty());
        this.entity = entity;
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
        ModMessages.sendToServer(new PlayerSetItemHandlerLockedC2SPacket(entity.getBlockPos(), !handler.locked));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
