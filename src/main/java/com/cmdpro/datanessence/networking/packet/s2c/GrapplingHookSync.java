package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.equipment.GrapplingHook;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record GrapplingHookSync(int id, GrapplingHook.GrapplingHookData data) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (player.level().getEntity(id) instanceof Player target) {
            target.setData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA, data != null ? Optional.of(data) : Optional.empty());
        }
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, GrapplingHookSync obj) {
        pBuffer.writeInt(obj.id);
        pBuffer.writeBoolean(obj.data != null);
        if (obj.data != null) {
            pBuffer.writeVec3(obj.data.pos);
            pBuffer.writeDouble(obj.data.distance);
        }
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<GrapplingHookSync> TYPE = new Type<>(DataNEssence.locate("grappling_hook_sync"));

    public static GrapplingHookSync read(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        boolean active = buf.readBoolean();
        GrapplingHook.GrapplingHookData data = null;
        if (active) {
            Vec3 pos = buf.readVec3();
            double distance = buf.readDouble();
            data = new GrapplingHook.GrapplingHookData(pos, distance);
        }
        return new GrapplingHookSync(id, data);
    }

}