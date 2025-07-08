package com.cmdpro.datanessence.client;

import com.cmdpro.databank.misc.SoundUtil;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.mixin.client.ChannelMixin;
import com.cmdpro.datanessence.mixin.client.SoundEngineMixin;
import com.cmdpro.datanessence.mixin.client.SoundManagerMixin;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
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
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class FactorySong {
    public static final HashMap<ResourceLocation, FactoryLoop> FACTORY_LOOPS = new HashMap<>();
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
                Minecraft.getInstance().getSoundManager().play(sound.sound);
                float time = ((float)FactorySongPointer)/20f;
                SoundUtil.setTime(sound.sound, time);
            }
            if (currentPlaying) {
                playing = true;
                float volume = sound.sound.getDynamicVolume(sound.sound.getVolume());
                SoundUtil.modifySound(sound.sound, (channel) -> channel.setVolume(volume));
            }
            sound.clearSources();
        }

        if (playing && !client.isPaused())
            FactorySongPointer++;
        if (FactorySongPointer > 612 || !playing) // 612 ticks = 30s + 600ms
            FactorySongPointer = 0;
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
            float volume = original;
            if (loop != null) {
                Vec3 eye = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                BlockPos closest = loop.getClosestSource(eye);
                if (closest == null) {
                    volume = 0;
                }
                double maxDist = 25;
                double dist = closest.getCenter().distanceTo(eye);
                volume *= (float)((25f-Math.clamp(dist, 0, maxDist))/25f);
            }
            return volume;
        }
    }
}
