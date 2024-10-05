package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.block.production.FluidCollector;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.IntegerRange;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Map;

public class ItemFilterBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            boolean isValid = false;
            for (int i = 0; i < filterHandler.getSlots(); i++) {
                ItemStack stack2 = filterHandler.getStackInSlot(i);
                if (ItemStack.matches(stack, stack2)) {
                    isValid = true;
                }
            }
            if (!isValid) {
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };
    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
    private final ItemStackHandler filterHandler = new ItemStackHandler(9*6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public IItemHandler getFilterHandler() {
        return lazyFilterHandler.get();
    }
    private Lazy<IItemHandler> lazyFilterHandler = Lazy.of(() -> filterHandler);
    public ItemFilterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ITEM_FILTER.get(), pos, state);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("filters", filterHandler.serializeNBT(pRegistries));
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        filterHandler.deserializeNBT(pRegistries, nbt.getCompound("filters"));
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ItemFilterBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            pBlockEntity.tickInventory();
        }
    }
    public void tickInventory() {
        for (Map.Entry<Direction, IntegerRange> direction : ItemFilter.DIRECTIONS.entrySet()) {
            BlockPos offset = getBlockPos().relative(direction.getKey());
            boolean canInsert = false;
            for (int i = direction.getValue().getMinimum(); i <= direction.getValue().getMaximum(); i++) {
                if (filterMatches(this.itemHandler.getStackInSlot(0), this.filterHandler.getStackInSlot(i))) {
                    canInsert = true;
                    break;
                }
            }
            if (canInsert) {
                IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, offset, direction.getKey().getOpposite());
                if (itemHandler != null) {
                    boolean movedAnything = false;
                    for (int o = 0; o < this.itemHandler.getSlots(); o++) {
                        ItemStack copy = this.itemHandler.getStackInSlot(o).copy();
                        if (!copy.isEmpty()) {
                            ItemStack copy2 = copy.copy();
                            int p = 0;
                            while (p < itemHandler.getSlots()) {
                                ItemStack copyCopy = copy.copy();
                                int remove = itemHandler.insertItem(p, copyCopy, false).getCount();
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
                                this.itemHandler.extractItem(o, copy2.getCount() - copy.getCount(), false);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public static boolean filterMatches(ItemStack stack, ItemStack stack2) {
        return stack.getItem() == stack2.getItem();
    }
}
