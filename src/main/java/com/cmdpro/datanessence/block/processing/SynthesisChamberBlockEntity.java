package com.cmdpro.datanessence.block.processing;

import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.misc.ILockableContainer;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.moddata.LockableItemHandler;
import com.cmdpro.datanessence.recipe.*;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.SynthesisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SynthesisChamberBlockEntity extends EssenceContainer implements MenuProvider, ILockableContainer {
    private final LockableItemHandler itemHandler = new LockableItemHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public void setLockedSlots() {
            super.setLockedSlots();
            setChanged();
        }

        @Override
        public void setLocked(boolean locked) {
            super.setLocked(locked);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return super.isItemValid(slot, stack);
        }
    };
    private final ItemStackHandler dataDriveHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof DataDrive;
        }
    };
    private final ItemStackHandler outputItemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    @Override
    public float getMaxEssence() {
        return 1000;
    }
    @Override
    public float getMaxLunarEssence() {
        return 1000;
    }
    @Override
    public float getMaxNaturalEssence() {
        return 1000;
    }
    @Override
    public float getMaxExoticEssence() {
        return 1000;
    }

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
    private Lazy<IItemHandler> lazyDataDriveHandler = Lazy.of(() -> dataDriveHandler);
    private Lazy<IItemHandler> lazyOutputHandler = Lazy.of(() -> outputItemHandler);
    private Lazy<IItemHandler> lazyCombinedHandler = Lazy.of(() -> new CombinedInvWrapper(itemHandler, outputItemHandler, dataDriveHandler));

    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }
    public IItemHandler getDataDriveHandler() {
        return lazyDataDriveHandler.get();
    }
    public IItemHandler getOutputHandler() {
        return lazyOutputHandler.get();
    }
    public IItemHandler getCombinedHandler() {
        return lazyCombinedHandler.get();
    }

    public SynthesisChamberBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SYNTHESIS_CHAMBER.get(), pos, state);
        item = ItemStack.EMPTY;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        setEssence(tag.getFloat("essence"));
        setLunarEssence(tag.getFloat("lunarEssence"));
        setNaturalEssence(tag.getFloat("naturalEssence"));
        setExoticEssence(tag.getFloat("exoticEssence"));
        item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
        craftingProgress = tag.getInt("craftingProgress");
        itemHandler.deserializeNBT(pRegistries, tag.getCompound("itemHandler"));
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putInt("craftingProgress", craftingProgress);
        tag.put("itemHandler", itemHandler.serializeNBT(pRegistries));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("inventoryOutput", outputItemHandler.serializeNBT(pRegistries));
        tag.put("inventoryDrive", dataDriveHandler.serializeNBT(pRegistries));
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putInt("craftingProgress", craftingProgress);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        outputItemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryOutput"));
        dataDriveHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryDrive"));
        setEssence(nbt.getFloat("essence"));
        setLunarEssence(nbt.getFloat("lunarEssence"));
        setNaturalEssence(nbt.getFloat("naturalEssence"));
        setExoticEssence(nbt.getFloat("exoticEssence"));
        craftingProgress = nbt.getInt("craftingProgress");
    }
    public ItemStack item;
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots()+outputItemHandler.getSlots()+dataDriveHandler.getSlots());
        for (int i = 0; i < dataDriveHandler.getSlots(); i++) {
            inventory.setItem(i, dataDriveHandler.getStackInSlot(i));
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i+dataDriveHandler.getSlots(), itemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < outputItemHandler.getSlots(); i++) {
            inventory.setItem(i+dataDriveHandler.getSlots()+itemHandler.getSlots(), outputItemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public RecipeInput getCraftingInv() {
        RecipeInput inventory = new RecipeInput() {
            @Override
            public ItemStack getItem(int pIndex) {
                return itemHandler.getStackInSlot(pIndex);
            }

            @Override
            public int size() {
                return 2;
            }
        };
        return inventory;
    }
    public SynthesisRecipe recipe;
    public boolean enoughEssence;
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public int craftingProgress;
    public int workTime;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, SynthesisChamberBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity);
            BufferUtil.getItemsFromBuffersBelow(pBlockEntity);
            boolean resetWorkTime = true;
            Optional<RecipeHolder<SynthesisRecipe>> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeRegistry.SYNTHESIS_TYPE.get(), pBlockEntity.getCraftingInv(), pLevel);
            if (recipe.isPresent()) {
                if (!recipe.get().value().equals(pBlockEntity.recipe)) {
                    pBlockEntity.workTime = 0;
                }
                if (recipe.get().value().getEntry().equals(DataDrive.getEntryId(pBlockEntity.dataDriveHandler.getStackInSlot(0)))) {
                    pBlockEntity.recipe = recipe.get().value();
                    pBlockEntity.essenceCost = recipe.get().value().getEssenceCost();
                    pBlockEntity.lunarEssenceCost = recipe.get().value().getLunarEssenceCost();
                    pBlockEntity.naturalEssenceCost = recipe.get().value().getNaturalEssenceCost();
                    pBlockEntity.exoticEssenceCost = recipe.get().value().getExoticEssenceCost();
                    boolean enoughEssence = false;
                    if (pBlockEntity.getEssence() >= pBlockEntity.essenceCost &&
                            pBlockEntity.getLunarEssence() >= pBlockEntity.lunarEssenceCost &&
                            pBlockEntity.getNaturalEssence() >= pBlockEntity.naturalEssenceCost &&
                            pBlockEntity.getExoticEssence() >= pBlockEntity.exoticEssenceCost) {
                        enoughEssence = true;
                    }
                    pBlockEntity.enoughEssence = enoughEssence;
                    if (enoughEssence) {
                        ItemStack result = recipe.get().value().getResultItem(pLevel.registryAccess());
                        if (pBlockEntity.outputItemHandler.insertItem(0, result, true).isEmpty()) {
                            resetWorkTime = false;
                            pBlockEntity.workTime++;
                            if (pBlockEntity.workTime >= recipe.get().value().getTime()) {
                                pBlockEntity.setEssence(pBlockEntity.getEssence() - pBlockEntity.essenceCost);
                                pBlockEntity.setLunarEssence(pBlockEntity.getEssence() - pBlockEntity.lunarEssenceCost);
                                pBlockEntity.setNaturalEssence(pBlockEntity.getEssence() - pBlockEntity.naturalEssenceCost);
                                pBlockEntity.setExoticEssence(pBlockEntity.getEssence() - pBlockEntity.exoticEssenceCost);
                                pBlockEntity.outputItemHandler.insertItem(0, recipe.get().value().assemble(pBlockEntity.getCraftingInv(), pLevel.registryAccess()), false);
                                pBlockEntity.itemHandler.extractItem(0, 1, false);
                                pBlockEntity.itemHandler.extractItem(1, 1, false);
                                pBlockEntity.workTime = 0;
                            }
                        }
                    }
                } else {
                    pBlockEntity.recipe = null;
                }
            } else {
                pBlockEntity.recipe = null;
            }
            if (resetWorkTime) {
                pBlockEntity.workTime = -1;
            }
            pBlockEntity.updateBlock();
        } else {
            Optional<RecipeHolder<SynthesisRecipe>> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeRegistry.SYNTHESIS_TYPE.get(), pBlockEntity.getCraftingInv(), pLevel);
            recipe.ifPresentOrElse(recipeHolder -> pBlockEntity.recipe = recipeHolder.value(), () -> pBlockEntity.recipe = null);
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    private static boolean hasNotReachedStackLimit(SynthesisChamberBlockEntity entity, ItemStack toAdd) {
        if (toAdd.is(entity.outputItemHandler.getStackInSlot(0).getItem())) {
            return entity.outputItemHandler.getStackInSlot(0).getCount() + toAdd.getCount() <= entity.outputItemHandler.getStackInSlot(0).getMaxStackSize();
        } else return entity.outputItemHandler.getStackInSlot(0).isEmpty();
    }
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new SynthesisChamberMenu(pContainerId, pInventory, this);
    }

    @Override
    public List<LockableItemHandler> getLockable() {
        return List.of(itemHandler);
    }
}
