package com.cmdpro.datanessence.mixins;

import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {

    @Inject(method = "getDrops", at = @At(value = "HEAD"), cancellable = true)
    public void getDrops(BlockState pState, LootParams.Builder pParams, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (pParams.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof Player player) {
            BlockState state = DataNEssenceUtil.getHiddenBlock((Block) (Object) this, player);
            if (state != null) {
                cir.setReturnValue(state.getBlock().getDrops(pState, pParams));
            }
        }
    }
}
