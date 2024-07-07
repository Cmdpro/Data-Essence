package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.EssenceShard;
import com.cmdpro.datanessence.item.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypeRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            DataNEssence.MOD_ID);
    public static final Supplier<SimpleParticleType> ESSENCE_SPARKLE =
            register("essence_sparkle", () -> new SimpleParticleType(true));

    private static <T extends AttachmentType<?>> Supplier<T> register(final String name, final Supplier<T> particle) {
        return ATTACHMENT_TYPES.register(name, particle);
    }
}