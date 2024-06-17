package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.ThrownEssenceBombProjectile;
import com.cmdpro.datanessence.entity.ThrownExoticEssenceBombProjectile;
import com.cmdpro.datanessence.entity.ThrownLunarEssenceBombProjectile;
import com.cmdpro.datanessence.entity.ThrownNaturalEssenceBombProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EntityInit {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<EntityType<ThrownEssenceBombProjectile>> ESSENCE_BOMB = ENTITY_TYPES.register("essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownEssenceBombProjectile>) ThrownEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "essencebomb"));
    public static final RegistryObject<EntityType<ThrownLunarEssenceBombProjectile>> LUNAR_ESSENCE_BOMB = ENTITY_TYPES.register("lunar_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownLunarEssenceBombProjectile>) ThrownLunarEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "lunaressencebomb"));
    public static final RegistryObject<EntityType<ThrownNaturalEssenceBombProjectile>> NATURAL_ESSENCE_BOMB = ENTITY_TYPES.register("natural_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownNaturalEssenceBombProjectile>) ThrownNaturalEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "naturalessencebomb"));
    public static final RegistryObject<EntityType<ThrownExoticEssenceBombProjectile>> EXOTIC_ESSENCE_BOMB = ENTITY_TYPES.register("exotic_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownExoticEssenceBombProjectile>) ThrownExoticEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "exoticessencebomb"));

    private static <T extends EntityType<?>> RegistryObject<T> register(final String name, final Supplier<T> entity) {
        return ENTITY_TYPES.register(name, entity);
    }
}
