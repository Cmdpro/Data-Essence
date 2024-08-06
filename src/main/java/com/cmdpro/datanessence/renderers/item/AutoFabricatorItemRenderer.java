package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.blockitem.AutoFabricatorItem;
import com.cmdpro.datanessence.item.blockitem.InfuserItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AutoFabricatorItemRenderer extends GeoItemRenderer<AutoFabricatorItem> {
    public AutoFabricatorItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<AutoFabricatorItem> {
        @Override
        public ResourceLocation getModelResource(AutoFabricatorItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/auto-fabricator.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(AutoFabricatorItem object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/auto-fabricator.png");
        }

        @Override
        public ResourceLocation getAnimationResource(AutoFabricatorItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/auto-fabricator.animation.json");
        }
    }
}