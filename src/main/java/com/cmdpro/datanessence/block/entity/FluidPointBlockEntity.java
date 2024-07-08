package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;

public class FluidPointBlockEntity extends BaseCapabilityPointBlockEntity implements GeoBlockEntity {
    public FluidPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_POINT.get(), pos, state);
    }
    private final FluidTank fluidHandler = new FluidTank(DataNEssenceConfig.fluidPointTransfer);
    public IFluidHandler getFluidHandler() {
        return lazyFluidHandler.get();
    }
    private Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidHandler);
    @Override
    public Color linkColor() {
        return new Color(0x56bae9);
    }

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().then("animation.essence_point.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
    @Override
    public void transfer(BlockEntity other) {
        IFluidHandler resolved = level.getCapability(Capabilities.FluidHandler.BLOCK, other.getBlockPos(), getDirection());
        IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
        for (int o = 0; o < resolved2.getTanks(); o++) {
            FluidStack copy = resolved2.getFluidInTank(o).copy();
            if (!copy.isEmpty()) {
                copy.setAmount(Math.clamp(0, DataNEssenceConfig.fluidPointTransfer, copy.getAmount()));
                int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                resolved2.getFluidInTank(o).shrink(filled);
            }
        }
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("fluid", fluidHandler.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        fluidHandler.readFromNBT(nbt.getCompound("fluid"));
    }

    @Override
    public void take(BlockEntity other) {
        IFluidHandler resolved = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
        IFluidHandler resolved2 = level.getCapability(Capabilities.FluidHandler.BLOCK, other.getBlockPos(), getDirection());
        for (int o = 0; o < resolved2.getTanks(); o++) {
            FluidStack copy = resolved2.getFluidInTank(o).copy();
            if (!copy.isEmpty()) {
                copy.setAmount(Math.clamp(0, DataNEssenceConfig.fluidPointTransfer, copy.getAmount()));
                int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                resolved2.getFluidInTank(o).shrink(filled);
            }
        }
    }
}
