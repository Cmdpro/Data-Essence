package com.cmdpro.datanessence.data.datatablet;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class DataTabManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static DataTabManager instance;
    protected DataTabManager() {
        super(GSON, "datanessence/data_tablet/tabs");
    }
    public static DataTabManager getOrCreateInstance() {
        if (instance == null) {
            instance = new DataTabManager();
        }
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Entries.tabs.clear();
        DataNEssence.LOGGER.info("[DATANESSENCE] Adding Data Tablet Tabs");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                DataTab tab = deserializeTab(location, GsonHelper.convertToJsonObject(i.getValue(), "top member"));
                if (tab == null) {
                    continue;
                }
                Entries.tabs.put(i.getKey(), tab);
                DataNEssence.LOGGER.info("[DATANESSENCE] Successfully added tab {}", location);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("[DATANESSENCE ERROR] Parsing error loading tab {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("[DATANESSENCE] Loaded {} Data Tablet Tabs", Entries.tabs.size());
    }
    public static DataTabSerializer serializer = new DataTabSerializer();
    protected DataTab deserializeTab(ResourceLocation id, JsonObject json) {
        DataTabSerializer serializer = this.serializer;
        if (serializer != null) {
            return serializer.read(id, json);
        }
        return null;
    }
}
