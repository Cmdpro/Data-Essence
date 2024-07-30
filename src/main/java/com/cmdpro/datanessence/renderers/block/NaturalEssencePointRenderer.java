package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.transmission.NaturalEssencePointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class NaturalEssencePointRenderer extends BaseEssencePointRenderer<NaturalEssencePointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public NaturalEssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(new ResourceLocation(DataNEssence.MOD_ID, "textures/block/natural_essence_point.png")));
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}