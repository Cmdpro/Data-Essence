package com.cmdpro.datanessence.api.essence;

import net.minecraft.network.chat.Component;

public enum EssenceType {
    ESSENCE(Component.translatable("datanessence.essence_types.essence"), 0), // Artifice, industry, electricity
    LUNAR_ESSENCE(Component.translatable("datanessence.essence_types.lunar_essence"), 0), // Transformation, the moon, rule-bending
    NATURAL_ESSENCE(Component.translatable("datanessence.essence_types.natural_essence"), 0), // Nature, life, the world
    EXOTIC_ESSENCE(Component.translatable("datanessence.essence_types.exotic_essence"), 0); // Stars, space, dreams
    // How *strange*. It feels like something's missing.
    // TODO set colors properly

    public static final EssenceType[] essences = values();
    public final Component name;
    public final int color;

    EssenceType(Component name, int color) {
        this.name = name;
        this.color = color;
    }

    public Component getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
