package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.FluidPointItem;
import com.cmdpro.datanessence.item.ItemPointItem;
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
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essencepoint.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ItemPointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/itempoint.png");
        }

        @Override
        public ResourceLocation getAnimationResource(ItemPointItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essencepoint.animation.json");
        }
    }
}