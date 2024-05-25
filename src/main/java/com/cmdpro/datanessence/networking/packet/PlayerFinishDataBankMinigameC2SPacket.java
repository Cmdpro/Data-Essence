package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerFinishDataBankMinigameC2SPacket {
    private final ResourceLocation entry;

    public PlayerFinishDataBankMinigameC2SPacket(ResourceLocation entry) {
        this.entry = entry;
    }

    public PlayerFinishDataBankMinigameC2SPacket(FriendlyByteBuf buf) {
        this.entry = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(entry);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            supplier.get().getSender().getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data) -> {
                DataBankEntry entry2 = DataBankEntries.entries.get(entry);
                if (entry2.tier <= data.getTier()) {
                    DataNEssenceUtil.DataTabletUtil.unlockEntry(supplier.get().getSender(), entry2.id);
                }
            });
        });
        context.setPacketHandled(true);
    }
}