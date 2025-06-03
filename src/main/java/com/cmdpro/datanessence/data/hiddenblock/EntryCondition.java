package com.cmdpro.datanessence.data.hiddenblock;

import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class EntryCondition extends HiddenBlockConditions.HiddenBlockCondition {
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
    public static class EntryConditionSerializer extends HiddenBlockConditions.HiddenBlockCondition.Serializer {
        public static final EntryConditionSerializer INSTANCE = new EntryConditionSerializer();
        @Override
        public HiddenBlockConditions.HiddenBlockCondition deserialize(JsonObject jsonObject) {
            ResourceLocation entry = ResourceLocation.tryParse(jsonObject.get("entry").getAsString());
            int completionStage = GsonHelper.getAsInt(jsonObject, "completionStage", -1);
            return new EntryCondition(entry, completionStage);
        }
    }
}
