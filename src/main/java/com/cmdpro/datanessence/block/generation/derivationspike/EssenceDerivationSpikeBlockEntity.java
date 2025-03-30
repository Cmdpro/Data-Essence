package com.cmdpro.datanessence.block.generation.derivationspike;

import com.cmdpro.databank.multiblock.Multiblock;
import com.cmdpro.databank.multiblock.MultiblockManager;
import com.cmdpro.databank.temperature.TemperatureUtil;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.block.Overheatable;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class EssenceDerivationSpikeBlockEntity extends BlockEntity implements EssenceBlockEntity, Overheatable {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 2000f);
    public int cooldown;
    public int temperature;
    public AnimationState animState = new AnimationState();
    public boolean isBroken;
    public boolean hasStructure;
    final Multiblock structure = MultiblockManager.multiblocks.get(DataNEssence.locate("generators/essence_derivation_spike"));

    private final FluidTank coolantTank = new FluidTank(4000);
    public IFluidHandler getFluidHandler() {
        return coolantTank;
    }

    public EssenceDerivationSpikeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ESSENCE_DERIVATION_SPIKE.get(), pos, blockState);
    }

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    @Override
    public Ingredient canBeRepairedWith() {
        return null;
    }

    @Override
    public int getMaxTemperature() {
        return 1300;
    }

    @Override
    public void overheat(BlockEntity block) {
        // angry hiss, damages everything in its AABB a LOT, shuts down.
    }

    public static void tick(Level world, BlockPos pos, BlockState state, EssenceDerivationSpikeBlockEntity spike) {
        if (!world.isClientSide()) {
            spike.hasStructure = spike.structure.checkMultiblock(world, pos);
            if (spike.hasStructure) {

                if (spike.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) < spike.getStorage().getMaxEssence() && spike.cooldown == 0) {

                    for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pos.getCenter().add(0, 2, 0), 11, 3, 11))) {
                        if (entity.isAlive())
                            entity.hurt(entity.damageSources().source(DamageTypeRegistry.essenceSiphoned), 10);
                        if (!entity.isAlive()) {
                            spike.getStorage().addEssence(EssenceTypeRegistry.ESSENCE.get(), Math.clamp(entity.getMaxHealth() * 4, 0f, 100f));
                            spike.temperature += 5;
                        }
                    }
                    spike.cooldown = 20;
                }
                spike.cooldown--;
                spike.temperature = Math.max(spike.temperature - 1, TemperatureUtil.ABSOLUTE_ZERO);// todo only if coolant is present
                spike.updateBlock();
            }
        }
    }

    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("cooldown", cooldown);
        tag.putInt("Temp", temperature);
        tag.putBoolean("IsBroken", isBroken);
        tag.putBoolean("Multi", hasStructure);
        tag.put("EssenceStorage", storage.toNbt());
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        cooldown = nbt.getInt("cooldown");
        temperature = nbt.getInt("Temp");
        isBroken = nbt.getBoolean("IsBroken");
        hasStructure = nbt.getBoolean("Multi");
    }
}
