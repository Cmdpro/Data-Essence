package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.*;
import com.cmdpro.datanessence.fluid.Genderfluid;
import com.cmdpro.datanessence.fluid.GenderfluidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class FluidRegistry {
    public static DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, DataNEssence.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, DataNEssence.MOD_ID);
    public static final Supplier<FlowingFluid> GENDERFLUID = register("genderfluid", Genderfluid.Source::new);
    public static final Supplier<FlowingFluid> GENDERFLUID_FLOWING = register("genderfluid_flowing", Genderfluid.Flowing::new);
    public static final Supplier<FluidType> GENDERFLUID_TYPE = registerType("genderfluid", () -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.datanessence.genderfluid")
            .density(500)
            .viscosity(500)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));
    private static <T extends Fluid> Supplier<T> register(final String name, final Supplier<T> entity) {
        return FLUIDS.register(name, entity);
    }
    private static <T extends FluidType> Supplier<T> registerType(final String name, final Supplier<T> entity) {
        return FLUID_TYPES.register(name, entity);
    }
}
