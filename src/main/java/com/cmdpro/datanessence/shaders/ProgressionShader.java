package com.cmdpro.datanessence.shaders;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class ProgressionShader extends ShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return new ResourceLocation(DataNEssence.MOD_ID, "shaders/post/progression.json");
    }

    @Override
    public void afterProcess() {
        if (time >= 5f) {
            setActive(false);
        }
    }
}
