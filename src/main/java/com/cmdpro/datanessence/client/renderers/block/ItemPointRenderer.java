package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.renderers.BaseCapabilityPointRenderer;
import com.cmdpro.datanessence.block.transmission.ItemPointBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;


public class ItemPointRenderer extends BaseCapabilityPointRenderer<ItemPointBlockEntity> {
    public ItemPointRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super( new Model<>(DataNEssence.locate("textures/block/item_point.png")), new RelayModel<>(DataNEssence.locate("textures/block/item_point.png")) );
    }
}