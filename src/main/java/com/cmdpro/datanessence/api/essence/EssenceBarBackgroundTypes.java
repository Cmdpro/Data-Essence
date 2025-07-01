package com.cmdpro.datanessence.api.essence;

public class EssenceBarBackgroundTypes {
    public static final EssenceBarBackgroundType INDUSTRIAL = new EssenceBarBackgroundType(
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 0, 55),
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 0, 0),
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 0, 80),
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 0, 90)
    );

    public static final EssenceBarBackgroundType LUNAR = new EssenceBarBackgroundType(
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 10, 55),
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 10, 0),
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 10, 80),
            EssenceType.createLocation(EssenceBarBackgroundType.ESSENCE_BAR_BACKGROUND_LOCATION, 10, 90)
    );
}
