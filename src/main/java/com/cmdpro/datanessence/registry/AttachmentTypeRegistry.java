package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.EssenceShard;
import com.cmdpro.datanessence.item.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AttachmentTypeRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            DataNEssence.MOD_ID);
    public static final Supplier<AttachmentType<Integer>> TIER =
            register("tier", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
    public static final Supplier<AttachmentType<Optional<BlockEntity>>> LINK_FROM =
            register("link_from", () -> AttachmentType.builder(() -> Optional.ofNullable((BlockEntity)null)).build());

    public static final Supplier<AttachmentType<Boolean>> UNLOCKED_ESSENCE =
            register("unlocked_essence", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<Boolean>> UNLOCKED_LUNAR_ESSENCE =
            register("unlocked_lunar_essence", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<Boolean>> UNLOCKED_NATURAL_ESSENCE =
            register("unlocked_natural_essence", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<Boolean>> UNLOCKED_EXOTIC_ESSENCE =
            register("unlocked_exotic_essence", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<ArrayList<ResourceLocation>>> UNLOCKED =
            register("unlocked", () -> AttachmentType.builder(() -> new ArrayList<ResourceLocation>()).serialize(
                    ResourceLocation.CODEC.listOf().xmap(ArrayList::new, (a) -> a.stream().toList())).copyOnDeath().build());
    public static final Supplier<AttachmentType<ArrayList<ResourceLocation>>> INCOMPLETE =
            register("incomplete", () -> AttachmentType.builder(() -> new ArrayList<ResourceLocation>()).serialize(
                    ResourceLocation.CODEC.listOf().xmap(ArrayList::new, (a) -> a.stream().toList())).copyOnDeath().build());

    private static <T extends AttachmentType<?>> Supplier<T> register(final String name, final Supplier<T> attachment) {
        return ATTACHMENT_TYPES.register(name, attachment);
    }
}