package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public class JukeboxSongRegistry {
    public static ResourceKey<JukeboxSong> UNDER_THE_SKY = create(DataNEssence.locate("under_the_sky"));
    public static ResourceKey<JukeboxSong> create(ResourceLocation loc) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, loc);
    }
}
