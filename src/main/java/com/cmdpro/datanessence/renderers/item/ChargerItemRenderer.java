package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.blockitem.ChargerItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ChargerItemRenderer extends GeoItemRenderer<ChargerItem> {
    public ChargerItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<ChargerItem> {
        @Override
        public ResourceLocation getModelResource(ChargerItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/charger.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ChargerItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/charger.png");
        }

        @Override
        public ResourceLocation getAnimationResource(ChargerItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/charger.animation.json");
        }
    }
}