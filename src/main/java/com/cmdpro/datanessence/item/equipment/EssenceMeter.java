package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.networking.packet.s2c.MachineEssenceValueSync;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;

import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class EssenceMeter extends Item {
    public static MachineEssenceValueSync.MachineEssenceValue currentMachineEssenceValue;
    public EssenceMeter(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockEntity tile = world.getBlockEntity(context.getClickedPos());
        Player player = context.getPlayer();

        if (tile instanceof EssenceBlockEntity machine) {
            if (!world.isClientSide) {
                var storage = machine.getStorage();
                var component = Component.translatable("item.datanessence.essence_meter.contains");

                for (EssenceType type : storage.getSupportedEssenceTypes()) {
                    component.append("\n  " + storage.getEssence(type) + " / " + storage.getMaxEssence() + " ");
                    component.append(type.getName());
                }

                player.sendSystemMessage(component);
                player.playNotifySound(SoundRegistry.UI_CLICK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
