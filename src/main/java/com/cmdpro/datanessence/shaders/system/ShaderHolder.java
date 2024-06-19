package com.cmdpro.datanessence.shaders.system;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class ShaderHolder {
    public ShaderInstance instance;
    public RenderStateShard.ShaderStateShard shard = new RenderStateShard.ShaderStateShard(() -> instance);
    public ResourceLocation shaderLocation;
    public VertexFormat shaderFormat;
    public ShaderHolder(ResourceLocation shaderLocation, VertexFormat shaderFormat) {
        this.shaderLocation = shaderLocation;
        this.shaderFormat = shaderFormat;
    }
    public ShaderInstance createInstance(ResourceProvider provider) throws IOException {
        ShaderInstance instance = new ShaderInstance(provider, shaderLocation, shaderFormat);
        this.instance = instance;
        return instance;
    }
}
