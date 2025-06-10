package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBarBackgroundType;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientEssenceBarUtil {
    public static void drawEssenceBarTiny(GuiGraphics graphics, int x, int y, EssenceType type, float amount, float full) {
        drawEssenceBarTiny(graphics, x, y, type, amount, full, null);
    }
    public static void drawEssenceBarTiny(GuiGraphics graphics, int x, int y, EssenceType type, float amount, float full, EssenceBarBackgroundType backgroundType) {
        if (backgroundType != null) {
            graphics.blit(backgroundType.tinyBarSprite.texture, x-1, y-1, backgroundType.tinyBarSprite.x, backgroundType.tinyBarSprite.y, 5, 24);
        }
        ResourceLocation fill = type.getTinyBarSprite().texture;
        int u = type.getTinyBarSprite().x;
        int v = type.getTinyBarSprite().y;
        if (amount > 0) {
            graphics.blit(fill, x, y + 22 - (int) Math.ceil(22f * (amount / full)), u, v + 22 - (int) Math.ceil(22f * (amount / full)), 3, (int) Math.ceil(22f * (amount / full)));
        }
    }

    public static Component getEssenceBarTooltipTiny(double mouseX, double mouseY, int x, int y, EssenceType type, float amount) {
        if (amount > 0) {
            if (mouseX <= x + 3 && mouseY <= y + 22 && mouseX >= x && mouseY >= y) {
                if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(type), false)) {
                    return Component.translatable(type.getTooltipKey(), amount);
                } else {
                    return Component.translatable("gui.essence_bar.unknown", amount);
                }
            }
        }
        return null;
    }

    public static void drawEssenceBar(GuiGraphics graphics, int x, int y, EssenceType type, float amount, float full) {
        drawEssenceBar(graphics, x, y, type, amount, full, null);
    }
    public static void drawEssenceBar(GuiGraphics graphics, int x, int y, EssenceType type, float amount, float full, EssenceBarBackgroundType backgroundType) {
        if (backgroundType != null) {
            graphics.blit(backgroundType.bigBarSprite.texture, x-1, y-1, backgroundType.bigBarSprite.x, backgroundType.bigBarSprite.y, 9, 54);
        }
        if (amount > 0) {
            ResourceLocation fill = type.getBigBarSprite().texture;
            int u = type.getBigBarSprite().x;
            int v = type.getBigBarSprite().y;
            graphics.blit(fill, x, y + 52 - (int) Math.ceil(52f * (amount / full)), u, v + 52 - (int) Math.ceil(52f * (amount / full)), 7, (int) Math.ceil(52f * (amount / full)));
        }
    }
    public static void drawEssenceIcon(GuiGraphics graphics, int x, int y, EssenceType type) {
        drawEssenceIcon(graphics, x, y, type, null, false, true);
    }
    public static void drawEssenceIcon(GuiGraphics graphics, int x, int y, EssenceType type, EssenceBarBackgroundType backgroundType, boolean unlocked) {
        drawEssenceIcon(graphics, x, y, type, backgroundType, true, unlocked);
    }
    public static void drawEssenceIcon(GuiGraphics graphics, int x, int y, EssenceType type, EssenceBarBackgroundType backgroundType, boolean drawBackground, boolean unlocked) {
        if (!unlocked && backgroundType == null) {
            return;
        }
        ResourceLocation texture = type.getIconSprite().texture;
        int u = type.getIconSprite().x;
        int v = type.getIconSprite().y;
        if (!unlocked) {
            texture = backgroundType.unknownIconSprite.texture;
            u = backgroundType.unknownIconSprite.x;
            v = backgroundType.unknownIconSprite.y;
        }
        if (backgroundType != null && drawBackground) {
            graphics.blit(backgroundType.iconSprite.texture, x, y, backgroundType.iconSprite.x, backgroundType.iconSprite.y, 9, 9);
        }
        graphics.blit(texture, x, y, u, v, 9, 9);
    }

    public static Component getEssenceBarTooltip(double mouseX, double mouseY, int x, int y, EssenceType type, float amount) {
        if (amount > 0) {
            if (mouseX >= x && mouseY >= y) {
                if (mouseX <= x + 7 && mouseY <= y + 52) {
                    if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(type), false)) {
                        return Component.translatable(type.getTooltipKey(), amount);
                    } else {
                        return Component.translatable("gui.essence_bar.unknown", amount);
                    }
                }
            }
        }
        return null;
    }
}
