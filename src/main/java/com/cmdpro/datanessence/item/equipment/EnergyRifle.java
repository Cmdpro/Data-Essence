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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EnergyRifle extends Item {

    private static final String CHARGE_TAG = "ChargeTime";
    private static int chargeLevel = 0;
    private static final int MAX_CHARGE_TICKS = 40; // 2 seconds
    int tickPerCharge = 0; // 2 seconds
    Level useLevel;
    Player usePlayer;
    public EnergyRifle(Properties properties) {
        super(properties);
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        System.out.println("released! " + chargeLevel);
        fireLaser(useLevel,usePlayer,chargeLevel);
        chargeLevel = 0;
        tickPerCharge = 0;
        return super.useOnRelease(stack);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        System.out.println("stopped!: " + chargeLevel);
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        System.out.println("ticked: " + tickPerCharge + " chargelvl: " + chargeLevel);
        tickPerCharge++;
        useLevel = level;
        usePlayer = (Player) livingEntity;
        if(tickPerCharge >= 40 && chargeLevel <= 3) {
            chargeLevel++;
                ((Player) livingEntity).playNotifySound(
                        switch (chargeLevel) {
                            case 1: yield SoundRegistry.ENERGY_RIFLE_CHARGE_1.value();
                            case 2: yield SoundRegistry.ENERGY_RIFLE_CHARGE_2.value();
                            case 3, 4: yield SoundRegistry.ENERGY_RIFLE_CHARGE_3.value();
                            default: yield SoundRegistry.ENERGY_RIFLE_CHARGE_1.value();
                        },
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );

            tickPerCharge = 0;
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

            player.startUsingItem(hand);

        System.out.println("USED: " + chargeLevel);
        ItemStack stack = player.getItemInHand(hand);

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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

    }

    private void fireLaser(Level level, Player player, int chargeLevel) {
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 dir = player.getLookAngle();

        player.playNotifySound(
                switch (chargeLevel) {
                    case 1: yield SoundRegistry.ENERGY_RIFLE_FIRE_1.value();
                    case 2: yield SoundRegistry.ENERGY_RIFLE_FIRE_2.value();
                    case 3: yield SoundRegistry.ENERGY_RIFLE_FIRE_3.value();
                    case 4: yield SoundRegistry.ENERGY_RIFLE_OVERCHARGE.value();
                    default: yield SoundRegistry.ENERGY_RIFLE_OVERCHARGE.value();
                },
                SoundSource.PLAYERS,
                3.0F,
                1.0F
        );

        RifleLaser laser = switch (chargeLevel){
            case 0 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level,0,0, 0);
            case 1 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level,2,10, 20);
            case 2 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level,3,21, 35);
            case 3 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level,5,41, 50);
            case 4 -> new RifleLaser(EntityRegistry.RIFLE_LASER.get(), player, level,5,41, 50, true);
            default -> throw new IllegalStateException("the world couldn't bear your strength, value: " + chargeLevel);
        };


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