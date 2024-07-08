package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record PlayerChangeDriveDataC2SPacket(ResourceLocation entry, boolean offhand) implements Message {

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        if (unlocked.contains(entry)) {
            ItemStack stack = player.getItemInHand(offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (stack.is(ItemRegistry.DATA_DRIVE.get())) {
                stack.getOrCreateTag().putString("dataId", entry.toString());
            }
        }
    }

    public static PlayerChangeDriveDataC2SPacket read(FriendlyByteBuf buf) {
        ResourceLocation entry = buf.readResourceLocation();
        boolean offhand = buf.readBoolean();
        return new PlayerChangeDriveDataC2SPacket(entry, offhand);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(entry);
        buf.writeBoolean(offhand);
    }

    public static final ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "player_change_data_drive_data");
    @Override
    public ResourceLocation id() {
        return ID;
    }
}