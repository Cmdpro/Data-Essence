package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.ExoticEssencePointItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ExoticEssencePointItemRenderer extends GeoItemRenderer<ExoticEssencePointItem> {
    public ExoticEssencePointItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<ExoticEssencePointItem> {
        @Override
        public ResourceLocation getModelResource(ExoticEssencePointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essencepoint.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ExoticEssencePointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/exoticessencepoint.png");
        }

        @Override
        public ResourceLocation getAnimationResource(ExoticEssencePointItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essencepoint.animation.json");
        }
    }
}