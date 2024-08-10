package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import com.cmdpro.datanessence.screen.widget.FluidWidget;
import com.cmdpro.datanessence.screen.widget.LockWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class FluidMixerScreen extends AbstractContainerScreen<FluidMixerMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/fluid_mixer.png");
    public FluidMixerScreen(FluidMixerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        addRenderableWidget(new FluidWidget(x+39, y+22, menu.blockEntity.getFluidHandler(), 0));
        addRenderableWidget(new FluidWidget(x+39, y+45, menu.blockEntity.getFluidHandler(), 1));
        addRenderableWidget(new FluidWidget(x+116, y+34, menu.blockEntity.getOutputHandler(), 0));
        addRenderableWidget(new LockWidget(menu.blockEntity, (LockableItemHandler)menu.blockEntity.getItemHandler(), x+84, y+52));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        ClientDataNEssenceUtil.renderLockedSlotBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY, x, y, menu.slots);
        if (menu.blockEntity.workTime >= 0) {
            pGuiGraphics.blit(TEXTURE, x + 82, y + 33, 188, 0, (int) Math.ceil(22f * ((float)menu.blockEntity.workTime / 20f)), 17);
        }
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBar(pGuiGraphics, x+8, y+17, 0, menu.blockEntity.getEssence(), menu.blockEntity.getMaxEssence());
        if (ClientPlayerData.getUnlockedEssences()[0]) {
            pGuiGraphics.blit(TEXTURE, x+7, y+6, 177, 0, 9, 9);
        }
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        List<FormattedCharSequence> component = new ArrayList<>();
        Component essence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+8, y+17, 0, menu.blockEntity.getEssence());
        if (essence != null) {
            component.clear();
            component.add(essence.getVisualOrderText());
        }
        if (component != null) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
