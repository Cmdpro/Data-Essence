package com.cmdpro.datanessence.data.pinging;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class PingableStructureManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static PingableStructureManager instance;
    protected PingableStructureManager() {
        super(GSON, "datanessence/pingable_structures");
    }
    public static PingableStructureManager getOrCreateInstance() {
        if (instance == null) {
            instance = new PingableStructureManager();
        }
        return instance;
    }
    public static Map<ResourceLocation, PingableStructure> types;
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        types = new HashMap<>();
        DataNEssence.LOGGER.info("Adding Data and Essence Pingable Structures");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                PingableStructure data = serializer.read(i.getKey(), obj);
                types.put(i.getKey(), data);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("Parsing error loading pingable structure {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("Loaded {} pingable structures", types.size());
    }
    public static PingableStructureSerializer serializer = new PingableStructureSerializer();
}
