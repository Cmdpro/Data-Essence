package com.cmdpro.datanessence.api.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
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
    public interface RendererCreationFunction {
        BlockEntityWithoutLevelRenderer get(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet);
    }
}