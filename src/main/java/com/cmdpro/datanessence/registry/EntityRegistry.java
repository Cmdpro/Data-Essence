package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EntityRegistry {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<EntityType<ThrownEssenceBombProjectile>> ESSENCE_BOMB = register("essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownEssenceBombProjectile>) ThrownEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "essence_bomb"));
    public static final RegistryObject<EntityType<ThrownLunarEssenceBombProjectile>> LUNAR_ESSENCE_BOMB = register("lunar_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownLunarEssenceBombProjectile>) ThrownLunarEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "lunar_essence_bomb"));
    public static final RegistryObject<EntityType<ThrownNaturalEssenceBombProjectile>> NATURAL_ESSENCE_BOMB = register("natural_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownNaturalEssenceBombProjectile>) ThrownNaturalEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "natural_essence_bomb"));
    public static final RegistryObject<EntityType<ThrownExoticEssenceBombProjectile>> EXOTIC_ESSENCE_BOMB = register("exotic_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownExoticEssenceBombProjectile>) ThrownExoticEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "exotic_essence_bomb"));
    public static final RegistryObject<EntityType<BlackHole>> BLACK_HOLE = register("black_hole", () -> EntityType.Builder.of((EntityType.EntityFactory<BlackHole>) BlackHole::new, MobCategory.MISC).sized(4F, 4F).build(DataNEssence.MOD_ID + ":" + "black_hole"));

    private static <T extends EntityType<?>> RegistryObject<T> register(final String name, final Supplier<T> entity) {
        return ENTITY_TYPES.register(name, entity);
    }
}
