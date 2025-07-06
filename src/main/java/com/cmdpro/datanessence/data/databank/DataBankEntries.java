package com.cmdpro.datanessence.data.databank;

import com.cmdpro.datanessence.data.datatablet.Entry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class DataBankEntries {
    public static HashMap<ResourceLocation, DataBankEntry> entries = new HashMap<>();
    public static HashMap<ResourceLocation, DataBankEntry> clientEntries = new HashMap<>();
    public static HashMap<ResourceLocation, Entry> playerDatabankClientEntries = new HashMap<>();
}
