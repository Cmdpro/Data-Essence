package com.cmdpro.datanessence.data.hiddenblock;

import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class EntryCondition extends HiddenBlockConditions.HiddenBlockCondition {
    public ResourceLocation entry;
    public boolean completionRequired;
    public EntryCondition(ResourceLocation entry, boolean completionRequired) {
        this.entry = entry;
        this.completionRequired = completionRequired;
    }
    @Override
    public boolean isUnlocked(Player player) {
        if (player.getData(AttachmentTypeRegistry.UNLOCKED).contains(entry)) {
            return true;
        } else if (!completionRequired) {
            if (player.getData(AttachmentTypeRegistry.INCOMPLETE).contains(entry)) {
                return true;
            }
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
            boolean completionRequired = GsonHelper.getAsBoolean(jsonObject, "completionRequired", true);
            return new EntryCondition(entry, completionRequired);
        }
    }
}
