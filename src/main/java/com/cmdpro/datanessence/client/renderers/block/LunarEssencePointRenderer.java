package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseEssencePointRenderer;
import com.cmdpro.datanessence.block.transmission.LunarEssencePointBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class LunarEssencePointRenderer extends BaseEssencePointRenderer<LunarEssencePointBlockEntity> {
    public LunarEssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(DataNEssence.locate("textures/block/lunar_essence_point.png")));
    }
}