package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseEssencePointRenderer;
import com.cmdpro.datanessence.block.transmission.NaturalEssencePointBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class NaturalEssencePointRenderer extends BaseEssencePointRenderer<NaturalEssencePointBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("natural_essence_point"), "main");
    public NaturalEssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/natural_essence_point.png");
    }
}