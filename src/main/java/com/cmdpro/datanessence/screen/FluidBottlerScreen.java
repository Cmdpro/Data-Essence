package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import javax.tools.Tool;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FluidBottlerScreen extends AbstractContainerScreen<FluidBottlerMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/fluid_bottler.png");
    public FluidBottlerScreen(FluidBottlerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        addRenderableWidget(new FluidWidget(x+62, y+45, menu.blockEntity.getFluidHandler(), 0));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if (menu.blockEntity.workTime >= 0) {
            pGuiGraphics.blit(TEXTURE, x + 82, y + 33, 188, 0, (int) Math.ceil(22f * ((float)menu.blockEntity.workTime / 20f)), 17);
        }
        ClientDataNEssenceUtil.EssenceBarRendering.drawEssenceBar(pGuiGraphics, x+8, y+17, 0, menu.blockEntity.getEssence(), menu.blockEntity.getMaxEssence());
        if (ClientPlayerData.getUnlockedEssences()[0]) {
            pGuiGraphics.blit(TEXTURE, x+7, y+6, 177, 0, 9, 9);
        }
    }
    public static class FluidWidget extends AbstractWidget {
        public IFluidHandler handler;
        public int slot;
        public FluidWidget(int pX, int pY, IFluidHandler handler, int slot) {
            super(pX, pY, 16, 16, Component.empty());
            this.handler = handler;
            this.slot = slot;
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
                pGuiGraphics.blit(getX(), getY(), 0, getWidth(), getHeight(), sprite, (float)tint.getRed()/255f, (float)tint.getGreen()/255f, (float)tint.getBlue()/255f, (float)tint.getAlpha()/255f);
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
        Component lunarEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+19, y+17, 1, menu.blockEntity.getLunarEssence());
        if (lunarEssence != null) {
            component.clear();
            component.add(lunarEssence.getVisualOrderText());
        }
        Component naturalEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+30, y+17, 2, menu.blockEntity.getNaturalEssence());
        if (naturalEssence != null) {
            component.clear();
            component.add(naturalEssence.getVisualOrderText());
        }
        Component exoticEssence = ClientDataNEssenceUtil.EssenceBarRendering.getEssenceBarTooltip(pMouseX, pMouseY, x+41, y+17, 3, menu.blockEntity.getExoticEssence());
        if (exoticEssence != null) {
            component.clear();
            component.add(exoticEssence.getVisualOrderText());
        }
        if (component != null) {
            pGuiGraphics.renderTooltip(font, component, pMouseX, pMouseY);
        }
    }
}
