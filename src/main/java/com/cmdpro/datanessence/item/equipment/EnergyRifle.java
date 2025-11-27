package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.entity.RifleLaser;
import com.cmdpro.datanessence.registry.EntityRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EnergyRifle extends Item {

    private static final String CHARGE_TAG = "ChargeTime";
    private static final int MAX_CHARGE_TICKS = 40; // 2 seconds

    public EnergyRifle(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isCharging(stack)) {
            // Start Charging
            setCharge(stack, MAX_CHARGE_TICKS);
            player.getCooldowns().addCooldown(this, 60);

            if (level.isClientSide) {
                player.playNotifySound(
                        SoundRegistry.ENERGY_RIFLE_CHARGE.value(),
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );
            }
        }

        return InteractionResultHolder.consume(stack);
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        // We check if the item has a charge tag
        if (isCharging(stack)) {

            int currentCharge = getCharge(stack);

            if (currentCharge > 0) {

                setCharge(stack, currentCharge - 1);
            } else {

                boolean isHolding = isSelected || player.getOffhandItem() == stack;

                if (isHolding) {
                    if (!level.isClientSide) {
                        fireLaser(level, player);
                    }
                    if (level.isClientSide) {
                        player.playNotifySound(
                                SoundRegistry.ENERGY_RIFLE_FIRE.value(),
                                SoundSource.PLAYERS,
                                1.0F,
                                1.0F
                        );
                    }
                } else {
                    if (level.isClientSide) {
                        player.playNotifySound(
                                SoundRegistry.ENERGY_RIFLE_INTERRUPT.value(),
                                SoundSource.PLAYERS,
                                1.0F,
                                1.0F
                        );
                    }
                }

                removeCharge(stack);
            }
        }
    }

    private void fireLaser(Level level, Player player) {
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 dir = player.getLookAngle();

        RifleLaser laser = new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level,3,21, 35);
        laser.setDeltaMovement(dir);
        laser.setPos(pos.add(dir.scale(0.1)));
        level.addFreshEntity(laser);
    }

    private boolean isCharging(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.contains(CHARGE_TAG);
    }

    private int getCharge(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getInt(CHARGE_TAG);
    }

    private void setCharge(ItemStack stack, int ticks) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(CHARGE_TAG, ticks));
    }

    private void removeCharge(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(CHARGE_TAG));
    }
}