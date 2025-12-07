package com.cmdpro.datanessence.block.processing;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.block.Machine;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.misc.ILockableContainer;
import com.cmdpro.datanessence.client.FactorySong;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.IHasRequiredKnowledge;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.screen.AutoFabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
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

import java.util.*;

public class AutoFabricatorBlockEntity extends BlockEntity implements MenuProvider, ILockableContainer, EssenceBlockEntity, Machine {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("crafting", (state, anim) -> {}, (state, anim) -> {}));
    private final LockableItemHandler itemHandler = new LockableItemHandler(9) {
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


    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
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

    public AutoFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.AUTO_FABRICATOR.get(), pos, state);
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
        craftingProgress = tag.getInt("craftingProgress");
        itemHandler.deserializeNBT(pRegistries, tag.getCompound("itemHandler"));
        maxTime = tag.getInt("maxTime");
        if (tag.contains("essenceCost")) {
            CompoundTag cost = tag.getCompound("essenceCost");
            essenceCost = new HashMap<>();
            for (String i : cost.getAllKeys()) {
                ResourceLocation location = ResourceLocation.tryParse(i);
                essenceCost.put(location, cost.getFloat(i));
            }
        } else {
            essenceCost = null;
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putInt("craftingProgress", craftingProgress);
        tag.put("itemHandler", itemHandler.serializeNBT(pRegistries));
        tag.putInt("maxTime", maxTime);
        if (essenceCost != null) {
            CompoundTag cost = new CompoundTag();
            for (Map.Entry<ResourceLocation, Float> i : essenceCost.entrySet()) {
                cost.putFloat(i.getKey().toString(), i.getValue());
            }
            tag.put("essenceCost", cost);
        }
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("inventoryOutput", outputItemHandler.serializeNBT(pRegistries));
        tag.put("inventoryDrive", dataDriveHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("craftingProgress", craftingProgress);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        outputItemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryOutput"));
        dataDriveHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryDrive"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
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
    public CraftingContainer getCraftingInv() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            items.add(itemHandler.getStackInSlot(i));
        }
        CraftingContainer inventory = new NonMenuCraftingContainer(items, 3, 3);
        return inventory;
    }
    public boolean tryCraft() {
        if (recipe instanceof IHasRequiredKnowledge recipe) {
            if (dataDriveHandler.getStackInSlot(0).has(DataComponentRegistry.DATA_ID) && dataDriveHandler.getStackInSlot(0).has(DataComponentRegistry.DATA_INCOMPLETE)) {
                if (recipe.getEntry().equals(DataDrive.getEntryId(dataDriveHandler.getStackInSlot(0)))) {
                    if (DataDrive.getEntryCompletionStage(dataDriveHandler.getStackInSlot(0)) < recipe.getCompletionStage()) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        CraftingInput craftingInput = getCraftingInv().asCraftInput();
        ItemStack stack = recipe.assemble(craftingInput, level.registryAccess()).copy();
        if (!recipe.matches(craftingInput, level)) {
            return false;
        }
        NonNullList<ItemStack> remaining = recipe.getRemainingItems(craftingInput);
        int left = getCraftingInv().asPositionedCraftInput().left();
        int top = getCraftingInv().asPositionedCraftInput().top();
        int craftWidth = 3;
        for (int k = 0; k < craftingInput.height(); k++) {
            for (int l = 0; l < craftingInput.width(); l++) {
                int i1 = l + left + (k + top) * craftWidth;
                ItemStack existingItem = itemHandler.getStackInSlot(i1);
                ItemStack remainderItem = remaining.get(l + k * craftingInput.width());
                if (!existingItem.isEmpty()) {
                    itemHandler.extractItem(i1, 1, false);
                    existingItem = itemHandler.getStackInSlot(i1); // we re-check this slot after crafting
                }

                if (!existingItem.isEmpty()) {
                    ItemEntity entity = new ItemEntity(level, (float) getBlockPos().getX() + 0.5f, (float) getBlockPos().getY() + 1f, (float) getBlockPos().getZ() + 0.5f, remainderItem);
                    level.addFreshEntity(entity);
                } else {
                    itemHandler.insertItem(i1, remainderItem, false);
                }
            }
        }
        outputItemHandler.insertItem(0, stack, false);
        level.playSound(null, worldPosition, SoundRegistry.AUTO_FABRICATOR_CRAFT.value(), SoundSource.BLOCKS, 2, 1);
        return true;
    }
    public Recipe<CraftingInput> recipe;
    public boolean enoughEssence;
    public Map<ResourceLocation, Float> essenceCost;
    public int craftingProgress;
    public int maxTime;
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(
            RecipeType<T> type, Level level, I input
    ) {
        return level.getRecipeManager().getRecipesFor(type, input, level).stream().filter(a -> {
            if (a.value() instanceof IHasRequiredKnowledge recipe) {
                return recipe.getEntry().equals(DataDrive.getEntryId(dataDriveHandler.getStackInSlot(0)));
            }
            return true;
        }).findFirst();
    }
    public void checkRecipes() {
        Optional<RecipeHolder<IFabricationRecipe>> fabricationRecipe = getRecipeFor(RecipeRegistry.FABRICATIONCRAFTING.get(), level, getCraftingInv().asCraftInput());
        Recipe<CraftingInput> recipe = null;
        if (fabricationRecipe.isEmpty()) {
            Optional<RecipeHolder<CraftingRecipe>> vanillaRecipe = getRecipeFor(RecipeType.CRAFTING, level, getCraftingInv().asCraftInput());
            if (vanillaRecipe.isPresent()) {
                recipe = vanillaRecipe.get().value();
            }
        } else {
            recipe = fabricationRecipe.get().value();
        }
        if (recipe != null) {
            this.recipe = recipe;
            if (recipe instanceof IFabricationRecipe recipe2) {
                essenceCost = recipe2.getEssenceCost();
                maxTime = recipe2.getTime();
            } else {
                essenceCost = new HashMap<>();
                maxTime = 20;
            }
            item = recipe.getResultItem(level.registryAccess());
        } else {
            this.recipe = null;
            essenceCost = null;
            item = ItemStack.EMPTY;
            maxTime = 20;
        }
    }
    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            checkRecipes();
        }
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AutoFabricatorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()));

            for ( var lockedSlot : pBlockEntity.getLockable() ) {
                if (!lockedSlot.locked)
                    return;
            }

            BufferUtil.getItemsFromBuffersBelow(pBlockEntity, pBlockEntity.itemHandler);

            if (pBlockEntity.recipe != null && hasNotReachedStackLimit(pBlockEntity, pBlockEntity.recipe.getResultItem(pLevel.registryAccess()))) {
                boolean enoughEssence = true;
                for (Map.Entry<ResourceLocation, Float> i : pBlockEntity.essenceCost.entrySet()) {
                    EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                    if (pBlockEntity.storage.getEssence(type) < i.getValue()) {
                        enoughEssence = false;
                    }
                }
                pBlockEntity.enoughEssence = enoughEssence;
                if (pBlockEntity.recipe.matches(pBlockEntity.getCraftingInv().asCraftInput(), pBlockEntity.level)) {
                    pBlockEntity.craftingProgress++;
                    for (Map.Entry<ResourceLocation, Float> i : pBlockEntity.essenceCost.entrySet()) {
                        EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                        pBlockEntity.storage.removeEssence(type, i.getValue()/pBlockEntity.maxTime);
                    }
                } else {
                    pBlockEntity.craftingProgress = -1;
                }
            } else {
                pBlockEntity.craftingProgress = -1;
            }
            if (pBlockEntity.craftingProgress >= pBlockEntity.maxTime) {
                pBlockEntity.tryCraft();
                pBlockEntity.craftingProgress = 0;
            }
            pBlockEntity.updateBlock();
        } else {
            if (pBlockEntity.craftingProgress >= 0 && pBlockEntity.essenceCost != null) {
                if ( pBlockEntity.essenceCost.containsKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get())) )
                    ClientHandler.markIndustrialFactorySong(pPos);
            }
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    private static boolean hasNotReachedStackLimit(AutoFabricatorBlockEntity entity, ItemStack toAdd) {
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
        return new AutoFabricatorMenu(pContainerId, pInventory, this);
    }

    @Override
    public List<LockableItemHandler> getLockable() {
        return List.of(itemHandler);
    }

    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private static class ClientHandler {
        static FactorySong.FactoryLoop industrialSound = FactorySong.getLoop(SoundRegistry.AUTO_FABRICATOR_LOOP_INDUSTRIAL.value());

        public static void markIndustrialFactorySong(BlockPos pos) {
            industrialSound.addSource(pos);
        }
    }

    @Override
    public List<Direction> getValidInputDirections() {
        return List.of(Direction.DOWN, Direction.UP);
    }
}
