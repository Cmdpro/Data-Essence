package com.cmdpro.datanessence.integration.emi.widgets;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceType;
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
    public EssenceType type;
    public int x;
    public int y;
    public float cost;
    public EssenceBarWidget(int x, int y, EssenceType type, float cost) {
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
        int u = type.getTinyBarSprite().x;
        int v = type.getTinyBarSprite().y;
        if (cost > 0) {
            draw.blit(type.getTinyBarSprite().texture, x, y+22 - (int) Math.ceil(22f * (cost / 1000f)), u, v + 22 - (int) Math.ceil(22f * (cost / 1000f)), 3, (int) Math.ceil(22f * (cost / 1000f)));
        }
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (cost > 0) {
            if (getBounds().contains(mouseX, mouseY)) {
                if (ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(type), false)) {
                    return List.of(ClientTooltipComponent.create(Component.translatable(type.getTooltipKey(), cost).getVisualOrderText()));
                } else {
                    return List.of(ClientTooltipComponent.create(Component.translatable("gui.essence_bar.unknown", cost).getVisualOrderText()));
                }
            }
        }
        return super.getTooltip(mouseX, mouseY);
    }
}
