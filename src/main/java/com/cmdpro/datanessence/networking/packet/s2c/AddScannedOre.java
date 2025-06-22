package com.cmdpro.datanessence.networking.packet.s2c;


import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.networking.Message;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record AddScannedOre(HashMap<BlockPos, Integer> ores) implements Message {

    public static HashMap<BlockPos, Integer> scanned = new HashMap<>();

    public static final Type<AddScannedOre> TYPE = new Type<>(DataNEssence.locate("add_scanned_ore"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void write(RegistryFriendlyByteBuf buf, AddScannedOre obj) {
        buf.writeMap(obj.ores, RegistryFriendlyByteBuf::writeBlockPos, FriendlyByteBuf::writeInt);
    }

    public static AddScannedOre read(RegistryFriendlyByteBuf buf) {
        HashMap<BlockPos, Integer> ores = new HashMap<>(buf.readMap(RegistryFriendlyByteBuf::readBlockPos, FriendlyByteBuf::readInt));
        return new AddScannedOre(ores);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        for (Map.Entry<BlockPos, Integer> i : ores.entrySet()) {
            if (!scanned.containsKey(i.getKey()) || scanned.get(i.getKey()) > i.getValue()) {
                scanned.put(i.getKey(), i.getValue());
            }
        }
    }
}