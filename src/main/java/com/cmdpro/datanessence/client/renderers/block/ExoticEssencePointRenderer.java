package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseEssencePointRenderer;
import com.cmdpro.datanessence.block.transmission.ExoticEssencePointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;


public class ExoticEssencePointRenderer extends BaseEssencePointRenderer<ExoticEssencePointBlockEntity> {
    public ExoticEssencePointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super( new Model<>(DataNEssence.locate("textures/block/exotic_essence_point.png")), new RelayModel(DataNEssence.locate("textures/block/exotic_essence_point.png")) );
    }
}