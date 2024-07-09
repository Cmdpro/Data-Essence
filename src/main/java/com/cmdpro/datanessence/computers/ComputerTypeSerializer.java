package com.cmdpro.datanessence.computers;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComputerTypeSerializer {
    public ComputerData read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("files")) {
            throw new JsonSyntaxException("Element files missing in entry JSON for " + entryId.toString());
        }
        List<ComputerFile> files = new ArrayList<>();
        for (JsonElement i : json.getAsJsonArray("files")) {
            ComputerFileType type = DataNEssenceRegistries.COMPUTER_FILE_TYPES_REGISTRY.get(ResourceLocation.tryParse(i.getAsJsonObject().get("type").getAsString()));
            files.add(type.fromJson(i.getAsJsonObject()));
        }
        ComputerData data = new ComputerData(files);
        return data;
    }
}