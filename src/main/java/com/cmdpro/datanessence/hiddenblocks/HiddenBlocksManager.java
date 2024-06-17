package com.cmdpro.datanessence.hiddenblocks;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class HiddenBlocksManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static HiddenBlocksManager instance;
    protected HiddenBlocksManager() {
        super(GSON, "datanessence/hidden_blocks");
    }
    public static HiddenBlocksManager getOrCreateInstance() {
        if (instance == null) {
            instance = new HiddenBlocksManager();
        }
        return instance;
    }
    public static Map<ResourceLocation, HiddenBlock> blocks = new HashMap<>();
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        blocks = new HashMap<>();
        DataNEssence.LOGGER.info("Adding Data and Essence Hidden Blocks");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                HiddenBlock block = serializer.read(i.getKey(), obj);
                blocks.put(i.getKey(), block);
            } catch (IllegalArgumentException | JsonParseException e) {
                DataNEssence.LOGGER.error("Parsing error loading data bank type {}", location, e);
            }
        }
        DataNEssence.LOGGER.info("Loaded {} hidden blocks", blocks.size());
    }
    public static HiddenBlocksSerializer serializer = new HiddenBlocksSerializer();
}
