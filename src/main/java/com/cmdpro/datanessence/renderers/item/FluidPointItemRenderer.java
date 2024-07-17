package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.EssencePointItem;
import com.cmdpro.datanessence.item.FluidPointItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FluidPointItemRenderer extends GeoItemRenderer<FluidPointItem> {
    public FluidPointItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<FluidPointItem> {
        @Override
        public ResourceLocation getModelResource(FluidPointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essence_point.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(FluidPointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/fluid_point.png");
        }

        @Override
        public ResourceLocation getAnimationResource(FluidPointItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essence_point.animation.json");
        }
    }
}