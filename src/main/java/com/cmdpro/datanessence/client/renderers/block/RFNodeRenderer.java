package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseCapabilityPointRenderer;
import com.cmdpro.datanessence.block.transmission.RFNodeBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;


public class RFNodeRenderer extends BaseCapabilityPointRenderer<RFNodeBlockEntity> {
    public RFNodeRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model<>(DataNEssence.locate("textures/block/rf_node.png")));
    }
}