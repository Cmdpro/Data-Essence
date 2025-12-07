package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.AdjustableAttributes;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;

public class EssenceSword extends SwordItem implements AdjustableAttributes {
    public static final Tier ESSENCE_SWORD = new SimpleTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            0,
            1.8f,
            0f,
            14,
            () -> Ingredient.of(ItemRegistry.ESSENCE_SHARD.get())
    );
    public static ResourceLocation FUEL_ESSENCE_TYPE = DataNEssence.locate("essence");
    final int COST_SLASH = 100;
    final int COST_HIT = 10;

    public EssenceSword(Properties properties) {
        super(ESSENCE_SWORD, properties
                .rarity(Rarity.UNCOMMON)
                .attributes(SwordItem.createAttributes(ESSENCE_SWORD, -1, -2.2f))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
                .component(DataComponentRegistry.ESSENCE_STORAGE, new ItemEssenceContainer(List.of(FUEL_ESSENCE_TYPE), 2500)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean canHit = ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) >= COST_HIT;
        if (canHit)
            ItemEssenceContainer.removeEssence(stack, FUEL_ESSENCE_TYPE, COST_HIT);
        return canHit;
    }

    @Override
    public void adjustAttributes(ItemAttributeModifierEvent event) {
        ItemStack blade = event.getItemStack();
        float damage = ( ItemEssenceContainer.getEssence(blade, FUEL_ESSENCE_TYPE) >= COST_HIT ) ? 5f : -1f;

        event.replaceModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier( SwordItem.BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        //event.replaceModifier(Attributes.ATTACK_SPEED, new AttributeModifier( SwordItem.BASE_ATTACK_SPEED_ID, 1.8f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is( ItemRegistry.ESSENCE_SHARD.get() );
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility itemAbility) {
        return ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) >= COST_HIT && super.canPerformAction(stack, itemAbility);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return (ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) < COST_HIT);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            if (ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) >= COST_SLASH) {
            ItemEssenceContainer.removeEssence(stack, FUEL_ESSENCE_TYPE, COST_SLASH);
                EssenceSlashProjectile slash = new EssenceSlashProjectile(EntityRegistry.ESSENCE_SLASH_PROJECTILE.get(), pPlayer, pLevel);
                slash.setPos(slash.position().offsetRandom(pPlayer.getRandom(), 0.25f));
                pLevel.addFreshEntity(slash);
                pPlayer.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.datanessence.essence_sword.tooltip_1", Component.literal(String.valueOf(COST_HIT)).withColor(0xFFFF96B5) ).withStyle(Style.EMPTY.withColor(EssenceTypeRegistry.ESSENCE.get().getColor())));
        tooltipComponents.add(Component.translatable("item.datanessence.essence_sword.tooltip_2", Component.literal(String.valueOf(COST_SLASH)).withColor(0xFFFF96B5) ).withStyle(Style.EMPTY.withColor(EssenceTypeRegistry.ESSENCE.get().getColor())));
    }
}
