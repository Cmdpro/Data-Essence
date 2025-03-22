package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record DragonPartsSync(int id, boolean horns, boolean tail, boolean wings) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (player.level().getEntity(id) instanceof Player target) {
            target.setData(AttachmentTypeRegistry.HAS_HORNS, horns);
            target.setData(AttachmentTypeRegistry.HAS_TAIL, tail);
            target.setData(AttachmentTypeRegistry.HAS_WINGS, wings);
        }
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, DragonPartsSync obj) {
        pBuffer.writeInt(obj.id);
        pBuffer.writeBoolean(obj.horns);
        pBuffer.writeBoolean(obj.tail);
        pBuffer.writeBoolean(obj.wings);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<DragonPartsSync> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "dragon_parts_sync"));

    public static DragonPartsSync read(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        boolean horns = buf.readBoolean();
        boolean tail = buf.readBoolean();
        boolean wings = buf.readBoolean();
        return new DragonPartsSync(id, horns, tail, wings);
    }

}