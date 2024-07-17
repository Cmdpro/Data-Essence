package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.entity.EssencePointBlockEntity;
import com.cmdpro.datanessence.item.EssencePointItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EssencePointItemRenderer extends GeoItemRenderer<EssencePointItem> {
    public EssencePointItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<EssencePointItem> {
        @Override
        public ResourceLocation getModelResource(EssencePointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/essence_point.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(EssencePointItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/essence_point.png");
        }

        @Override
        public ResourceLocation getAnimationResource(EssencePointItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/essence_point.animation.json");
        }
    }
}