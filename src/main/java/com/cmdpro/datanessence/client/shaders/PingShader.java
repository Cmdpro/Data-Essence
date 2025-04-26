package com.cmdpro.datanessence.client.shaders;

import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class PingShader extends PostShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return DataNEssence.locate("shaders/post/ping.json");
    }
    public Vector3f pingPosition;
    public PingShader(Vector3f pingPosition) {
        this.pingPosition = pingPosition;
    }

    @Override
    public void setUniforms(PostPass instance) {
        super.setUniforms(instance);
        instance.getEffect().safeGetUniform("PingPosition").set(pingPosition);
    }

    @Override
    public void beforeProcess() {
        super.beforeProcess();
        if (time >= 5) {
            queueRemoval();
        }
    }
}
