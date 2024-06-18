package com.cmdpro.datanessence.mixins.client;

import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class GameRendererMixin {
    @Inject(method = "resize", at = @At(value = "HEAD"))
    private void renderLevel(int pWidth, int pHeight, CallbackInfo ci) {
        for (ShaderInstance i : ShaderManager.instances) {
            i.resize(pWidth, pHeight);
        }
    }
}
