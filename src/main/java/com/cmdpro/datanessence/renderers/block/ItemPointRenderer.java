package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.transmission.ItemPointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class ItemPointRenderer extends BaseCapabilityPointRenderer<ItemPointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public ItemPointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/item_point.png")));
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}