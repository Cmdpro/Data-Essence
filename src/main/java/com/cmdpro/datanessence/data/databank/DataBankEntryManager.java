package com.cmdpro.datanessence.data.databank;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class DataBankEntryManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static DataBankEntryManager instance;
    protected DataBankEntryManager() {
        super(GSON, "datanessence/data_tablet/data_bank_entries");
    }
    public static DataBankEntryManager getOrCreateInstance() {
        if (instance == null) {
            instance = new DataBankEntryManager();
        }
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        DataBankEntries.entries.clear();
        DataNEssence.LOGGER.info("Adding Data and Essence Data Bank Entries");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                DataBankEntry entry = deserializeEntry(location, GsonHelper.convertToJsonObject(i.getValue(), "top member"));
                if (entry == null) {
                    continue;
                }
                DataBankEntries.entries.put(i.getKey(), entry);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("Parsing error loading data bank entry {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("Loaded {} data bank entries", DataBankEntries.entries.size());
    }
    public static DataBankEntrySerializer serializer = new DataBankEntrySerializer();
    protected DataBankEntry deserializeEntry(ResourceLocation id, JsonObject json) {
        DataBankEntrySerializer serializer = this.serializer;
        if (serializer != null) {
            return serializer.read(id, json);
        }
        return null;
    }
}
