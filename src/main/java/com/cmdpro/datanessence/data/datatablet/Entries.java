package com.cmdpro.datanessence.data.datatablet;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Entries {
    public static HashMap<ResourceLocation, Entry> entries = new HashMap<>();
    public static HashMap<ResourceLocation, DataTab> tabs = new HashMap<>();
    public static List<DataTab> tabsSorted = new ArrayList<>();
}
