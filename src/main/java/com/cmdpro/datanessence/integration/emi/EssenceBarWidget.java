package com.cmdpro.datanessence.integration.emi;

import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EssenceBarWidget extends Widget {
    public int type;
    public int x;
    public int y;
    public float cost;
    public EssenceBarWidget(int x, int y, int type, float cost) {
        this.type = type;
        this.cost = cost;
        this.x = x;
        this.y = y;
    }
    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, 3, 22);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        ResourceLocation fill = DataTabletScreen.TEXTURECRAFTING;
        int u = type == 0 || type == 2 ? 6 : 1;
        int v = type == 0 || type == 1 ? 202 : 226;
        if (cost > 0) {
            draw.blit(fill, x, y+22 - (int) Math.ceil(22f * (cost / 1000f)), u, v + 22 - (int) Math.ceil(22f * (cost / 1000f)), 3, (int) Math.ceil(22f * (cost / 1000f)));
        }
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (cost > 0) {
            if (getBounds().contains(mouseX, mouseY)) {
                if (ClientPlayerData.getUnlockedEssences()[type]) {
                    return List.of(ClientTooltipComponent.create(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator." + (type == 0 ? "essence" : type == 1 ? "lunar_essence" : type == 2 ? "natural_essence" : "exotic_essence"), cost).getVisualOrderText()));
                } else {
                    return List.of(ClientTooltipComponent.create(Component.translatable("item.datanessence.data_tablet.page_type.crafting.fabricator.unknown", cost).getVisualOrderText()));
                }
            }
        }
        return super.getTooltip(mouseX, mouseY);
    }
}
