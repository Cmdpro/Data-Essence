package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.FabricatorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FabricatorItemRenderer extends GeoItemRenderer<FabricatorItem> {
    public FabricatorItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<FabricatorItem> {
        @Override
        public ResourceLocation getModelResource(FabricatorItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/fabricator.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(FabricatorItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/fabricator.png");
        }

        @Override
        public ResourceLocation getAnimationResource(FabricatorItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/fabricator.animation.json");
        }
    }
}