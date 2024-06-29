package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
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

public class ItemPointBlockEntity extends BaseCapabilityPointBlockEntity implements GeoBlockEntity {
    public ItemPointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ITEM_POINT.get(), pos, state);
    }
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().then("animation.essence_point.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    public Color linkColor() {
        return new Color(0xef4d3d);
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
        other.getCapability(ForgeCapabilities.ITEM_HANDLER, getDirection()).ifPresent((resolved) -> {
            getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((resolved2) -> {
                boolean movedAnything = false;
                for (int o = 0; o < resolved2.getSlots(); o++) {
                    ItemStack copy = resolved2.getStackInSlot(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setCount(Math.clamp(0, DataNEssenceConfig.itemPointTransfer, copy.getCount()));
                        ItemStack copy2 = copy.copy();
                        int p = 0;
                        while (p < resolved.getSlots()) {
                            ItemStack copyCopy = copy.copy();
                            int remove = resolved.insertItem(p, copyCopy, false).getCount();
                            if (remove < copyCopy.getCount()) {
                                movedAnything = true;
                            }
                            copy.setCount(remove);
                            if (remove <= 0) {
                                break;
                            }
                            p++;
                        }
                        if (movedAnything) {
                            resolved2.extractItem(o, copy2.getCount()-copy.getCount(), false);
                            break;
                        }
                    }
                }
            });
        });
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public void drops() {
        SimpleContainer inventory = getInv();
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public void take(BlockEntity other) {
        getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((resolved) -> {
            other.getCapability(ForgeCapabilities.ITEM_HANDLER, getDirection()).ifPresent((resolved2) -> {
                boolean movedAnything = false;
                for (int o = 0; o < resolved2.getSlots(); o++) {
                    ItemStack copy = resolved2.getStackInSlot(o).copy();
                    if (!copy.isEmpty()) {
                        copy.setCount(Math.clamp(0, DataNEssenceConfig.itemPointTransfer, copy.getCount()));
                        ItemStack copy2 = copy.copy();
                        int p = 0;
                        while (p < resolved.getSlots()) {
                            ItemStack copyCopy = copy.copy();
                            int remove = resolved.insertItem(p, copyCopy, false).getCount();
                            if (remove < copyCopy.getCount()) {
                                movedAnything = true;
                            }
                            copy.setCount(remove);
                            if (remove <= 0) {
                                break;
                            }
                            p++;
                        }
                        if (movedAnything) {
                            resolved2.extractItem(o, copy2.getCount()-copy.getCount(), false);
                            break;
                        }
                    }
                }
            });
        });
    }
}
