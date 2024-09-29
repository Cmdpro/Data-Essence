package com.cmdpro.datanessence.shaders;

import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.resources.ResourceLocation;

public class ProgressionShader extends PostShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "shaders/post/progression.json");
    }

    @Override
    public void afterProcess() {
        if (time >= 5f) {
            setActive(false);
        }
    }
}
