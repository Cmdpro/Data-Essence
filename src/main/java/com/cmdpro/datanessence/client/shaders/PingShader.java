package com.cmdpro.datanessence.client.shaders;

import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;

public class PingShader extends PostShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "shaders/post/ping.json");
    }

    @Override
    public void beforeProcess() {
        super.beforeProcess();
        if (time >= 5) {
            setActive(false);
        }
    }
}
