package com.cmdpro.datanessence.item.blockitem;

import com.cmdpro.datanessence.client.renderers.item.ExoticEssencePointItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ExoticEssencePointItem extends BlockItem {

    public ExoticEssencePointItem(Block block, Properties settings) {
        super(block, settings);
    }
    public static IClientItemExtensions extensions() {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ExoticEssencePointItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return renderer;
            }
        };
    }
}