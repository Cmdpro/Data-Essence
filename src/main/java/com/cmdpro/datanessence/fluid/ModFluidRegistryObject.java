package com.cmdpro.datanessence.fluid;

import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.FluidRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFluidRegistryObject {
    public final String name;
    public DeferredHolder<Fluid, ? extends FlowingFluid> source;
    public DeferredHolder<Fluid, ? extends FlowingFluid> flowing;
    public DeferredHolder<FluidType, ?> type;
    public DeferredHolder<Block, ? extends LiquidBlock> block;
    public DeferredHolder<Item, ?> bucket;
    public ModFluidRegistryObject(String name, DeferredHolder<Fluid, ? extends FlowingFluid> source, DeferredHolder<Fluid, ? extends FlowingFluid> flowing, DeferredHolder<FluidType, ?> type, DeferredHolder<Block, ? extends LiquidBlock> block, DeferredHolder<Item, ?> bucket) {
        this.name = name;
        this.source = source;
        this.flowing = flowing;
        this.type = type;
        this.block = block;
        this.bucket = bucket;
    }
    public static ModFluidRegistryObject register(String name, ModFluidType type) {
        return register(name, type, FluidRegistry.FLUIDS, FluidRegistry.FLUID_TYPES, BlockRegistry.BLOCKS, ItemRegistry.ITEMS);
    }
    public static ModFluidRegistryObject register(String name, ModFluidType type, DeferredRegister<Fluid> fluidRegistry, DeferredRegister<FluidType> fluidTypeRegistry, DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry) {
        ModFluidRegistryObject obj = new ModFluidRegistryObject(name, null, null, null, null, null);

        Supplier<FlowingFluid> source = () -> type.createSourceFluid(obj);
        Supplier<FlowingFluid> flowing = () -> type.createFlowingFluid(obj);
        Supplier<FluidType> fluidType = () -> type.createFluidType(obj);
        Supplier<LiquidBlock> block = () -> type.createBlock(obj);
        Supplier<Item> bucket = type.hasBucket() ? () -> type.createBucket(obj) : null;

        obj.source = fluidRegistry.register(name + "_source", source);
        obj.flowing = fluidRegistry.register(name + "_flowing", flowing);
        obj.type = fluidTypeRegistry.register(name, fluidType);
        obj.block = blockRegistry.register(name, block);

        DeferredHolder<Item, ?> bucketHolder = null;
        if (bucket != null) {
            bucketHolder = itemRegistry.register(name + "_bucket", bucket);
        }
        obj.bucket = bucketHolder;

        return obj;
    }
}
