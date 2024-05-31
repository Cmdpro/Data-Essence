package com.cmdpro.datanessence.mixins.client;

import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {
    @Shadow
    private Map<BlockState, BakedModel> modelByStateCache;
    @Inject(method = "getBlockModel", at = @At(value = "HEAD"), cancellable = true)
    public void getBlockModel(BlockState pState, CallbackInfoReturnable<BakedModel> cir) {
        BlockState state = ClientDataNEssenceUtil.getHiddenBlock(pState.getBlock());
        if (state != null) {
            cir.setReturnValue(modelByStateCache.get(state));
        }
    }
}
