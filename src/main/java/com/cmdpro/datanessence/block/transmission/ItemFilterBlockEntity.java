package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.node.ICustomItemPointBehaviour;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.screen.ItemFilterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.IntegerRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ItemFilterBlockEntity extends BlockEntity implements MenuProvider, ICustomItemPointBehaviour {
    @Override
    public boolean canExtractItem(IItemHandler handler, IItemHandler other) {
        return false;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            boolean isValid = false;
            for (int i = 9*slot; i < (9*(slot+1))-1; i++) {
                ItemStack stack2 = filterHandler.getStackInSlot(i);
                if (filterMatches(stack, stack2)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };

    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots()+filterHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < filterHandler.getSlots(); i++) {
            inventory.setItem(i+itemHandler.getSlots(), filterHandler.getStackInSlot(i));
        }
        return inventory;
    }

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
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
            int index = direction.getValue().getMinimum()/9;
            BlockPos offset = getBlockPos().relative(direction.getKey());
            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, offset, direction.getKey().getOpposite());
            if (itemHandler != null) {
                boolean movedAnything = false;
                ItemStack copy = this.itemHandler.getStackInSlot(index);
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
                        this.itemHandler.extractItem(index, copy2.getCount() - copy.getCount(), false);
                        break;
                    }
                }
            }
        }
    }
    public static boolean filterMatches(ItemStack stack, ItemStack stack2) {
        if (stack.isEmpty() || stack2.isEmpty()) {
            return false;
        }
        return ItemStack.isSameItem(stack, stack2);
    }
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new ItemFilterMenu(pContainerId, pInventory, this);
    }
}
