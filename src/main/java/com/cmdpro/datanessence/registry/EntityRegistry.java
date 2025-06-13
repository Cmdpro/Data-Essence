package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.*;
import com.cmdpro.datanessence.entity.ancientcombatunit.AncientCombatUnit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EntityRegistry {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DataNEssence.MOD_ID);
    public static final Supplier<EntityType<ThrownEssenceBombProjectile>> ESSENCE_BOMB = register("essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownEssenceBombProjectile>) ThrownEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "essence_bomb"));
    public static final Supplier<EntityType<ThrownLunarEssenceBombProjectile>> LUNAR_ESSENCE_BOMB = register("lunar_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownLunarEssenceBombProjectile>) ThrownLunarEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "lunar_essence_bomb"));
    public static final Supplier<EntityType<ThrownNaturalEssenceBombProjectile>> NATURAL_ESSENCE_BOMB = register("natural_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownNaturalEssenceBombProjectile>) ThrownNaturalEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "natural_essence_bomb"));
    public static final Supplier<EntityType<ThrownExoticEssenceBombProjectile>> EXOTIC_ESSENCE_BOMB = register("exotic_essence_bomb", () -> EntityType.Builder.of((EntityType.EntityFactory<ThrownExoticEssenceBombProjectile>) ThrownExoticEssenceBombProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "exotic_essence_bomb"));
    public static final Supplier<EntityType<BlackHole>> BLACK_HOLE = register("black_hole", () -> EntityType.Builder.of((EntityType.EntityFactory<BlackHole>) BlackHole::new, MobCategory.MISC).sized(4F, 4F).build(DataNEssence.MOD_ID + ":" + "black_hole"));
    public static final Supplier<EntityType<AncientSentinel>> ANCIENT_SENTINEL = register("ancient_sentinel", () -> EntityType.Builder.of((EntityType.EntityFactory<AncientSentinel>) AncientSentinel::new, MobCategory.MONSTER).sized(1.25F, 1.875F).build(DataNEssence.MOD_ID + ":" + "ancient_sentinel"));
    public static final Supplier<EntityType<AncientSentinelProjectile>> ANCIENT_SENTINEL_PROJECTILE = ENTITY_TYPES.register("ancient_sentinel_projectile", () -> EntityType.Builder.of((EntityType.EntityFactory<AncientSentinelProjectile>) AncientSentinelProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(DataNEssence.MOD_ID + ":" + "ancient_sentinel_projectile"));
    public static final Supplier<EntityType<EssenceSlashProjectile>> ESSENCE_SLASH_PROJECTILE = ENTITY_TYPES.register("essence_slash", () -> EntityType.Builder.of((EntityType.EntityFactory<EssenceSlashProjectile>) EssenceSlashProjectile::new, MobCategory.MISC).sized(1f, 1f).build(DataNEssence.MOD_ID + ":" + "essence_slash"));
    public static final Supplier<EntityType<AncientCombatUnit>> ANCIENT_COMBAT_UNIT = register("ancient_combat_unit", () -> EntityType.Builder.of((EntityType.EntityFactory<AncientCombatUnit>) AncientCombatUnit::new, MobCategory.MONSTER).sized(0.6F, 1.8F).build(DataNEssence.MOD_ID + ":" + "ancient_combat_unit"));
    public static final Supplier<EntityType<ShockwaveEntity>> SHOCKWAVE = register("shockwave", () -> EntityType.Builder.of((EntityType.EntityFactory<ShockwaveEntity>) ShockwaveEntity::new, MobCategory.MISC).sized(0F, 0F).build(DataNEssence.MOD_ID + ":" + "shockwave"));

    private static <T extends EntityType<?>> Supplier<T> register(final String name, final Supplier<T> entity) {
        return ENTITY_TYPES.register(name, entity);
    }
}
