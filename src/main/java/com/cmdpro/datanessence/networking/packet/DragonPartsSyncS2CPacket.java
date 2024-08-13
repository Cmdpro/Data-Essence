package com.cmdpro.datanessence.networking.packet;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record DragonPartsSyncS2CPacket(int id, boolean horns, boolean tail, boolean wings) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (player.level().getEntity(id) instanceof Player target) {
            target.setData(AttachmentTypeRegistry.HAS_HORNS, horns);
            target.setData(AttachmentTypeRegistry.HAS_TAIL, tail);
            target.setData(AttachmentTypeRegistry.HAS_WINGS, wings);
        }
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, DragonPartsSyncS2CPacket obj) {
        pBuffer.writeInt(obj.id);
        pBuffer.writeBoolean(obj.horns);
        pBuffer.writeBoolean(obj.tail);
        pBuffer.writeBoolean(obj.wings);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<DragonPartsSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "dragon_parts_sync"));

    public static DragonPartsSyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        boolean horns = buf.readBoolean();
        boolean tail = buf.readBoolean();
        boolean wings = buf.readBoolean();
        return new DragonPartsSyncS2CPacket(id, horns, tail, wings);
    }

}