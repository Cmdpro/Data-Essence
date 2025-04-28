package com.cmdpro.datanessence.data.computers;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class ComputerTypeManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static ComputerTypeManager instance;
    protected ComputerTypeManager() {
        super(GSON, "datanessence/computer_types");
    }
    public static ComputerTypeManager getOrCreateInstance() {
        if (instance == null) {
            instance = new ComputerTypeManager();
        }
        return instance;
    }
    public static Map<ResourceLocation, ComputerData> types;
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        types = new HashMap<>();
        DataNEssence.LOGGER.info("[DATANESSENCE] Adding Computer Types");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                ComputerData data = serializer.read(i.getKey(), obj);
                types.put(i.getKey(), data);
                DataNEssence.LOGGER.info("[DATANESSENCE] Successfully added computer type {}", location);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("[DATANESSENCE] Parsing error loading computer type {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("[DATANESSENCE] Loaded {} computer types", types.size());
    }
    public static ComputerTypeSerializer serializer = new ComputerTypeSerializer();
}
