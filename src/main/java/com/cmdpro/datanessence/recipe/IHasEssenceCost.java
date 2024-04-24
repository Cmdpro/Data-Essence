package com.cmdpro.datanessence.recipe;

import java.util.Map;

public interface IHasEssenceCost {
    default float getEssenceCost() { return 0; }
    default float getLunarEssenceCost() { return 0; }
    default float getNaturalEssenceCost() { return 0; }
    default float getExoticEssenceCost() { return 0; }
}
