package EsetKalenko.Halcyon.data.hidden;

import EsetKalenko.Halcyon.Halcyon;
import com.cmdpro.databank.hidden.HiddenCondition;
import EsetKalenko.Halcyon.data.datatablet.Entries;
import EsetKalenko.Halcyon.data.datatablet.Entry;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
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
        if (player == null) {
            return true;
        }
        if (player.getData(AttachmentTypeRegistry.UNLOCKED).contains(entry)) {
            return true;
        } else if (completionStage != -1) {
            Entry entry = Entries.entries.get(this.entry);

            if (entry == null)
                return false;

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
