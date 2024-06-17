package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
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
        super(BlockEntityInit.FLUID_POINT.get(), pos, state);
    }
    private final FluidTank fluidHandler = new FluidTank(DataNEssenceConfig.fluidPointTransfer);
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> fluidHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public Color linkColor() {
        return new Color(0x56bae9);
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
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
        other.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((resolved) -> {
            getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((resolved2) -> {
                for (int o = 0; o < resolved2.getTanks(); o++) {
                    FluidStack copy = resolved2.getFluidInTank(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setAmount(Math.clamp(0, DataNEssenceConfig.fluidPointTransfer, copy.getAmount()));
                        int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        resolved2.getFluidInTank(o).shrink(filled);
                    }
                }
            });
        });
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
        getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((resolved) -> {
            other.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((resolved2) -> {
                for (int o = 0; o < resolved2.getTanks(); o++) {
                    FluidStack copy = resolved2.getFluidInTank(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setAmount(Math.clamp(0, DataNEssenceConfig.fluidPointTransfer, copy.getAmount()));
                        int filled = resolved.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        resolved2.getFluidInTank(o).shrink(filled);
                    }
                }
            });
        });
    }
}
