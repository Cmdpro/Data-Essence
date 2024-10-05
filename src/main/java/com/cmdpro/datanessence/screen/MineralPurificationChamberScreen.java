package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.api.util.client.ClientEssenceBarUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
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
import org.jline.reader.Widget;

import java.util.ArrayList;
import java.util.List;

public class MineralPurificationChamberScreen extends AbstractContainerScreen<MineralPurificationChamberMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/mineral_purification_chamber.png");
    public MineralPurificationChamberScreen(MineralPurificationChamberMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if (menu.blockEntity.workTime >= 0 && menu.blockEntity.maxTime != -1) {
            pGuiGraphics.blit(TEXTURE, x + 82, y + 33, 188, 0, (int) Math.ceil(22f * ((float)menu.blockEntity.workTime / menu.blockEntity.maxTime)), 17);
        }
        ClientEssenceBarUtil.drawEssenceBar(pGuiGraphics, x+8, y+17, EssenceTypeRegistry.ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()), menu.blockEntity.getStorage().getMaxEssence());
        if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()), false)) {
            pGuiGraphics.blit(TEXTURE, x+7, y+6, 177, 0, 9, 9);
        }
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        addRenderableWidget(new FluidWidget(x+26, y+28, 16, 28, menu.blockEntity.getWaterHandler(), 0));
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        List<FormattedCharSequence> component = new ArrayList<>();
        Component essence = ClientEssenceBarUtil.getEssenceBarTooltip(pMouseX, pMouseY, x+8, y+17, EssenceTypeRegistry.ESSENCE.get(), menu.blockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()));
        if (essence != null) {
            component.clear();
            component.add(essence.getVisualOrderText());
        }
        if (!component.isEmpty()) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
