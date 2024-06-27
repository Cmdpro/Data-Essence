package com.cmdpro.datanessence.screen.datatablet.pages.crafting;

import com.cmdpro.datanessence.screen.datatablet.pages.crafting.types.CraftingTableType;
import com.cmdpro.datanessence.screen.datatablet.pages.crafting.types.FabricatorType;
import com.cmdpro.datanessence.screen.datatablet.pages.crafting.types.InfuserType;

import java.util.ArrayList;
import java.util.List;

public class CraftingTypes {
    public static List<CraftingType> types = new ArrayList<>();
    static {
        types.add(new CraftingTableType());
        types.add(new FabricatorType());
        types.add(new InfuserType());
    }
}
