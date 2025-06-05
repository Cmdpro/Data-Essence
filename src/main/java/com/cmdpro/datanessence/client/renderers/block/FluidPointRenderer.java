package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseCapabilityPointRenderer;
import com.cmdpro.datanessence.block.transmission.FluidPointBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class FluidPointRenderer extends BaseCapabilityPointRenderer<FluidPointBlockEntity> {
    public FluidPointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(DataNEssence.locate("textures/block/fluid_point.png")));
    }
}