package com.cmdpro.datanessence.networking.packet.c2s;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.misc.ILockableContainer;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record PlayerSetItemHandlerLocked(BlockPos blockPos, boolean locked) implements Message {

    public static PlayerSetItemHandlerLocked read(RegistryFriendlyByteBuf buf) {
        BlockPos blockPos = buf.readBlockPos();
        boolean locked = buf.readBoolean();
        return new PlayerSetItemHandlerLocked(blockPos, locked);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if (player.level().getBlockEntity(blockPos) instanceof ILockableContainer lockable) {
            for (LockableItemHandler handler : lockable.getLockable()) {
                handler.setLocked(locked);
                handler.clearLockedSlots();
                if (handler.locked) {
                    handler.setLockedSlots();
                }
            }
        }
    }

    public static void write(RegistryFriendlyByteBuf buf, PlayerSetItemHandlerLocked obj) {
        buf.writeBlockPos(obj.blockPos);
        buf.writeBoolean(obj.locked);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerSetItemHandlerLocked> TYPE = new Type<>(DataNEssence.locate("player_set_item_handler_locked"));
}