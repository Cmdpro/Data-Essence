package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.damagesource.IReductionFunction;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = DataNEssence.MOD_ID)
public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> magicProjectile = damageType("magic_projectile");
    public static final ResourceKey<DamageType> laser = damageType("laser");
    public static final ResourceKey<DamageType> blackHole = damageType("black_hole");
    public static final ResourceKey<DamageType> essenceSiphoned = damageType("essence_siphoned");
    public static final ResourceKey<DamageType> crushed = damageType("crushed");
    public static final ResourceKey<DamageType> overheatFailure = damageType("overheat_failure");
    public static final ResourceKey<DamageType> ancientProjectile = damageType("ancient_projectile");

    private static ResourceKey<DamageType> damageType(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, DataNEssence.locate(name));
    }


    @SubscribeEvent
    public static void onLivingEntityTick(LivingDamageEvent.Pre event) {
        if (event.getSource().is(ancientProjectile)) {
            event.setNewDamage(event.getContainer().getOriginalDamage());
        }
    }
    @SubscribeEvent
    public static void onBlock(LivingShieldBlockEvent event) {
        if (event.getDamageSource().is(ancientProjectile)) {
            event.setBlocked(false);
        }
    }
}
