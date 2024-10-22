package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseCapabilityPointRenderer;
import com.cmdpro.datanessence.block.transmission.ItemPointBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class ItemPointRenderer extends BaseCapabilityPointRenderer<ItemPointBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "item_point"), "main");
    public ItemPointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/item_point.png");
    }
}