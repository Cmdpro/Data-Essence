package com.cmdpro.datanessence.screen.databank;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBankTypeManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static DataBankTypeManager instance;
    protected DataBankTypeManager() {
        super(GSON, "datanessence/data_tablet/data_bank_types");
    }
    public static DataBankTypeManager getOrCreateInstance() {
        if (instance == null) {
            instance = new DataBankTypeManager();
        }
        return instance;
    }
    public static Map<ResourceLocation, ResourceLocation[]> types;
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        types = new HashMap<>();
        DataNEssence.LOGGER.info("Adding Data and Essence Data Bank Types");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                ResourceLocation[] ids = serializer.read(i.getKey(), obj);
                types.put(i.getKey(), ids);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("Parsing error loading data bank type {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("Loaded {} data bank types", types.size());
    }
    public static DataBankTypeSerializer serializer = new DataBankTypeSerializer();
}
