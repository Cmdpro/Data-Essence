package com.cmdpro.datanessence.api.essence;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EssenceType {
    public static final ResourceLocation ESSENCE_BAR_LOCATION = ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/gui/essence_bars.png");

    public final Component name;
    public final int color;
    public final EssenceBarSpriteLocation tinyBarSprite, bigBarSprite;

    public EssenceType(Component name, int color, EssenceBarSpriteLocation tinyBarSprite, EssenceBarSpriteLocation bigBarSprite) {
        this.name = name;
        this.color = color;
        this.tinyBarSprite = tinyBarSprite;
        this.bigBarSprite = bigBarSprite;
    }
    public static EssenceBarSpriteLocation createLocation(ResourceLocation location, int x, int y) {
        return new EssenceBarSpriteLocation(location, x, y);
    }

    public Component getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
    public EssenceBarSpriteLocation getTinyBarSprite() {
        return tinyBarSprite;
    }

    public EssenceBarSpriteLocation getBigBarSprite() {
        return bigBarSprite;
    }
    private static class EssenceBarSpriteLocation {
        public EssenceBarSpriteLocation(ResourceLocation texture, int x, int y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
        }
        public ResourceLocation texture;
        public int x;
        public int y;
    }
}
