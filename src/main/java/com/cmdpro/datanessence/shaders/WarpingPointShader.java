package com.cmdpro.datanessence.shaders;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WarpingPointShader extends ShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return new ResourceLocation(DataNEssence.MOD_ID, "shaders/post/warping_point.json");
    }
    public Vec3 point;
    @Override
    public void setUniforms(PostPass instance) {
        instance.getEffect().safeGetUniform("Point").set(point.toVector3f());
    }

    @Override
    public void process() {
        List<Vec3> blackHoles = new ArrayList<Vec3>();
        blackHoles.add(new Vec3(-14, 75, 27));
        for (Vec3 i : blackHoles) {
            point = i;
            super.process();
        }
    }
}
