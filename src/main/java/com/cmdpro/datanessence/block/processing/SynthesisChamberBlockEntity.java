package com.cmdpro.datanessence.block.processing;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.misc.ILockableContainer;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.recipe.*;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.SynthesisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SynthesisChamberBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity, ILockableContainer {
    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private final LockableItemHandler itemHandler = new LockableItemHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            checkRecipes();
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
            checkRecipes();
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

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }
    public IItemHandler getDataDriveHandler() {
        return dataDriveHandler;
    }
    public IItemHandler getOutputHandler() {
        return outputItemHandler;
    }
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler, dataDriveHandler, outputItemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
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
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
        workTime = tag.getInt("workTime");
        itemHandler.deserializeNBT(pRegistries, tag.getCompound("itemHandler"));
        maxWorkTime = tag.getInt("maxWorkTime");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putInt("workTime", workTime);
        tag.putInt("maxWorkTime", recipe == null ? -1 : recipe.getTime());
        tag.put("itemHandler", itemHandler.serializeNBT(pRegistries));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("inventoryOutput", outputItemHandler.serializeNBT(pRegistries));
        tag.put("inventoryDrive", dataDriveHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("workTime", workTime);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        outputItemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryOutput"));
        dataDriveHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryDrive"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        workTime = nbt.getInt("workTime");
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
    public void checkRecipes() {

        Optional<RecipeHolder<SynthesisRecipe>> recipe = level.getRecipeManager().getRecipesFor(RecipeRegistry.SYNTHESIS_TYPE.get(), getCraftingInv(), level).stream().filter((a) -> a.value().getEntry().equals(DataDrive.getEntryId(dataDriveHandler.getStackInSlot(0)))).findFirst();
        if (recipe.isPresent()) {
            if (!recipe.get().value().equals(this.recipe)) {
                workTime = 0;
            }
            if (dataDriveHandler.getStackInSlot(0).has(DataComponentRegistry.DATA_ID) && dataDriveHandler.getStackInSlot(0).has(DataComponentRegistry.DATA_INCOMPLETE)) {
                if (recipe.get().value().getEntry().equals(DataDrive.getEntryId(dataDriveHandler.getStackInSlot(0))) && DataDrive.getEntryCompletionStage(dataDriveHandler.getStackInSlot(0)) >= recipe.get().value().getCompletionStage()) {
                    this.recipe = recipe.get().value();
                    essenceCost = recipe.get().value().getEssenceCost();
                } else {
                    this.recipe = null;
                }
            } else {
                this.recipe = null;
            }
        } else {
            this.recipe = null;
        }
    }
    public SynthesisRecipe recipe;
    public boolean enoughEssence;
    public Map<ResourceLocation, Float> essenceCost;
    public int workTime;
    public int maxWorkTime;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, SynthesisChamberBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()));

            for ( var lockedSlot : pBlockEntity.getLockable() ) {
                if (!lockedSlot.locked)
                    return;
            }

            BufferUtil.getItemsFromBuffersBelow(pBlockEntity, pBlockEntity.itemHandler);

            boolean resetWorkTime = true;
            if (pBlockEntity.recipe != null) {
                boolean enoughEssence = true;
                for (Map.Entry<ResourceLocation, Float> i : pBlockEntity.essenceCost.entrySet()) {
                    EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                    if (pBlockEntity.storage.getEssence(type) < i.getValue()/pBlockEntity.recipe.getTime()) {
                        enoughEssence = false;
                    }
                }
                pBlockEntity.enoughEssence = enoughEssence;
                if (enoughEssence) {
                    ItemStack result = pBlockEntity.recipe.getResultItem(pLevel.registryAccess());
                    if (pBlockEntity.outputItemHandler.insertItem(0, result, true).isEmpty()) {
                        resetWorkTime = false;
                        pBlockEntity.workTime++;
                        for (Map.Entry<ResourceLocation, Float> i : pBlockEntity.essenceCost.entrySet()) {
                            EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                            pBlockEntity.storage.removeEssence(type, i.getValue()/pBlockEntity.recipe.getTime());
                        }
                        if (pBlockEntity.workTime >= pBlockEntity.recipe.getTime()) {
                            pBlockEntity.outputItemHandler.insertItem(0, pBlockEntity.recipe.assemble(pBlockEntity.getCraftingInv(), pLevel.registryAccess()), false);
                            pBlockEntity.itemHandler.extractItem(0, 1, false);
                            pBlockEntity.itemHandler.extractItem(1, 1, false);
                            pBlockEntity.workTime = 0;
                        }
                    }
                }
            }
            if (resetWorkTime) {
                pBlockEntity.workTime = -1;
            }
            pBlockEntity.updateBlock();
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            checkRecipes();
        }
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
