package com.cmdpro.datanessence.api.essence;

import net.minecraft.network.chat.Component;

public enum EssenceType {
    ESSENCE(Component.translatable("datanessence.essence_types.essence"), 0xe236ef), // Artifice, industry, electricity
    LUNAR_ESSENCE(Component.translatable("datanessence.essence_types.lunar_essence"), 0xf5fbc0), // Transformation, the moon, rule-bending
    NATURAL_ESSENCE(Component.translatable("datanessence.essence_types.natural_essence"), 0x57f36c), // Nature, life, the world
    EXOTIC_ESSENCE(Component.translatable("datanessence.essence_types.exotic_essence"), 0xe7fcf9); // Stars, space, dreams
    // How *strange*. It feels like something's missing.

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
