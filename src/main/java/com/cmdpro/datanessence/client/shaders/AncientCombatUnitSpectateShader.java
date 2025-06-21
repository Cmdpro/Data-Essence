package com.cmdpro.datanessence.client.shaders;

import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.cmdpro.databank.mixin.client.RenderBuffersMixin;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.datanessence.DataNEssence;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.SequencedMap;

public class AncientCombatUnitSpectateShader extends PostShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return DataNEssence.locate("shaders/post/hologram.json");
    }
    @Override
    public void setUniforms(PostPass instance) {
        super.setUniforms(instance);
        instance.getEffect().setSampler("HologramSampler", Minecraft.getInstance().getMainRenderTarget()::getColorTextureId);
    }
}
