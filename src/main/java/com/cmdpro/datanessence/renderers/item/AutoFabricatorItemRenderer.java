package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.blockitem.InfuserItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AutoFabricatorItemRenderer extends GeoItemRenderer<InfuserItem> {
    public AutoFabricatorItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<InfuserItem> {
        @Override
        public ResourceLocation getModelResource(InfuserItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/auto-fabricator.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(InfuserItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/auto-fabricator.png");
        }

        @Override
        public ResourceLocation getAnimationResource(InfuserItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/auto-fabricator.animation.json");
        }
    }
}