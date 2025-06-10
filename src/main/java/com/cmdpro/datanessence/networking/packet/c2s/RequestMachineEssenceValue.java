package com.cmdpro.datanessence.networking.packet.c2s;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.misc.ILockableContainer;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.MachineEssenceValueSync;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record RequestMachineEssenceValue(BlockPos blockPos) implements Message {

    public static RequestMachineEssenceValue read(RegistryFriendlyByteBuf buf) {
        BlockPos blockPos = buf.readBlockPos();
        return new RequestMachineEssenceValue(blockPos);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        MachineEssenceValueSync packet = MachineEssenceValueSync.create(player.level(), blockPos);
        if (packet != null) {
            ModMessages.sendToPlayer(packet, player);
        }
    }

    public static void write(RegistryFriendlyByteBuf buf, RequestMachineEssenceValue obj) {
        buf.writeBlockPos(obj.blockPos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<RequestMachineEssenceValue> TYPE = new Type<>(DataNEssence.locate("request_machine_essence_value"));
}