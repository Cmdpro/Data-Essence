package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.transmission.FluidPointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class FluidPointRenderer extends BaseCapabilityPointRenderer<FluidPointBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public FluidPointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(new ResourceLocation(DataNEssence.MOD_ID, "textures/block/fluid_point.png")));
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}