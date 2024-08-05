package com.cmdpro.datanessence.renderers.entity;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.AncientSentinel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class AncientSentinelRenderer extends GeoEntityRenderer<AncientSentinel> {
    public AncientSentinelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Model());
        this.shadowRadius = 0.5f;
    }
    @Override
    public ResourceLocation getTextureLocation(AncientSentinel instance) {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/entity/ancient_sentinel.png");
    }
    public static class Model extends GeoModel<AncientSentinel> {
        @Override
        public ResourceLocation getModelResource(AncientSentinel object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "geo/ancient_sentinel.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(AncientSentinel object) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/entity/ancient_sentinel.png");
        }

        @Override
        public ResourceLocation getAnimationResource(AncientSentinel animatable) {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "animations/ancient_sentinel.animation.json");
        }
    }
}
