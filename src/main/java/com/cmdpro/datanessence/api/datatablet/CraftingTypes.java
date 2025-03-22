package com.cmdpro.datanessence.api.datatablet;

import com.cmdpro.datanessence.datatablet.pages.crafting.types.*;

import java.util.ArrayList;
import java.util.List;

public class CraftingTypes {
    public static List<CraftingType> types = new ArrayList<>();
    static {
        types.add(new CraftingTableType());
        types.add(new FabricatorType());
        types.add(new InfuserType());
        types.add(new EntropicProcessorType());
        types.add(new FluidMixingType());
        types.add(new SynthesisType());
        types.add(new SmeltingType());
        types.add(new MeltingType());
        types.add(new DryingType());
    }
}
