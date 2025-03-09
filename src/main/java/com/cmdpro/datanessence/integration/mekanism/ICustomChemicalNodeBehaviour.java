package com.cmdpro.datanessence.integration.mekanism;

import mekanism.api.chemical.IChemicalHandler;

public interface ICustomChemicalNodeBehaviour {
    default boolean canExtractChemical(IChemicalHandler handler, IChemicalHandler other) {
        return true;
    }
    default boolean canInsertChemical(IChemicalHandler handler, IChemicalHandler other) {
        return true;
    }
}
