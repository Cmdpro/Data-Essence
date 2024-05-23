package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class UnlockedEntrySyncS2CPacket {
    private final List<ResourceLocation> unlocked;

    public UnlockedEntrySyncS2CPacket(List<ResourceLocation> unlocked) {
        this.unlocked = unlocked;
    }

    public UnlockedEntrySyncS2CPacket(FriendlyByteBuf buf) {
        unlocked = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(unlocked, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(this, supplier));
        });
        context.setPacketHandled(true);
    }

    public static class ClientPacketHandler {
        public static void handlePacket(UnlockedEntrySyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            ClientPlayerUnlockedEntries.set(msg.unlocked);
        }
    }
}