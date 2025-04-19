package com.cmdpro.datanessence.data.pinging;

import com.cmdpro.datanessence.api.computer.ComputerFile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class PingableStructure {
    public ResourceKey<Structure> structure;
    public ResourceKey<Advancement> advancement;
    public Optional<ResourceKey<Advancement>> requiredAdvancement;
    public Color color1;
    public Color color2;
    public PingableStructureIcon icon;
    public PingableStructure(ResourceKey<Structure> structure, ResourceKey<Advancement> advancement, Optional<ResourceKey<Advancement>> requiredAdvancement, Color color1, Color color2, PingableStructureIcon icon) {
        this.structure = structure;
        this.advancement = advancement;
        this.requiredAdvancement = requiredAdvancement;
        this.color1 = color1;
        this.color2 = color2;
        this.icon = icon;
    }
    public static class PingableStructureIcon {
        public static final MapCodec<PingableStructureIcon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter((obj) -> obj.texture),
                Codec.INT.fieldOf("u").forGetter((obj) -> obj.u),
                Codec.INT.fieldOf("v").forGetter((obj) -> obj.v)
        ).apply(instance, PingableStructureIcon::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, PingableStructureIcon> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
            buffer.writeResourceLocation(value.texture);
            buffer.writeInt(value.u);
            buffer.writeInt(value.v);
        }, buffer -> {
            ResourceLocation texture = buffer.readResourceLocation();
            int u = buffer.readInt();
            int v = buffer.readInt();
            return new PingableStructureIcon(texture, u, v);
        });
        public ResourceLocation texture;
        public int u;
        public int v;
        protected PingableStructureIcon(ResourceLocation texture, int u, int v) {
            this.texture = texture;
            this.u = u;
            this.v = v;
        }
    }
}
