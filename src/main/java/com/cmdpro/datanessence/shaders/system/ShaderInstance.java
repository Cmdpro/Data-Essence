package com.cmdpro.datanessence.shaders.system;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.List;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

public abstract class ShaderInstance {
    public abstract ResourceLocation getShaderLocation();
    private PostChain postChain;
    public float time;
    public List<PostPass> passes;
    private boolean active;
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        if (!active) {
            time = 0;
        }
        this.active = active;
    }
    public void process() {
        if (postChain == null) {
            try {
                postChain = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(), getShaderLocation());
                postChain.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                passes = postChain.passes;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if (postChain != null) {
            if (active) {
                time += Minecraft.getInstance().getDeltaFrameTime() / 20.0;
            }
            for (PostPass i : passes) {
                i.getEffect().safeGetUniform("time").set(time);
                setUniforms(i.getEffect());
            }
            beforeProcess();
            if (active) {
                postChain.process(Minecraft.getInstance().getFrameTime());
                GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, Minecraft.getInstance().getMainRenderTarget().frameBufferId);
                afterProcess();
            }
        }
    }
    public void setUniforms(EffectInstance instance) {}
    public void beforeProcess() {}
    public void afterProcess() {}
}
