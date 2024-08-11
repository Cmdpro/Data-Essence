package com.cmdpro.datanessence.shaders;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.shaders.system.ShaderHolder;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.earlydisplay.ElementShader;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataNEssenceCoreShaders implements ResourceManagerReloadListener {
    public static List<ShaderHolder> SHADERS = new ArrayList<>();

    public static final VertexFormat POSITION_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Normal", VertexFormatElement.NORMAL).build();
    public static ShaderHolder WARPINGPOINT = createShader("warping_point", POSITION_NORMAL);
    public static ShaderHolder HOLOGRAM_BLOCK = createShader("hologram_block", DefaultVertexFormat.BLOCK);
    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {

    }

    public static ShaderHolder createShader(String name, VertexFormat shaderFormat) {
        ShaderHolder shaderHolder = new ShaderHolder(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, name), shaderFormat);
        SHADERS.add(shaderHolder);
        return shaderHolder;
    }

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        ResourceProvider resourceProvider = event.getResourceProvider();

        for (ShaderHolder shader : SHADERS) {
            event.registerShader(shader.createInstance(resourceProvider), s -> {});
        }

    }
}
