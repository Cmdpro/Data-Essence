package com.cmdpro.datanessence.api.block;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.block.processing.FabricatorBlockEntity;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseFabricatorBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public MultiEssenceContainer storage = new MultiEssenceContainer(getSupportedEssenceTypes(), getMaxEssence());
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            checkRecipes();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 9) {
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };
    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            checkRecipes();
        }
    }
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }
    public IItemHandler getItemHandler() {
        return itemHandler;
    }
    public void checkRecipes() {
        Optional<RecipeHolder<IFabricationRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.FABRICATIONCRAFTING.get(), getCraftingInv().asCraftInput(), level);
        if (recipe.isPresent()) {
            this.recipe = recipe.get().value();
            essenceCost = recipe.get().value().getEssenceCost();
            item = recipe.get().value().getResultItem(level.registryAccess());
            maxTime = recipe.get().value().getTime();
        } else {
            Optional<RecipeHolder<CraftingRecipe>> recipe2 = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, getCraftingInv().asCraftInput(), level);
            if (recipe2.isPresent()) {
                this.recipe = recipe2.get().value();
                item = recipe2.get().value().getResultItem(level.registryAccess());
                essenceCost = null;
            } else {
                this.recipe = null;
                essenceCost = null;
                item = ItemStack.EMPTY;
            }
            maxTime = 20;
        }
    }
    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public abstract List<EssenceType> getSupportedEssenceTypes();
    public abstract float getMaxEssence();

    public BaseFabricatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        time = tag.getInt("time");
        maxTime = tag.getInt("maxTime");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putInt("time", time);
        tag.putInt("maxTime", maxTime);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("time", time);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        time = nbt.getInt("time");
    }
    public ItemStack item;
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
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
    public static InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!pLevel.isClientSide) {
            if (blockEntity instanceof BaseFabricatorBlockEntity ent) {
                if (ent.time >= 0) {
                    ent.time = -1;
                } else {
                    if (ent.recipe != null) {
                        if (ent.enoughEssence) {
                            IFabricationRecipe fabricationRecipe = null;
                            if (ent.recipe instanceof IFabricationRecipe) {
                                fabricationRecipe = (IFabricationRecipe) ent.recipe;
                            }
                            if (fabricationRecipe == null || DataTabletUtil.playerHasEntry(pPlayer, fabricationRecipe.getEntry(), fabricationRecipe.getCompletionStage())) {
                                ent.time = 0;
                                pLevel.playSound(null, pPos, SoundRegistry.FABRICATOR_START.value(), SoundSource.BLOCKS, 2, 1);
                            } else {
                                pPlayer.sendSystemMessage(Component.translatable("block.datanessence.fabricator.dont_know_how"));
                            }
                        } else {
                            pPlayer.sendSystemMessage(Component.translatable("block.datanessence.fabricator.not_enough_essence"));
                        }
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    public void tryCraft() {
        CraftingInput craftingInput = getCraftingInv().asCraftInput();
        ItemStack stack = recipe.assemble(craftingInput, level.registryAccess()).copy();
        NonNullList<ItemStack> remaining = recipe.getRemainingItems(craftingInput);
        int left = getCraftingInv().asPositionedCraftInput().left();
        int top = getCraftingInv().asPositionedCraftInput().top();
        int craftWidth = 3;

        if (essenceCost != null) {
            for (Map.Entry<ResourceLocation, Float> cost : essenceCost.entrySet()) {
                EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(cost.getKey());
                storage.removeEssence(type, cost.getValue());
            }
        }

        for (int k = 0; k < craftingInput.height(); k++) {
            for (int l = 0; l < craftingInput.width(); l++) {
                int i1 = l + left + (k + top) * craftWidth;
                ItemStack itemstack = itemHandler.getStackInSlot(i1);
                ItemStack itemstack1 = remaining.get(l + k * craftingInput.width());
                if (!itemstack.isEmpty()) {
                    itemHandler.extractItem(i1, 1, false);
                    itemstack = itemHandler.getStackInSlot(i1);
                }

                if (!itemstack1.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        itemHandler.setStackInSlot(i1, itemstack1);
                    } else if (ItemStack.isSameItemSameComponents(itemstack, itemstack1)) {
                        itemstack1.grow(itemstack.getCount());
                        itemHandler.setStackInSlot(i1, itemstack1);
                    } else {
                        ItemEntity entity = new ItemEntity(level, (float) getBlockPos().getX() + 0.5f, (float) getBlockPos().getY() + 1f, (float) getBlockPos().getZ() + 0.5f, stack);
                        level.addFreshEntity(entity);
                    }
                }
            }
        }

        ItemEntity entity = new ItemEntity(level, (float) getBlockPos().getX() + 0.5f, (float) getBlockPos().getY() + 1f, (float) getBlockPos().getZ() + 0.5f, stack);
        level.addFreshEntity(entity);
        level.playSound(null, getBlockPos(), SoundRegistry.FABRICATOR_CRAFT.value(), SoundSource.BLOCKS, 2, 1);
    }
    public int time;
    public int maxTime;
    public Recipe<CraftingInput> recipe;
    public boolean enoughEssence;
    public Map<ResourceLocation, Float> essenceCost;

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseFabricatorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, pBlockEntity.getSupportedEssenceTypes());
            if (pBlockEntity.recipe != null) {
                boolean enoughEssence = true;
                if (pBlockEntity.essenceCost != null) {
                    for (Map.Entry<ResourceLocation, Float> i : pBlockEntity.essenceCost.entrySet()) {
                        EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                        if (pBlockEntity.storage.getEssence(type) < i.getValue()) {
                            enoughEssence = false;
                        }
                    }
                }
                pBlockEntity.enoughEssence = enoughEssence;
                if (pBlockEntity.recipe.matches(pBlockEntity.getCraftingInv().asCraftInput(), pBlockEntity.level)) {
                    if (pBlockEntity.time != -1) {
                        pBlockEntity.time++;
                    }
                } else {
                    pBlockEntity.time = -1;
                }
            } else {
                pBlockEntity.time = -1;
            }
            if (pBlockEntity.time >= pBlockEntity.maxTime) {
                pBlockEntity.tryCraft();
                pBlockEntity.time = 0;
            }
            pBlockEntity.updateBlock();
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, Block.UPDATE_ALL);
        this.setChanged();
    }
    private static boolean hasNotReachedStackLimit(BaseFabricatorBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(2).getCount() < entity.itemHandler.getStackInSlot(2).getMaxStackSize();
    }
}
