package com.cmdpro.datanessence.computers;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.computer.ComputerFile;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ComputerTypeSerializer {
    public ComputerData read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("files")) {
            throw new JsonSyntaxException("Element files missing in computer type JSON for " + entryId.toString());
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