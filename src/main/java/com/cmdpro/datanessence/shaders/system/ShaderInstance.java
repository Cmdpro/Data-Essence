package com.cmdpro.datanessence.shaders.system;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

public abstract class ShaderInstance {
    public abstract ResourceLocation getShaderLocation();
    private PostChain postChain;
    public float time;
    public List<PostPass> passes;
    private boolean active;
    public PoseStack viewModelStack;
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        if (!active) {
            time = 0;
        }
        this.active = active;
    }
    public void resize(int pWidth, int pHeight) {
        if (postChain != null) {
            postChain.resize(pWidth, pHeight);
        }
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
            for (PostPass i : passes) {
                i.getEffect().safeGetUniform("time").set(time);
                i.getEffect().safeGetUniform("CameraPosition").set(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f());
                i.getEffect().safeGetUniform("ModelViewMat").set(RenderSystem.getModelViewMatrix());
                setUniforms(i);
            }
            beforeProcess();
            if (active) {
                time += Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true) / 20f;
                processPostChain();
                afterProcess();
            }
        }
    }
    public void processPostChain() {
        postChain.process(Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());
        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, Minecraft.getInstance().getMainRenderTarget().frameBufferId);
    }
    public void tick() {

    }
    public void setUniforms(PostPass instance) {}
    public void beforeProcess() {}
    public void afterProcess() {}
}
