package com.cmdpro.datanessence.renderers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.FabricatorItem;
import com.cmdpro.datanessence.item.InfuserItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class InfuserItemRenderer extends GeoItemRenderer<InfuserItem> {
    public InfuserItemRenderer() {
        super(new Model());
    }
    public static class Model extends GeoModel<InfuserItem> {
        @Override
        public ResourceLocation getModelResource(InfuserItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "geo/infuser.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(InfuserItem object) {
            return new ResourceLocation(DataNEssence.MOD_ID, "textures/block/infuser.png");
        }

        @Override
        public ResourceLocation getAnimationResource(InfuserItem animatable) {
            return new ResourceLocation(DataNEssence.MOD_ID, "animations/infuser.animation.json");
        }
    }
}