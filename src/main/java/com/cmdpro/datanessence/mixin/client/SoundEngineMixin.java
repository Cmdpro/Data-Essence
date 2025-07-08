package com.cmdpro.datanessence.mixin.client;

import com.cmdpro.datanessence.client.ClientEvents;
import com.cmdpro.datanessence.client.FactorySong;
import com.cmdpro.datanessence.client.shaders.MachineOutputShader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SoundEngine.class)
public interface SoundEngineMixin {
    @Accessor("instanceToChannel")
    public Map<SoundInstance, ChannelAccess.ChannelHandle> getInstanceToChannel();
}
