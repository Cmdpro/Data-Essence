package com.cmdpro.datanessence.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;

import java.awt.*;

public class FluidWidget extends AbstractWidget {
    public IFluidHandler handler;
    public int slot;
    public boolean fillWithAmount;
    public FluidWidget(int pX, int pY, IFluidHandler handler, int slot, boolean fillWithAmount) {
        this(pX, pY, 16, 16, handler, slot, fillWithAmount);
    }
    public FluidWidget(int pX, int pY, int pWidth, int pHeight, IFluidHandler handler, int slot, boolean fillWithAmount) {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.handler = handler;
        this.slot = slot;
        this.fillWithAmount = fillWithAmount;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        FluidStack fluid = handler.getFluidInTank(slot);
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluid);
        if (stillTexture != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            int tintColor = fluidTypeExtensions.getTintColor(fluid);
            Color tint = new Color(tintColor);
            int height = getHeight();
            if (fillWithAmount) {
                height = (int) (height * ((float) handler.getFluidInTank(slot).getAmount() / (float) handler.getTankCapacity(slot)));
            }
            pGuiGraphics.blit(getX(), getY()+(getHeight()-height), 0, getWidth(), height, sprite, (float)tint.getRed()/255f, (float)tint.getGreen()/255f, (float)tint.getBlue()/255f, (float)tint.getAlpha()/255f);
        }
        if (!fluid.isEmpty()) {
            setTooltip(Tooltip.create(Component.translatable("gui.widget.fluid", fluid.getAmount(), handler.getTankCapacity(0), fluid.getHoverName())));
        } else {
            setTooltip(Tooltip.create(Component.empty()));
        }
        if (isHovered) {
            pGuiGraphics.fill(getX(), getY(), getX()+getWidth(), getY()+getHeight(), 0x7fFFFFFF);
        }
    }
    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }
    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}