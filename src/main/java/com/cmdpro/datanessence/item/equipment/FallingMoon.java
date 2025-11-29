package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.entity.RifleLaser;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FallingMoon extends Item {

    public static ResourceLocation LUNAR = DataNEssence.locate("lunar_essence");
    private final int TICKS_PER_CHARGE = 40; // 2 seconds per charge level
    private final int COOLDOWN = 20;

    final int COST_LOW = 100;
    final int COST_MED = 500;
    final int COST_HIGH = 1000;

    public FallingMoon(Properties properties) {
        super(properties
                .component(DataComponentRegistry.ESSENCE_STORAGE, new ItemEssenceContainer(List.of(LUNAR), 5000))
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.datanessence.falling_moon.tooltip_1" ).withStyle(Style.EMPTY.withColor( 0xffe8cc6d ).withItalic(true)));
        tooltipComponents.add(Component.translatable("item.datanessence.falling_moon.tooltip_2",
                Component.literal(String.valueOf(COST_LOW)).withColor(0xffe8cc6d),
                Component.literal(String.valueOf(COST_MED)).withColor(0xffe8cc6d),
                Component.literal(String.valueOf(COST_HIGH)).withColor(0xffe8cc6d)
        ).withStyle(Style.EMPTY.withColor(EssenceTypeRegistry.LUNAR_ESSENCE.get().getColor())));
    }


    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ItemEssenceContainer.getEssence(stack, LUNAR) < 5000;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * ItemEssenceContainer.getEssence(stack, LUNAR) / 5000F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xf5fbc0;
    }



    @Override
    public boolean useOnRelease(ItemStack stack) {
        return super.useOnRelease(stack);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (entity instanceof Player player) {
            int usedDuration = this.getUseDuration(stack, entity) - count;
            int chargeLevel = calculateCharge(usedDuration);


            int essenceCost = switch (chargeLevel) {
                case 1 -> COST_LOW;
                case 2 -> COST_MED;
                case 3, 4 -> COST_HIGH;
                default -> 0;
            };

            if (chargeLevel > 0 && ItemEssenceContainer.getEssence(stack, LUNAR) >= essenceCost) {
                ItemEssenceContainer.removeEssence(stack, LUNAR, essenceCost);

                fireLaser(entity.level(), player, chargeLevel);
                player.getCooldowns().addCooldown(this, COOLDOWN);
            } else {
                player.playNotifySound(
                        SoundRegistry.ENERGY_RIFLE_INTERRUPT.value(),
                        SoundSource.PLAYERS,
                        3.0F,
                        1.0F
                );
            }
        }
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide && livingEntity instanceof Player player) {
            int usedDuration = this.getUseDuration(stack, livingEntity) - remainingUseDuration;

            if (usedDuration > 0 && usedDuration % TICKS_PER_CHARGE == 0) {
                int currentCharge = calculateCharge(usedDuration);

                // Calculate projected cost to check if we should play the "ready" sound
                int projectedCost = switch (currentCharge) {
                    case 1 -> COST_LOW;
                    case 2 -> COST_MED;
                    case 3, 4 -> COST_HIGH;
                    default -> 0;
                };

                if (currentCharge <= 4 && ItemEssenceContainer.getEssence(stack, LUNAR) >= projectedCost) {
                    player.playNotifySound(
                            switch (currentCharge) {
                                case 1 -> SoundRegistry.FALLING_MOON_CHARGE_1.value();
                                case 2 -> SoundRegistry.FALLING_MOON_CHARGE_2.value();
                                case 3, 4 -> SoundRegistry.FALLING_MOON_CHARGE_3.value();
                                default -> SoundRegistry.FALLING_MOON_CHARGE_1.value();
                            },
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F
                    );
                }
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (ItemEssenceContainer.getEssence(stack, LUNAR) < 100) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 160;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    private int calculateCharge(int usedDuration) {
        int charge = usedDuration / TICKS_PER_CHARGE;
        return Math.min(charge, 4);
    }

    private void fireLaser(Level level, Player player, int chargeLevel) {
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 dir = player.getLookAngle();

        player.playNotifySound(
                switch (chargeLevel) {
                    case 1 -> SoundRegistry.FALLING_MOON_FIRE_1.value();
                    case 2 -> SoundRegistry.FALLING_MOON_FIRE_2.value();
                    case 3 -> SoundRegistry.FALLING_MOON_FIRE_3.value();
                    case 4 -> SoundRegistry.FALLING_MOON_OVERCHARGE.value();
                    default -> SoundRegistry.ENERGY_RIFLE_INTERRUPT.value();
                },
                SoundSource.PLAYERS,
                3.0F,
                1.0F
        );

        RifleLaser laser = switch (chargeLevel) {
            case 0 -> null;
            case 1 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level, 2, 10, 20);
            case 2 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level, 3, 21, 35);
            case 3 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level, 5, 41, 50);
            case 4 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level, 5, 41, 50, true);
            default -> throw new IllegalStateException("Unexpected charge value: " + chargeLevel);
        };

        if (laser != null) {
            laser.setDeltaMovement(dir);
            if (chargeLevel == 4) laser.setPos(pos.add(dir.scale(0.1)));
            else laser.setPos(pos.add(dir.scale(1)));
            level.addFreshEntity(laser);
        }
    }
}