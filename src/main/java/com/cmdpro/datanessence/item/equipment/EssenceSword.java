package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class EssenceSword extends SwordItem {
    public static final Tier ESSENCE_SWORD = new SimpleTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            750,
            6.0F,
            2.0F,
            14,
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER)
    );
    public static ResourceLocation FUEL_ESSENCE_TYPE = DataNEssence.locate("essence");
    public EssenceSword(Properties pProperties) {
        super(ESSENCE_SWORD, pProperties.attributes(SwordItem.createAttributes(ESSENCE_SWORD, 3, -2.4F)).component(DataComponentRegistry.ESSENCE_STORAGE, new ItemEssenceContainer(List.of(FUEL_ESSENCE_TYPE), 2500)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            if (ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) >= 10) {
                ItemEssenceContainer.removeEssence(stack, FUEL_ESSENCE_TYPE, 10);
                EssenceSlashProjectile slash = new EssenceSlashProjectile(EntityRegistry.ESSENCE_SLASH_PROJECTILE.get(), pPlayer, pLevel);
                slash.setPos(slash.position().offsetRandom(pPlayer.getRandom(), 0.25f));
                pLevel.addFreshEntity(slash);
                pPlayer.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }
}
