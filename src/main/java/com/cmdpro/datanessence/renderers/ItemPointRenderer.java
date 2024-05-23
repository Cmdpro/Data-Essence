package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.entity.FluidPointBlockEntity;
import com.cmdpro.datanessence.block.entity.ItemPointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class ItemPointRenderer extends BaseCapabilityPointRenderer<ItemPointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public ItemPointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(new ResourceLocation(DataNEssence.MOD_ID, "textures/block/itempoint.png")));
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}