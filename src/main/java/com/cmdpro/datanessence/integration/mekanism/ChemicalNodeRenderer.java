package com.cmdpro.datanessence.integration.mekanism;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseCapabilityPointRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ChemicalNodeRenderer extends BaseCapabilityPointRenderer<ChemicalNodeBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "chemical_node"), "main");

    public ChemicalNodeRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/chemical_node.png");
    }
}
