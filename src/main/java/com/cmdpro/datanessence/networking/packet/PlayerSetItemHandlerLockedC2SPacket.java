package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.ILockableContainer;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.screen.databank.DataBankEntries;
import com.cmdpro.datanessence.screen.databank.DataBankEntry;
import com.cmdpro.datanessence.screen.datatablet.Entries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record PlayerSetItemHandlerLockedC2SPacket(BlockPos blockPos, boolean locked) implements Message {

    public static PlayerSetItemHandlerLockedC2SPacket read(RegistryFriendlyByteBuf buf) {
        BlockPos blockPos = buf.readBlockPos();
        boolean locked = buf.readBoolean();
        return new PlayerSetItemHandlerLockedC2SPacket(blockPos, locked);
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

    public static void write(RegistryFriendlyByteBuf buf, PlayerSetItemHandlerLockedC2SPacket obj) {
        buf.writeBlockPos(obj.blockPos);
        buf.writeBoolean(obj.locked);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<PlayerSetItemHandlerLockedC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "player_set_item_handler_locked"));
}