package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseEssencePointRenderer;
import com.cmdpro.datanessence.block.transmission.NaturalEssencePointBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class NaturalEssencePointRenderer extends BaseEssencePointRenderer<NaturalEssencePointBlockEntity> {
    public NaturalEssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(DataNEssence.locate("textures/block/natural_essence_point.png")));
    }
}