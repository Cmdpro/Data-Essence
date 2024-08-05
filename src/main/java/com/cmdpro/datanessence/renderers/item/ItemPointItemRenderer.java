package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.blockitem.ItemPointItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ItemPointItemRenderer extends GeoItemRenderer<ItemPointItem> {
    public ItemPointItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<ItemPointItem> {
        @Override
        public ResourceLocation getModelResource(ItemPointItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/essence_point.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ItemPointItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/item_point.png");
        }

        @Override
        public ResourceLocation getAnimationResource(ItemPointItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/essence_point.animation.json");
        }
    }
}