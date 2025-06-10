package com.cmdpro.datanessence.api.essence;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EssenceBarBackgroundType {
    public static final ResourceLocation ESSENCE_BAR_BACKGROUND_LOCATION = DataNEssence.locate("textures/gui/essence_bar_backgrounds.png");
    public final EssenceType.EssenceBarSpriteLocation tinyBarSprite, bigBarSprite, iconSprite, unknownIconSprite;
    public EssenceBarBackgroundType(EssenceType.EssenceBarSpriteLocation tinyBarSprite, EssenceType.EssenceBarSpriteLocation bigBarSprite, EssenceType.EssenceBarSpriteLocation iconSprite, EssenceType.EssenceBarSpriteLocation unknownIconSprite) {
        this.tinyBarSprite = tinyBarSprite;
        this.bigBarSprite = bigBarSprite;
        this.iconSprite = iconSprite;
        this.unknownIconSprite = unknownIconSprite;
    }
}
