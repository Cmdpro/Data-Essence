package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseEssencePointRenderer;
import com.cmdpro.datanessence.block.transmission.EssencePointBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class EssencePointRenderer extends BaseEssencePointRenderer<EssencePointBlockEntity> {
    public EssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(DataNEssence.locate("textures/block/essence_point.png")));
    }
}