package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.api.util.item.EssenceChargeableItemUtil;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class IlluminationRod extends Item {
    public float useCost = 25f; // how much one light block costs to place. 200 uses with a capacity of 5000

    public IlluminationRod(Properties properties) {
        super(properties.component(DataComponentRegistry.MAX_ESSENCE, 5000f));
    }

    // Returns true if a light block could be placed.
    public boolean placeLight(Level world, BlockPos target, ItemStack stack) {
        BlockState targetState = world.getBlockState(target);
        if (targetState.isAir()) {
            if (EssenceChargeableItemUtil.getEssence(stack) >= useCost) {
                world.setBlock(target, BlockRegistry.FLARE_LIGHT.get().defaultBlockState(), 3);
                EssenceChargeableItemUtil.drainEssence(stack, useCost);
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();

        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos target = pos.offset(direction.getNormal());

        if (placeLight(world, target, context.getItemInHand())) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    // what use does this have? don't know, but it's funny
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (EssenceChargeableItemUtil.getEssence(stack) >= useCost) {
            interactionTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
            EssenceChargeableItemUtil.drainEssence(stack, useCost);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
