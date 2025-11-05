package com.cmdpro.datanessence.data.datatablet.pages;

import com.cmdpro.databank.multiblock.Multiblock;
import com.cmdpro.databank.multiblock.MultiblockManager;
import com.cmdpro.databank.multiblock.MultiblockRenderer;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import com.cmdpro.datanessence.api.datatablet.Page;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.MultiblockPageSerializer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Rotation;

public class MultiblockPage extends Page {
    public ResourceLocation multiblock;
    float renderRotationX;
    float renderRotationY;
    int mode; // 0 = multiblock renderer; 1 = material list

    public MultiblockPage(ResourceLocation multiblock) {
        this.multiblock = multiblock;
        renderRotationX = -30f;
        renderRotationY = -45f;
        mode = 0;
    }

    @Override
    public void render(DataTabletScreen screen, GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, int xOffset, int yOffset) {
        Multiblock multiblock = MultiblockManager.multiblocks.get(this.multiblock);

        if (mode == 0)
            renderMultiblock(graphics, multiblock, xOffset, yOffset);
        if (mode == 1)
            renderMaterialsList(graphics, multiblock, xOffset, yOffset);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        if (mouseX >= xOffset+120 && mouseX <= xOffset+120+16 && mouseY >= yOffset + 142 && mouseY <= yOffset + 142 + 16) {
            graphics.blit(DataTabletScreen.TEXTURE_MISC, xOffset+120, yOffset+142, 0, 54, 16, 16);
        } else {
            graphics.blit(DataTabletScreen.TEXTURE_MISC, xOffset+120, yOffset+142, 0, 38, 16, 16);
        }
        graphics.pose().popPose();
    }

    public void renderMultiblock(GuiGraphics graphics, Multiblock multiblock, int xOffset, int yOffset) {
        graphics.pose().pushPose();
        int sizeX = multiblock.multiblockLayers[0][0].length();
        int sizeY = multiblock.multiblockLayers.length;
        int sizeZ = multiblock.multiblockLayers[0].length;
        float maxX = 90;
        float maxY = 90;
        float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
        float scaleX = maxX / diag;
        float scaleY = maxY / sizeY;
        float scale = -Math.min(scaleX, scaleY);
        graphics.pose().translate(xOffset+(DataTabletScreen.imageWidth/2f), yOffset+(DataTabletScreen.imageHeight/2f), 100);
        graphics.pose().scale(scale, scale, scale);
        graphics.pose().translate(-(float) sizeX / 2, -(float) sizeY / 2, 0);
        float offX = (float) -sizeX / 2f;
        float offZ = (float) -sizeZ / 2f;
        graphics.pose().mulPose(Axis.XP.rotationDegrees(renderRotationX));
        graphics.pose().translate(-offX, 0, -offZ);
        graphics.pose().mulPose(Axis.YP.rotationDegrees(renderRotationY));
        graphics.pose().translate(offX, 0, offZ);
        graphics.pose().translate(-multiblock.center.getX(), -multiblock.center.getY(), -multiblock.center.getZ());
        MultiblockRenderer.renderMultiblock(multiblock, null, graphics.pose(), Minecraft.getInstance().getTimer(), Rotation.NONE, Minecraft.getInstance().renderBuffers().bufferSource());
        graphics.pose().popPose();
    }

    public void renderMaterialsList(GuiGraphics graphics, Multiblock multiblock, int xOffset, int yOffset) {
        var list = multiblock.getStates();
        // TODO ^ there should be a method made to get the quantity of different blockstates in a structure, this is unsuitable
    }

    @Override
    public boolean onClick(DataTabletScreen screen, double pMouseX, double pMouseY, int pButton, int xOffset, int yOffset) {
        if (pMouseX >= xOffset+120 && pMouseX <= xOffset+120+16) {
            if (pMouseY >= yOffset+142 && pMouseY <= yOffset+142+16) {
                MultiblockRenderer.multiblockPos = null;
                MultiblockRenderer.multiblockRotation = null;
                MultiblockRenderer.multiblock = MultiblockRenderer.multiblock == null ? MultiblockManager.multiblocks.get(multiblock) : null;
                Client.playClick();
                Minecraft.getInstance().popGuiLayer();
                return true;
            }
        }
        return super.onClick(screen, pMouseX, pMouseY, pButton, xOffset, yOffset);
    }

    @Override
    public boolean onDrag(DataTabletScreen screen, double mouseX, double mouseY, int button, double xDrag, double yDrag) {

        renderRotationX -= (float) (0.5*yDrag);
        renderRotationY += (float) (0.5*xDrag);

        return true;
    }

    @Override
    public PageSerializer getSerializer() {
        return MultiblockPageSerializer.INSTANCE;
    }

    public static class Client {

        public static void playClick() {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.UI_CLICK, 1.0F));
        }
    }
}
