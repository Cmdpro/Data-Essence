package com.cmdpro.datanessence.datatablet;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class EntryManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static EntryManager instance;
    protected EntryManager() {
        super(GSON, "datanessence/data_tablet/entries");
    }
    public static EntryManager getOrCreateInstance() {
        if (instance == null) {
            instance = new EntryManager();
        }
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Entries.entries.clear();
        DataNEssence.LOGGER.info("Adding Data and Essence Entries");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                Entry entry = deserializeEntry(location, GsonHelper.convertToJsonObject(i.getValue(), "top member"));
                if (entry == null) {
                    continue;
                }
                Entries.entries.put(i.getKey(), entry);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("Parsing error loading entry {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("Loaded {} entries", Entries.entries.size());
        for (Entry i : Entries.entries.values()) {
            for (Entry o : Entries.entries.values()) {
                if(i != o && i.x == o.x && i.y == o.y && i.tab.equals(o.tab)) {
                    DataNEssence.LOGGER.warn("Entry \"" + i.id + "\" is overlapping with entry \"" + o.id + "\"");
                }
            }
            i.updateParentEntries();
        }
    }
    public static EntrySerializer serializer = new EntrySerializer();
    protected Entry deserializeEntry(ResourceLocation id, JsonObject json) {
        EntrySerializer serializer = this.serializer;
        if (serializer != null) {
            return serializer.read(id, json);
        }
        return null;
    }
}
