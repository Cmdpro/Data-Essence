package com.cmdpro.datanessence.item.blockitem;

import com.cmdpro.datanessence.renderers.item.EssencePointItemRenderer;
import com.cmdpro.datanessence.renderers.item.FabricatorItemRenderer;
import com.cmdpro.datanessence.renderers.item.NaturalEssencePointItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class NaturalEssencePointItem extends BlockItem {

    public NaturalEssencePointItem(Block block, Properties settings) {
        super(block, settings);
    }

    public static IClientItemExtensions extensions() {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new NaturalEssencePointItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return renderer;
            }
        };
    }
}