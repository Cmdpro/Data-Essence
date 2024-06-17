package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.EssencePointItem;
import com.cmdpro.datanessence.item.LunarEssencePointItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LunarEssencePointItemRenderer extends GeoItemRenderer<LunarEssencePointItem> {
    public LunarEssencePointItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<LunarEssencePointItem> {
        @Override
        public ResourceLocation getModelResource(LunarEssencePointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essence_point.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(LunarEssencePointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/lunar_essence_point.png");
        }

        @Override
        public ResourceLocation getAnimationResource(LunarEssencePointItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essence_point.animation.json");
        }
    }
}