package com.cmdpro.datanessence.screen.widget;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.c2s.PlayerSetItemHandlerLocked;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LockWidget extends AbstractWidget {
    public static final ResourceLocation TEXTURE = DataNEssence.locate("textures/gui/widgets.png");
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
            this.setTooltip(Tooltip.create(Component.translatable("tooltip.datanessence.toggle_lock")));
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        ModMessages.sendToServer(new PlayerSetItemHandlerLocked(entity.getBlockPos(), !handler.locked));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK.value(), 1.0F));
    }

}
