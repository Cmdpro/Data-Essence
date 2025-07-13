package com.cmdpro.datanessence.mixin.client;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Channel.class)
public interface ChannelMixin {
    @Accessor("source")
    public int getSource();
}
