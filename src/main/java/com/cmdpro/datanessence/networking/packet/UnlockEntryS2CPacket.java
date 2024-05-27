package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class UnlockEntryS2CPacket {
    private final ResourceLocation unlocked;

    public UnlockEntryS2CPacket(ResourceLocation unlocked) {
        this.unlocked = unlocked;
    }

    public UnlockEntryS2CPacket(FriendlyByteBuf buf) {
        unlocked = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(unlocked);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(this, supplier));
        });
        context.setPacketHandled(true);
    }

    public static class ClientPacketHandler {
        public static void handlePacket(UnlockEntryS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            if (!ClientPlayerUnlockedEntries.getUnlocked().contains(msg.unlocked)) {
                ClientPlayerUnlockedEntries.getUnlocked().add(msg.unlocked);
            }
        }
    }
}