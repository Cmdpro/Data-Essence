package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.datanessence.client.renderers.item.BasicAnimatedBlockItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class AnimatedBlockItemUtil {
    public static IClientItemExtensions createExtensions(RendererCreationFunction function) {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = function.get(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return renderer;
            }
        };
    }
    public static IClientItemExtensions createBasicExtensions(ResourceLocation texture, ResourceLocation model) {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new BasicAnimatedBlockItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), texture, model);
                }
                return renderer;
            }
        };
    }
    public interface RendererCreationFunction {
        BlockEntityWithoutLevelRenderer get(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet);
    }
}