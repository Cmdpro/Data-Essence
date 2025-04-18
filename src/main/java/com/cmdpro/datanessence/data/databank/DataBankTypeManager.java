package com.cmdpro.datanessence.data.databank;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
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
        Map<ResourceLocation, ResourceLocation[]> removals = new HashMap<>();
        DataNEssence.LOGGER.info("Adding Data and Essence Data Bank Types");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                ResourceLocation[] ids = serializer.read(i.getKey(), obj);
                List<ResourceLocation> idsList = new ArrayList<>(List.of(ids));
                if (types.containsKey(i.getKey())) {
                    idsList.addAll(List.of(types.get(i.getKey())));
                }
                types.put(i.getKey(), idsList.toArray(new ResourceLocation[0]));
                List<ResourceLocation> removals2 = new ArrayList<>(List.of(serializer.readRemovals(i.getKey(), obj)));
                if (!removals2.isEmpty()) {
                    removals2.addAll(List.of(removals.getOrDefault(i.getKey(), new ResourceLocation[0])));
                    removals.put(i.getKey(), removals2.toArray(new ResourceLocation[0]));
                }
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("Parsing error loading data bank type {}", location, e);
            }
        }
        for (Map.Entry<ResourceLocation, ResourceLocation[]> i : removals.entrySet()) {
            ResourceLocation[] ids = i.getValue();
            List<ResourceLocation> idsList = new ArrayList<>(List.of(types.get(i.getKey())));
            idsList.removeAll(List.of(ids));
            types.put(i.getKey(), idsList.toArray(new ResourceLocation[0]));
        }
        DataNEssence.LOGGER.info("Loaded {} data bank types", types.size());
    }
    public static DataBankTypeSerializer serializer = new DataBankTypeSerializer();
}
