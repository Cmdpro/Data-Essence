package com.cmdpro.datanessence.client;

import com.cmdpro.databank.misc.SoundUtil;
import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class FactorySong {
    public static final HashMap<ResourceLocation, FactoryLoop> FACTORY_LOOPS = new HashMap<>();
    private static float lastBlockVolume = 1.0f;

    public static void addFactorySongSound(ResourceLocation id) {
        FACTORY_LOOPS.put(id, new FactorySong.FactoryLoop(new FactorySong.FactorySoundInstance(id, SoundSource.BLOCKS, 0.5f, 1.0f, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true)));
    }

    public static FactoryLoop getLoop(SoundEvent event) {
        return FACTORY_LOOPS.get(event.getLocation());
    }

    public static int FactorySongPointer;

    @SubscribeEvent
    public static void handleFactorySong(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        SoundManager soundManager = client.getSoundManager();
        boolean playing = false;

        List<SoundInstance> toSync = new ArrayList<>();

        float currentVolume = client.options.getSoundSourceVolume(SoundSource.BLOCKS);
        boolean volumeJustCameBack = lastBlockVolume == 0f && currentVolume > 0f;
        lastBlockVolume = currentVolume;

        if (currentVolume > 0f || volumeJustCameBack) {
            for (var sound : FactorySong.FACTORY_LOOPS.values()) {
                boolean currentPlaying = false;

                if (soundManager.isActive(sound.sound)) {
                    if (sound.getSourceCount() > 0) {
                        currentPlaying = true;
                    } else {
                        soundManager.stop(sound.sound);
                    }
                } else if (sound.getSourceCount() > 0) {
                    currentPlaying = true;
                    soundManager.play(sound.sound);
                    toSync.add(sound.sound);
                }

                if (currentPlaying) {
                    playing = true;

                    float volume = sound.sound.getDynamicVolume(sound.sound.getVolume());

                    if (SoundUtil.getChannelHandle(sound.sound) == null) {
                        soundManager.play(sound.sound);
                    } else {
                        SoundUtil.modifySound(sound.sound, (channel) -> channel.setVolume(volume));
                    }
                }

                sound.clearSources();
            }

            if (playing && !client.isPaused()) {
                FactorySongPointer++;
            }

            if (FactorySongPointer > 612 || !playing) {
                FactorySongPointer = 0;
            }

            for (SoundInstance i : toSync) {
                float time = ((float) FactorySongPointer) / 20f;
                SoundUtil.setTime(i, time);
            }
        }
    }

    public static class FactoryLoop {
        public FactorySoundInstance sound;
        private final List<BlockPos> sources = new ArrayList<>();

        public FactoryLoop(FactorySoundInstance sound) {
            this.sound = sound;
            this.sound.loop = this;
        }

        public void clearSources() {
            sources.clear();
        }

        public void addSource(BlockPos pos) {
            if (!sources.contains(pos)) {
                sources.add(pos);
            }
        }

        public BlockPos getClosestSource(Vec3 pos) {
            double closestDist = Double.MAX_VALUE;
            BlockPos closest = null;
            for (BlockPos i : sources) {
                double dist = i.getCenter().distanceTo(pos);
                if (dist < closestDist) {
                    closest = i;
                    closestDist = dist;
                }
            }
            return closest;
        }

        public int getSourceCount() {
            return sources.size();
        }
    }

    public static class FactorySoundInstance extends SimpleSoundInstance {
        public FactoryLoop loop;

        public FactorySoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, BlockPos entity) {
            super(soundEvent, source, volume, pitch, random, entity);
        }

        public FactorySoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, double x, double y, double z) {
            super(soundEvent, source, volume, pitch, random, x, y, z);
        }

        public FactorySoundInstance(ResourceLocation location, SoundSource source, float volume, float pitch, RandomSource random, boolean looping, int delay, Attenuation attenuation, double x, double y, double z, boolean relative) {
            super(location, source, volume, pitch, random, looping, delay, attenuation, x, y, z, relative);
        }

        public float getDynamicVolume(float original) {
            if (original <= 0f) return 0f;
            float blockVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.BLOCKS);
            if (blockVolume <= 0f) return 0f;

            float volume = original;

            if (loop != null) {
                Vec3 eye = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                if (eye == null) return 0f;

                BlockPos closest = loop.getClosestSource(eye);
                if (closest == null) return 0f;

                Vec3 center = closest.getCenter();
                if (center == null) return 0f;

                double maxDist = 25;
                double dist = center.distanceTo(eye);
                double clampedDist = Math.max(0, Math.min(maxDist, dist));
                volume *= (float)((maxDist - clampedDist) / maxDist);
            }

            return volume;
        }
    }
}
