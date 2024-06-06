package com.cmdpro.datanessence.postprocessors;

import com.cmdpro.datanessence.DataNEssence;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

public class ProgressionProcessor extends PostProcessor {
    @Override
    public ResourceLocation getPostChainLocation() {
        return new ResourceLocation(DataNEssence.MOD_ID, "progression");
    }

    @Override
    public void beforeProcess(PoseStack poseStack) {
        for (EffectInstance i : effects) {
            i.safeGetUniform("time").set((float)time);
        }
    }
    @Override
    public void afterProcess() {
        if (time >= 5f) {
            setActive(false);
        }
    }
}
