package com.cmdpro.datanessence.data.hidden;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class EntryCondition extends HiddenCondition {
    public ResourceLocation entry;
    public int completionStage;
    public EntryCondition(ResourceLocation entry, int completionStage) {
        this.entry = entry;
        this.completionStage = completionStage;
    }
    @Override
    public boolean isUnlocked(Player player) {
        if (player == null) {
            return true;
        }
        if (player.getData(AttachmentTypeRegistry.UNLOCKED).contains(entry)) {
            return true;
        } else if (completionStage != -1) {
            Entry entry = Entries.entries.get(this.entry);
            return entry.getIncompleteStageServer(player) >= completionStage;
        }
        return false;
    }

    @Override
    public Serializer getSerializer() {
        return EntryConditionSerializer.INSTANCE;
    }
    public static class EntryConditionSerializer extends HiddenCondition.Serializer<EntryCondition> {
        public static final EntryConditionSerializer INSTANCE = new EntryConditionSerializer();
        public static final MapCodec<EntryCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("entry").forGetter((condition) -> condition.entry),
                Codec.INT.optionalFieldOf("completion_stage", -1).forGetter((condition) -> condition.completionStage)
        ).apply(instance, EntryCondition::new));

        @Override
        public MapCodec<EntryCondition> codec() {
            return CODEC;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, EntryCondition> STREAM_CODEC = StreamCodec.of((buf, val) -> {
            buf.writeResourceLocation(val.entry);
            buf.writeInt(val.completionStage);
        }, (buf) -> {
            ResourceLocation entry = buf.readResourceLocation();
            int completionStage = buf.readInt();
            return new EntryCondition(entry, completionStage);
        });
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EntryCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
