package com.cmdpro.datanessence.client.shaders;

import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;

public class GenderEuphoriaShader extends PostShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "shaders/post/gender_euphoria.json");
    }
    public float colorOffset;
    public float fade;
    @Override
    public void setUniforms(PostPass instance) {
        super.setUniforms(instance);
        instance.getEffect().safeGetUniform("colorOffset").set(colorOffset);
        instance.getEffect().safeGetUniform("fade").set(fade);
    }

    @Override
    public void beforeProcess() {
        super.beforeProcess();
        if (isActive()) {
            colorOffset += Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true) / 20f;
            if (colorOffset >= 1) {
                colorOffset = 0;
            }
            fade += Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true) / 20f;
            if (fade >= 5) {
                fade = 0;
            }
        }
    }
}
