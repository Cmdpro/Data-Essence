package com.cmdpro.datanessence.mixin.client;

import com.cmdpro.datanessence.client.ClientEvents;
import com.cmdpro.datanessence.client.shaders.MachineOutputShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "resize", at = @At(value = "TAIL"), remap = false)
    private void resize(int pWidth, int pHeight, CallbackInfo ci) {
        if (ClientEvents.tempRenderTarget != null) {
            ClientEvents.tempRenderTarget.resize(pWidth, pHeight, Minecraft.ON_OSX);
        }
        MachineOutputShader.getMachineOutputTarget().resize(pWidth, pHeight, Minecraft.ON_OSX);
    }
}
