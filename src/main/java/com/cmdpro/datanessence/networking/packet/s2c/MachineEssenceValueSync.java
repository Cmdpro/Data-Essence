package com.cmdpro.datanessence.networking.packet.s2c;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.client.ClientEvents;
import com.cmdpro.datanessence.item.equipment.EssenceMeter;
import com.cmdpro.datanessence.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record MachineEssenceValueSync(BlockPos pos, Map<EssenceType, Float> values, float maxValue) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext ctx) {
        EssenceMeter.currentMachineEssenceValue = new MachineEssenceValue(pos, values, maxValue);
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, MachineEssenceValueSync obj) {
        pBuffer.writeBlockPos(obj.pos);
        pBuffer.writeMap(obj.values, (buf, type) -> buf.writeResourceKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getResourceKey(type).orElseThrow()), FriendlyByteBuf::writeFloat);
        pBuffer.writeFloat(obj.maxValue);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<MachineEssenceValueSync> TYPE = new Type<>(DataNEssence.locate("machine_essence_value_sync"));

    public static MachineEssenceValueSync read(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Map<EssenceType, Float> values = buf.readMap((buf2) -> {
            ResourceKey<EssenceType> essenceType = buf2.readResourceKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY_KEY);
            return DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(essenceType);
        }, FriendlyByteBuf::readFloat);
        float maxValue = buf.readFloat();
        return new MachineEssenceValueSync(pos, values, maxValue);
    }
    public static class MachineEssenceValue {
        public BlockPos pos;
        public Map<EssenceType, Float> values;
        public float maxValue;
        public MachineEssenceValue(BlockPos pos, Map<EssenceType, Float> values, float maxValue) {
            this.pos = pos;
            this.values = values;
            this.maxValue = maxValue;
        }
    }
    public static MachineEssenceValueSync create(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof EssenceBlockEntity essenceBlockEntity) {
            Map<EssenceType, Float> values = new HashMap<>();
            for (EssenceType i : essenceBlockEntity.getStorage().getSupportedEssenceTypes()) {
                values.put(i, essenceBlockEntity.getStorage().getEssence(i));
            }
            return new MachineEssenceValueSync(pos, values, essenceBlockEntity.getStorage().getMaxEssence());
        }
        return null;
    }
}