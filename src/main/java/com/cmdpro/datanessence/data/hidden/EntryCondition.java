package com.cmdpro.datanessence.data.hidden;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class EntryCondition extends HiddenCondition {
    public ResourceLocation entry;
    public int completionStage;
    public EntryCondition(ResourceLocation entry, int completionStage) {
        this.entry = entry;
        this.completionStage = completionStage;
    }
    @Override
    public boolean isUnlocked(Player player) {
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
    }
}
