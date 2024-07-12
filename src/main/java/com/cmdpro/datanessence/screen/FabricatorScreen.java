package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(DataNEssence.MOD_ID, "textures/gui/fabricator.png");
    public FabricatorScreen(FabricatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    public float time;
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.25f);
        pGuiGraphics.renderItem(menu.blockEntity.item, x+145, y+35);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
        if (menu.blockEntity.getEssence() > 0) {
            pGuiGraphics.blit(TEXTURE, x + 8, y + 69 - (int) Math.ceil(52f * (menu.blockEntity.getEssence() / 1000f)), 178, 63 - (int) Math.ceil(52f * (menu.blockEntity.getEssence() / 1000f)), 7, (int) Math.ceil(52f * (menu.blockEntity.getEssence() / 1000f)));
        }
        if (menu.blockEntity.getLunarEssence() > 0) {
            pGuiGraphics.blit(TEXTURE, x + 19, y + 69 - (int) Math.ceil(52f * (menu.blockEntity.getLunarEssence() / 1000f)), 189, 63 - (int) Math.ceil(52f * (menu.blockEntity.getLunarEssence() / 1000f)), 7, (int) Math.ceil(52f * (menu.blockEntity.getLunarEssence() / 1000f)));
        }
        if (menu.blockEntity.getNaturalEssence() > 0) {
            pGuiGraphics.blit(TEXTURE, x + 30, y + 69 - (int) Math.ceil(52f * (menu.blockEntity.getNaturalEssence() / 1000f)), 200, 63 - (int) Math.ceil(52f * (menu.blockEntity.getNaturalEssence() / 1000f)), 7, (int) Math.ceil(52f * (menu.blockEntity.getNaturalEssence() / 1000f)));
        }
        if (menu.blockEntity.getExoticEssence() > 0) {
            pGuiGraphics.blit(TEXTURE, x + 41, y + 69 - (int) Math.ceil(52f * (menu.blockEntity.getExoticEssence() / 1000f)), 211, 63 - (int) Math.ceil(52f * (menu.blockEntity.getExoticEssence() / 1000f)), 7, (int) Math.ceil(52f * (menu.blockEntity.getExoticEssence() / 1000f)));
        }
        if (ClientPlayerData.getUnlockedEssences()[0]) {
            pGuiGraphics.blit(TEXTURE, x+7, y+6, 177, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences()[1]) {
            pGuiGraphics.blit(TEXTURE, x+18, y+6, 188, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences()[2]) {
            pGuiGraphics.blit(TEXTURE, x+29, y+6, 199, 0, 9, 9);
        }
        if (ClientPlayerData.getUnlockedEssences()[3]) {
            pGuiGraphics.blit(TEXTURE, x+40, y+6, 210, 0, 9, 9);
        }
        time += pPartialTick;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        List<FormattedCharSequence> component = new ArrayList<>();
        if (menu.blockEntity.getEssence() > 0) {
            if (pMouseX >= x + 8 && pMouseY >= y + (69 - 52)) {
                if (pMouseX <= x + 8+7 && pMouseY <= y + 69) {
                    component.clear();
                    if (ClientPlayerData.getUnlockedEssences()[0]) {
                        component.add(Component.translatable("gui.essence_bar.essence", menu.blockEntity.getEssence()).getVisualOrderText());
                    } else {
                        component.add(Component.translatable("gui.essence_bar.unknown", menu.blockEntity.getEssence()).getVisualOrderText());
                    }
                }
            }
        }
        if (menu.blockEntity.getLunarEssence() > 0) {
            if (pMouseX >= x + 19 && pMouseY >= y + (69 - 52)) {
                if (pMouseX <= x + 19+7 && pMouseY <= y + 69) {
                    component.clear();
                    if (ClientPlayerData.getUnlockedEssences()[1]) {
                        component.add(Component.translatable("gui.essence_bar.lunar_essence", menu.blockEntity.getLunarEssence()).getVisualOrderText());
                    } else {
                        component.add(Component.translatable("gui.essence_bar.unknown", menu.blockEntity.getLunarEssence()).getVisualOrderText());
                    }
                }
            }
        }
        if (menu.blockEntity.getNaturalEssence() > 0) {
            if (pMouseX >= x + 30 && pMouseY >= y + (69 - 52)) {
                if (pMouseX <= x + 30+7 && pMouseY <= y + 69) {
                    component.clear();
                    if (ClientPlayerData.getUnlockedEssences()[2]) {
                        component.add(Component.translatable("gui.essence_bar.natural_essence", menu.blockEntity.getNaturalEssence()).getVisualOrderText());
                    } else {
                        component.add(Component.translatable("gui.essence_bar.unknown", menu.blockEntity.getNaturalEssence()).getVisualOrderText());
                    }
                }
            }
        }
        if (menu.blockEntity.getExoticEssence() > 0) {
            if (pMouseX >= x + 41 && pMouseY >= y + (69 - 52)) {
                if (pMouseX <= x + 41+7 && pMouseY <= y + 69) {
                    component.clear();
                    if (ClientPlayerData.getUnlockedEssences()[3]) {
                        component.add(Component.translatable("gui.essence_bar.exotic_essence", menu.blockEntity.getExoticEssence()).getVisualOrderText());
                    } else {
                        component.add(Component.translatable("gui.essence_bar.unknown", menu.blockEntity.getExoticEssence()).getVisualOrderText());
                    }
                }
            }
        }
        if (component != null) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
