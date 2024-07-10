package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.computers.ClientComputerData;
import com.cmdpro.datanessence.computers.ComputerData;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.screen.ComputerScreen;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.databank.DataBankEntrySerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record ComputerDataSyncS2CPacket(ComputerData data) implements Message {
    public static ComputerDataSyncS2CPacket read(FriendlyByteBuf buf) {
        ComputerData data = ComputerData.fromNetwork(buf);
        return new ComputerDataSyncS2CPacket(data);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientComputerData.clientComputerData = data;
        Minecraft.getInstance().setScreen(new ComputerScreen(Component.empty()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        ComputerData.toNetwork(buf, data);
    }
    public static ResourceLocation ID = new ResourceLocation(DataNEssence.MOD_ID, "computer_data_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }
}