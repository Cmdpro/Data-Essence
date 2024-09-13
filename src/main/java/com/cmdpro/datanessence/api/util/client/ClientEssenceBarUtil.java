package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientEssenceBarUtil {
    public static void drawEssenceBarTiny(GuiGraphics graphics, int x, int y, EssenceType type, float amount, float full) {
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
        if (amount > 0) {
            ResourceLocation fill = type.getBigBarSprite().texture;
            int u = type.getBigBarSprite().x;
            int v = type.getBigBarSprite().y;
            graphics.blit(fill, x, y + 52 - (int) Math.ceil(52f * (amount / full)), u, v + 52 - (int) Math.ceil(52f * (amount / full)), 7, (int) Math.ceil(52f * (amount / full)));
        }
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
