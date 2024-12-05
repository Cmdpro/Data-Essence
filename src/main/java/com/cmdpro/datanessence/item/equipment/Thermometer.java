package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.databank.temperature.TemperatureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Thermometer extends Item {

    public Thermometer(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
        int T = TemperatureUtil.getAmbientTemperatureAt(pos);
        player.sendSystemMessage(Component.literal("Ambient temperature: " + T + "°C"));

        return InteractionResultHolder.success(stack);
    }
}
