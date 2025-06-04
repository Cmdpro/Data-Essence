package com.cmdpro.datanessence.networking.packet.c2s;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public record PlayerChangeDriveData(ResourceLocation entry, int completionStage, boolean offhand) implements Message {

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        HashMap<ResourceLocation, Integer> incomplete = player.getData(AttachmentTypeRegistry.INCOMPLETE_STAGES);
        if (unlocked.contains(entry) || incomplete.containsKey(entry)) {
            ItemStack stack = player.getItemInHand(offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (stack.is(ItemRegistry.DATA_DRIVE.get())) {
                stack.set(DataComponentRegistry.DATA_ID, entry);
                stack.set(DataComponentRegistry.DATA_INCOMPLETE, completionStage);
            }
        }
    }

    public static PlayerChangeDriveData read(RegistryFriendlyByteBuf buf) {
        ResourceLocation entry = buf.readResourceLocation();
        int completionStage = buf.readInt();
        boolean offhand = buf.readBoolean();
        return new PlayerChangeDriveData(entry, completionStage, offhand);
    }
    public static void write(RegistryFriendlyByteBuf buf, PlayerChangeDriveData obj) {
        buf.writeResourceLocation(obj.entry);
        buf.writeInt(obj.completionStage);
        buf.writeBoolean(obj.offhand);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerChangeDriveData> TYPE = new Type<>(DataNEssence.locate("player_change_data_drive_data"));
}