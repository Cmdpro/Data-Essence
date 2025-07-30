package com.cmdpro.datanessence.api.misc;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class SymbolTypes {
    public static HashMap<ResourceLocation, SymbolType> symbols = new HashMap<>();
    static {
        /*
        Large symbols are 32x32
        Normal symbols are 16x16
        Small symbols are 8x8

        You can return null for any of the 3 symbol locations, returning null basically just means the symbol has nothing for that size
        It shouldn't crash if one of the symbol locations is null and you try to blit it, it should simply do nothing

        It may also be nice to add a static final variable in here that directly links to the symbol type

        Example:
        symbols.put(DataNEssence.locate("example"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 32),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 48)
        ));

        Example with null:
        symbols.put(DataNEssence.locate("example2"), new SymbolType(
                null,
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 32),
                null
        ));
         */

        // The Cities - also used to represent the seven colors
        symbols.put(DataNEssence.locate("shar"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 16)
        ));
        symbols.put(DataNEssence.locate("besar"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 32),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 48)
        ));
        symbols.put(DataNEssence.locate("nula"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 64),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 80)
        ));
        symbols.put(DataNEssence.locate("eniro"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 96),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 112)
        ));
        symbols.put(DataNEssence.locate("ipari"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 128),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 144)
        ));
        symbols.put(DataNEssence.locate("koru"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 160),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 176)
        ));
        symbols.put(DataNEssence.locate("aru"), new SymbolType(
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 0, 0),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 192),
                SymbolType.getSymbolLocation(SymbolType.SYMBOL_LOCATION, 32, 208)
        ));
    }
}
