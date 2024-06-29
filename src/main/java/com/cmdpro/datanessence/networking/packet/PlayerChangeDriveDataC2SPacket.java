package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerChangeDriveDataC2SPacket {
    private final ResourceLocation entry;
    private final boolean offhand;

    public PlayerChangeDriveDataC2SPacket(ResourceLocation entry, boolean offhand) {
        this.entry = entry;
        this.offhand = offhand;
    }

    public PlayerChangeDriveDataC2SPacket(FriendlyByteBuf buf) {
        this.entry = buf.readResourceLocation();
        this.offhand = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(entry);
        buf.writeBoolean(offhand);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            context.getSender().getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                if (data.getUnlocked().contains(entry)) {
                    ItemStack stack = context.getSender().getItemInHand(offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                    if (stack.is(ItemRegistry.DATA_DRIVE.get())) {
                        stack.getOrCreateTag().putString("dataId", entry.toString());
                    }
                }
            });
        });
        context.setPacketHandled(true);
    }
}