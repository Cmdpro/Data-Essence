package com.cmdpro.datanessence.block.processing;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.AnimationState;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FabricatorBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity {
    public AnimationState animState = new AnimationState();
    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 1000);
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
    public IItemHandler getItemHandler() {
        return itemHandler;
    }
    public void checkRecipes() {
        Optional<RecipeHolder<IFabricationRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.FABRICATIONCRAFTING.get(), getCraftingInv().asCraftInput(), level);
        if (recipe.isPresent()) {
            this.recipe = recipe.get().value();
            essenceCost = recipe.get().value().getEssenceCost();
            item = recipe.get().value().getResultItem(level.registryAccess());
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
        }
    }
    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    

    public FabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FABRICATOR.get(), pos, state);
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
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("item", item.saveOptional(pRegistries));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
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
    public static InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                        Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!pLevel.isClientSide) {
            if (blockEntity instanceof FabricatorBlockEntity ent) {
                if (ent.recipe != null) {
                    if (ent.enoughEssence) {
                        IFabricationRecipe fabricationRecipe = null;
                        if (ent.recipe instanceof IFabricationRecipe) {
                            fabricationRecipe = (IFabricationRecipe) ent.recipe;
                        }
                        if (fabricationRecipe == null || DataTabletUtil.playerHasEntry(pPlayer, fabricationRecipe.getEntry(), fabricationRecipe.allowIncomplete())) {
                            ItemStack stack = ent.recipe.assemble(ent.getCraftingInv().asCraftInput(), pLevel.registryAccess()).copy();
                            for (int i = 0; i < 9; i++) {
                                ent.itemHandler.extractItem(i, 1, false);
                            }
                            if (ent.essenceCost != null) {
                                for (Map.Entry<ResourceLocation, Float> i : ent.essenceCost.entrySet()) {
                                    EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                                    ent.storage.removeEssence(type, i.getValue());
                                }
                            }
                            ItemEntity entity = new ItemEntity(pLevel, (float) pPos.getX() + 0.5f, (float) pPos.getY() + 1f, (float) pPos.getZ() + 0.5f, stack);
                            pLevel.addFreshEntity(entity);
                            pLevel.playSound(null, pPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 2, 1);
                        } else {
                            pPlayer.sendSystemMessage(Component.translatable("block.datanessence.fabricator.dont_know_how"));
                        }
                    } else {
                        pPlayer.sendSystemMessage(Component.translatable("block.datanessence.fabricator.not_enough_essence"));
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    public Recipe<CraftingInput> recipe;
    public boolean enoughEssence;
    public Map<ResourceLocation, Float> essenceCost;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FabricatorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()));
            if (pBlockEntity.essenceCost != null) {
                boolean enoughEssence = true;
                for (Map.Entry<ResourceLocation, Float> i : pBlockEntity.essenceCost.entrySet()) {
                    EssenceType type = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i.getKey());
                    if (pBlockEntity.storage.getEssence(type) < i.getValue()) {
                        enoughEssence = false;
                    }
                }
                pBlockEntity.enoughEssence = enoughEssence;
            } else {
                pBlockEntity.enoughEssence = true;
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
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FabricatorMenu(pContainerId, pInventory, this);
    }
    private static boolean hasNotReachedStackLimit(FabricatorBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(2).getCount() < entity.itemHandler.getStackInSlot(2).getMaxStackSize();
    }
}
