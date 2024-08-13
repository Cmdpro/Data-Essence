package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientEssenceBarUtil {
    public static void drawEssenceBarTiny(GuiGraphics graphics, int x, int y, int type, float amount, float full) {
        ResourceLocation fill = DataTabletScreen.TEXTURECRAFTING;
        int u = type == 0 || type == 2 ? 6 : 1;
        int v = type == 0 || type == 1 ? 202 : 226;
        if (amount > 0) {
            graphics.blit(fill, x, y + 22 - (int) Math.ceil(22f * (amount / full)), u, v + 22 - (int) Math.ceil(22f * (amount / full)), 3, (int) Math.ceil(22f * (amount / full)));
        }
    }

    public static Component getEssenceBarTooltipTiny(double mouseX, double mouseY, int x, int y, int type, float amount) {
        if (amount > 0) {
            if (mouseX <= x + 3 && mouseY <= y + 22 && mouseX >= x && mouseY >= y) {
                if (ClientPlayerData.getUnlockedEssences()[type]) {
                    return Component.translatable("gui.essence_bar." + (type == 0 ? "essence" : type == 1 ? "lunar_essence" : type == 2 ? "natural_essence" : "exotic_essence"), amount);
                } else {
                    return Component.translatable("gui.essence_bar.unknown", amount);
                }
            }
        }
        return null;
    }

    public static void drawEssenceBar(GuiGraphics graphics, int x, int y, int type, float amount, float full) {
        if (amount > 0) {
            ResourceLocation fill = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/essence_bars.png");
            int u = type * 11;
            int v = 0;
            graphics.blit(fill, x, y + 52 - (int) Math.ceil(52f * (amount / full)), u, v + 52 - (int) Math.ceil(52f * (amount / full)), 7, (int) Math.ceil(52f * (amount / full)));
        }
    }

    public static Component getEssenceBarTooltip(double mouseX, double mouseY, int x, int y, int type, float amount) {
        if (amount > 0) {
            if (mouseX >= x && mouseY >= y) {
                if (mouseX <= x + 7 && mouseY <= y + 52) {
                    if (ClientPlayerData.getUnlockedEssences()[type]) {
                        return Component.translatable("gui.essence_bar." + (type == 0 ? "essence" : type == 1 ? "lunar_essence" : type == 2 ? "natural_essence" : "exotic_essence"), amount);
                    } else {
                        return Component.translatable("gui.essence_bar.unknown", amount);
                    }
                }
            }
        }
        return null;
    }
}
