package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.transmission.EssencePointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class EssencePointRenderer extends BaseEssencePointRenderer<EssencePointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public EssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/essence_point.png")));
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}