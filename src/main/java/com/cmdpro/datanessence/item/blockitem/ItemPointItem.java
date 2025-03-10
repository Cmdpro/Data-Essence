package com.cmdpro.datanessence.item.blockitem;

import com.cmdpro.datanessence.client.renderers.item.ItemPointItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemPointItem extends BlockItem {
    public ItemPointItem(Block block, Properties settings) {
        super(block, settings);
    }

    public static IClientItemExtensions extensions() {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ItemPointItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return renderer;
            }
        };
    }
}