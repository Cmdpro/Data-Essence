package com.cmdpro.datanessence.renderers;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.item.EnderTransporterItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EssencePointItemRenderer extends GeoItemRenderer<EnderTransporterItem> {
    public EssencePointItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<EnderTransporterItem> {
        @Override
        public ResourceLocation getModelResource(EnderTransporterItem object) {
            return new ResourceLocation(Runology.MOD_ID, "geo/endertransporter.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(EnderTransporterItem object) {
            return new ResourceLocation(Runology.MOD_ID, "textures/block/endertransporter.png");
        }

        @Override
        public ResourceLocation getAnimationResource(EnderTransporterItem animatable) {
            return new ResourceLocation(Runology.MOD_ID, "animations/endertransporter.animation.json");
        }
    }
}