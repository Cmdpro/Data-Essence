package com.cmdpro.datanessence.block.generation.derivationspike;

import com.cmdpro.databank.model.animation.DatabankAnimationDefinition;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
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

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class EssenceDerivationSpikeBlockEntity extends BlockEntity implements EssenceBlockEntity, Overheatable {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 2000f);
    public int cooldown, temperature;
    public AnimationDefinition anim;
    public boolean isBroken, hasStructure, hasRedstone;
    final Multiblock structure = MultiblockManager.multiblocks.get(DataNEssence.locate("generators/essence_derivation_spike"));

    private final FluidTank coolantTank = new FluidTank(4000);
    public IFluidHandler getFluidHandler() {
        return coolantTank;
    }

    public DatabankAnimationState animState = new DatabankAnimationState("retract_spike")
            .addAnim(new DatabankAnimationDefinition("rotate_rings", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationDefinition("extend_spike", (state, anim) -> {}, (state, anim) -> state.setAnim("rotate_rings")))
            .addAnim(new DatabankAnimationDefinition("retract_spike", (state, anim) -> {}, (state, anim) -> {}));


    public EssenceDerivationSpikeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ESSENCE_DERIVATION_SPIKE.get(), pos, blockState);
    }

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    @Override
    public int getMaxTemperature() {
        return 1300;
    }

    @Override
    public void overheat(BlockEntity block) {
        BlockPos pos = block.getBlockPos();
        Level world = block.getLevel();
        temperature = TemperatureUtil.getBiomeTemperature(world.getBiome(pos).value());
        // angry hiss, damages everything in its AABB a LOT, shuts down.
        world.playSound(null, pos, SoundEvents.CREEPER_PRIMED, SoundSource.BLOCKS);
        world.playSound(null, pos, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS);
        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pos.getCenter().add(0, 1, 0), 11, 3, 11))) {
            if (entity.isAlive())
                entity.hurt(entity.damageSources().source(DamageTypeRegistry.essenceSiphoned), 20);
        }
        isBroken = true;
    }

    public boolean isRedstonePowered() {
        BlockPos pos = this.getBlockPos();
        hasRedstone = this.getLevel().hasNeighborSignal(pos);
        return hasRedstone;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, EssenceDerivationSpikeBlockEntity spike) {
        if (!world.isClientSide()) {
            spike.hasStructure = spike.structure.checkMultiblock(world, pos);
            if (spike.hasStructure) {

                if (spike.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) < spike.getStorage().getMaxEssence() && spike.cooldown <= 0 && !spike.isBroken && spike.isRedstonePowered()) {

                    for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pos.getCenter().add(0, 1, 0), 11, 3, 11))) {
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

                if ( spike.temperature >= spike.getMaxTemperature() )
                    spike.overheat(spike);

                //if ( world.random.nextInt() % 15 == 0)
                //    spike.temperature = Math.max(spike.temperature - 1, TemperatureUtil.getBiomeTemperature(world.getBiome(pos).value()));// todo only if coolant is present

                if ( spike.isBroken && world.random.nextInt() % 17 == 0 ) {
                    world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.75f);
                    ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, 5, 0, 0, 0, 0);
                }
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
        tag.putBoolean("Redstone", hasRedstone);
        tag.put("EssenceStorage", storage.toNbt());
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        cooldown = nbt.getInt("cooldown");
        temperature = nbt.getInt("Temp");
        isBroken = nbt.getBoolean("IsBroken");
        hasRedstone = nbt.getBoolean("Redstone");
        hasStructure = nbt.getBoolean("Multi");
    }
}
