package EsetKalenko.Halcyon.client;

import EsetKalenko.Halcyon.Halcyon;
import EsetKalenko.Halcyon.config.DataNEssenceClientConfig;
import com.cmdpro.databank.misc.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = Halcyon.MOD_ID)
public class StructureSongs {
    public static final HashMap<ResourceLocation, StructureMusic> STRUCTURE_SONGS = new HashMap<>();

    public static void addStructureSong(ResourceLocation id) {
        STRUCTURE_SONGS.put(id, new StructureMusic(
                new StructureSongSoundInstance(
                        id,
                        SoundSource.MASTER,
                        1.0f,
                        1.0f,
                        SoundInstance.createUnseededRandom(),
                        false,
                        0,
                        SoundInstance.Attenuation.NONE,
                        0.0,
                        0.0,
                        0.0,
                        false)));
    }

    public static StructureMusic getSong(SoundEvent event) {
        return STRUCTURE_SONGS.get(event.getLocation());
    }

    @SubscribeEvent
    public static void handleStructureSongs(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        SoundManager soundManager = client.getSoundManager();
        MusicManager musicManager = client.getMusicManager();

        float currentVolume = client.options.getSoundSourceVolume(SoundSource.MASTER);

        if (currentVolume > 0f) {
            for (var sound : StructureSongs.STRUCTURE_SONGS.values()) {
                boolean playing = false;

                if (soundManager.isActive(sound.sound)) {
                        musicManager.stopPlaying();
                        playing = true;

                    if (!sound.isSetToPlay) {
                        soundManager.stop(sound.sound);
                    }
                } else if (sound.isSetToPlay) {
                    soundManager.play(sound.sound);
                    playing = true;
                }

                if (playing) {
                    float volume = (sound.sound.getVolume() * DataNEssenceClientConfig.structureSongVolume * 0.01f);

                    if (SoundUtil.getChannelHandle(sound.sound) == null) {
                        soundManager.play(sound.sound);
                    } else {
                        SoundUtil.modifySound(sound.sound, (channel) -> channel.setVolume(volume));
                    }
                }
            }
        }
    }

    public static class StructureMusic {
        public StructureSongSoundInstance sound;
        private boolean isSetToPlay;

        public StructureMusic(StructureSongSoundInstance sound) {
            this.sound = sound;
            this.sound.music = this;
            this.isSetToPlay = false;
        }

        public void stop() {
            isSetToPlay = false;
        }

        public void start() {
            if (!isSetToPlay) {
                isSetToPlay = true;
            }
        }
    }

    public static class StructureSongSoundInstance extends SimpleSoundInstance {
        public StructureMusic music;

        public StructureSongSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, BlockPos entity) {
            super(soundEvent, source, volume, pitch, random, entity);
        }

        public StructureSongSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, double x, double y, double z) {
            super(soundEvent, source, volume, pitch, random, x, y, z);
        }

        public StructureSongSoundInstance(ResourceLocation location, SoundSource source, float volume, float pitch, RandomSource random, boolean looping, int delay, Attenuation attenuation, double x, double y, double z, boolean relative) {
            super(location, source, volume, pitch, random, looping, delay, attenuation, x, y, z, relative);
        }
    }
}
