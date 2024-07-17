package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.entity.ExoticEssencePointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class ExoticEssencePointRenderer extends BaseEssencePointRenderer<ExoticEssencePointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public ExoticEssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(new ResourceLocation(DataNEssence.MOD_ID, "textures/block/exotic_essence_point.png")));
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}