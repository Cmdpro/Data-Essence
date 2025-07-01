package com.cmdpro.datanessence.data.datatablet;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

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
        HashMap<DataTab.DataTabPlacement, List<DataTab>> placements = new HashMap<>();
        DataTab.DataTabPlacement bothIgnored = new DataTab.DataTabPlacement(DataTab.DataTabPlacement.IGNORED, DataTab.DataTabPlacement.IGNORED);
        DataTab.DataTabPlacement bothAll = new DataTab.DataTabPlacement(DataTab.DataTabPlacement.ALL, DataTab.DataTabPlacement.ALL);
        placements.put(bothIgnored, new ArrayList<>());
        placements.put(bothAll, new ArrayList<>());
        for (DataTab i : Entries.tabs.values()) {
            List<DataTab> list = placements.getOrDefault(i.placement, new ArrayList<>());
            list.add(i);
            placements.put(i.placement, list);
        }
        List<DataTab> sorted = new ArrayList<>(placements.get(bothIgnored));

        List<DataTab> toSort = new ArrayList<>();
        placements.entrySet().stream()
                .filter((i) -> i.getKey() != bothAll)
                .filter((i) ->
                        (i.getKey().placeBefore().equals(DataTab.DataTabPlacement.ALL)) ||
                                (i.getKey().placeAfter().equals(DataTab.DataTabPlacement.ALL)))
                .map(Map.Entry::getValue).forEach(toSort::addAll);
        for (DataTab i : toSort) {
            if (i.placement.placeBefore().equals(DataTab.DataTabPlacement.ALL)) {
                if (i.placement.placeAfter().equals(DataTab.DataTabPlacement.ALL)) {
                    DataNEssence.LOGGER.error("[DATANESSENCE ERROR] Unable to sort {}, both \"place_after\" and \"place_before\" are set to \"datanessence:all\"", i.id);
                    continue;
                }
                if (!i.placement.placeAfter().equals(DataTab.DataTabPlacement.IGNORED)) {
                    DataNEssence.LOGGER.error("[DATANESSENCE ERROR] Unable to sort {}, \"place_before\" is set to \"datanessence:all\" but place_after is not ignored", i.id);
                    continue;
                }
                sorted.addFirst(i);
            } else if (i.placement.placeAfter().equals(DataTab.DataTabPlacement.ALL)) {
                if (!i.placement.placeBefore().equals(DataTab.DataTabPlacement.IGNORED)) {
                    DataNEssence.LOGGER.error("[DATANESSENCE ERROR] Unable to sort {}, \"place_after\" is set to \"datanessence:all\" but place_before is not ignored", i.id);
                    continue;
                }
                sorted.addLast(i);
            }
        }
        toSort = new ArrayList<>();
        placements.entrySet().stream()
                .filter((i) -> !isSpecial(i.getKey().placeBefore()) || !isSpecial(i.getKey().placeAfter()))
                .filter((i) -> !i.getKey().placeBefore().equals(DataTab.DataTabPlacement.ALL) && !i.getKey().placeAfter().equals(DataTab.DataTabPlacement.ALL))
                .map(Map.Entry::getValue).forEach(toSort::addAll);
        List<DataTab> errored = new ArrayList<>(new ArrayList<>(toSort).stream()
                .filter((i) ->
                        ((!Entries.tabs.containsKey(i.placement.placeBefore()) && !isSpecial(i.placement.placeBefore()))) ||
                        ((!Entries.tabs.containsKey(i.placement.placeAfter()) && !isSpecial(i.placement.placeAfter()))) ||
                        i.id.equals(i.placement.placeBefore()) || i.id.equals(i.placement.placeAfter())
                )
                .toList());
        toSort.removeAll(errored);
        for (DataTab i : errored) {
            boolean beforeErrored = !Entries.tabs.containsKey(i.placement.placeBefore());
            boolean afterErrored = !Entries.tabs.containsKey(i.placement.placeBefore());
            if (beforeErrored && afterErrored) {
                String before = i.placement.placeBefore().toString();
                String after = i.placement.placeBefore().toString();
                DataNEssence.LOGGER.error("[DATANESSENCE ERROR] Unable to sort {}, both \"{}\" and \"{}\" dont exist", i.id, before, after);
            } else {
                String erroredValue = beforeErrored ? i.placement.placeBefore().toString() : afterErrored ? i.placement.placeAfter().toString() : "unknown";
                DataNEssence.LOGGER.error("[DATANESSENCE ERROR] Unable to sort {}, \"{}\" doesnt exist", i.id, erroredValue);
            }
        }
        errored.clear();
        while (!toSort.isEmpty()) {
            for (DataTab i : new ArrayList<>(toSort)) {
                int index = -1;
                var after = sorted.stream().filter((j) -> j.id.equals(i.placement.placeAfter())).findFirst();
                if (!isSpecial(i.placement.placeAfter())) {
                    if (after.isPresent()) {
                        index = sorted.indexOf(after.get()) + 1;
                    } else {
                        continue;
                    }
                }
                var before = sorted.stream().filter((j) -> j.id.equals(i.placement.placeBefore())).findFirst();
                if (!isSpecial(i.placement.placeBefore())) {
                    if (before.isPresent()) {
                        if (index != -1) {
                            int max = sorted.indexOf(before.get());
                            if (index > max) {
                                index = max;
                            }
                        } else {
                            index = sorted.indexOf(before.get());
                        }
                    } else {
                        continue;
                    }
                }
                if (index != -1) {
                    sorted.add(index, i);
                    toSort.remove(i);
                }
            }
        }
        Entries.tabsSorted = sorted;
        DataNEssence.LOGGER.info("[DATANESSENCE] Sorted {} Data Tablet Tabs", Entries.tabsSorted.size());
    }
    private boolean isSpecial(ResourceLocation placement) {
        return placement.equals(DataTab.DataTabPlacement.ALL) || placement.equals(DataTab.DataTabPlacement.IGNORED);
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
