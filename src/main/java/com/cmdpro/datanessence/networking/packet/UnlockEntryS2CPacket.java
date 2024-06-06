package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.chunk.ChunkAccess;
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
            Entry entry = Entries.entries.get(msg.unlocked);
            if (entry != null) {
                if (!ClientPlayerUnlockedEntries.getUnlocked().contains(msg.unlocked)) {
                    ClientPlayerUnlockedEntries.getUnlocked().add(msg.unlocked);
                }
                if (entry.critical) {
                    ClientDataNEssenceUtil.unlockedCriticalData(entry);
                }
                ClientDataNEssenceUtil.updateWorld();
            }
        }
    }
}