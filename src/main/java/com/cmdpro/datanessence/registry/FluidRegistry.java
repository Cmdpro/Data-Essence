package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.fluid.CooledWater;
import com.cmdpro.datanessence.fluid.Genderfluid;
import com.cmdpro.datanessence.fluid.ModFluidRegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class FluidRegistry {
    public static DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, DataNEssence.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, DataNEssence.MOD_ID);

    public static final ModFluidRegistryObject GENDERFLUID = ModFluidRegistryObject.register("genderfluid", new Genderfluid());
    public static final ModFluidRegistryObject COOLED_WATER = ModFluidRegistryObject.register("cooled_water", new CooledWater());

    private static <T extends Fluid> Supplier<T> register(final String name, final Supplier<T> entity) {
        return FLUIDS.register(name, entity);
    }
    private static <T extends FluidType> Supplier<T> registerType(final String name, final Supplier<T> entity) {
        return FLUID_TYPES.register(name, entity);
    }
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(Genderfluid.GenderfluidFluid.EXTENSIONS, FluidRegistry.GENDERFLUID.type.get());
    }
}
