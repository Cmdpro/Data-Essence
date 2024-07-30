package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

public class EssenceSword extends SwordItem {
    public static final Tier ESSENCE_SWORD = new SimpleTier(
            // The tag that determines what blocks this tool cannot break. See below for more information.
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            250,
            6.0F,
            2.0F,
            14,
            // Determines the repair ingredient of the tier. Use a supplier for lazy initializing.
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER)
    );
    public EssenceSword(Properties pProperties) {
        super(ESSENCE_SWORD, pProperties.component(DataComponentRegistry.MAX_ESSENCE, 10000f));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (DataNEssenceUtil.ItemUtil.EssenceChargeableItemUtil.getEssence(stack) >= 10) {
            DataNEssenceUtil.ItemUtil.EssenceChargeableItemUtil.drainEssence(stack, 10);
            EssenceSlashProjectile slash = new EssenceSlashProjectile(EntityRegistry.ESSENCE_SLASH_PROJECTILE.get(), pPlayer, pLevel);
            slash.setPos(slash.position().offsetRandom(pPlayer.getRandom(), 0.25f));
            pLevel.addFreshEntity(slash);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }
}
