package com.cmdpro.datanessence.block.processing;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.IHasRequiredKnowledge;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.AutoFabricatorMenu;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutoFabricatorBlockEntity extends EssenceContainer implements MenuProvider, GeoBlockEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
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
            if (slot == 0) {
                return stack.getItem() instanceof DataDrive;
            }
            return super.isItemValid(slot, stack);
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
        setEssence(tag.getFloat("essence"));
        setLunarEssence(tag.getFloat("lunarEssence"));
        setNaturalEssence(tag.getFloat("naturalEssence"));
        setExoticEssence(tag.getFloat("exoticEssence"));
        item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
        craftingProgress = tag.getInt("craftingProgress");
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
    public boolean hasIngredients(List<Ingredient> ingredients) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(itemHandler.getStackInSlot(i).copy());
        }
        for (Ingredient i : ingredients) {
            boolean found = false;
            for (int o = 0; o < 9; o++) {
                if (!i.isEmpty()) {
                    if (i.test(stacks.get(o))) {
                        found = true;
                        stacks.get(o).shrink(1);
                        break;
                    }
                } else {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    public boolean tryCraft() {
        if (recipe instanceof IFabricationRecipe recipe) {
            if (recipe.getEntry().equals(DataDrive.getEntry(dataDriveHandler.getStackInSlot(0)))) {
                return false;
            }
        }
        ItemStack stack = recipe.assemble(getCraftingInv().asCraftInput(), level.registryAccess()).copy();
        List<Ingredient> ingredients = recipe.getIngredients();
        if (!hasIngredients(ingredients)) {
            return false;
        }
        for (Ingredient i : ingredients) {
            for (int o = 0; o < 9; o++) {
                if (i.test(itemHandler.getStackInSlot(o))) {
                    itemHandler.extractItem(o, 1, false);
                    break;
                }
            }
        }
        setEssence(getEssence()-essenceCost);
        setLunarEssence(getLunarEssence()-lunarEssenceCost);
        setNaturalEssence(getNaturalEssence()-naturalEssenceCost);
        setExoticEssence(getExoticEssence()-exoticEssenceCost);
        outputItemHandler.insertItem(0, stack, false);
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 2, 1);
        return true;
    }
    public CraftingRecipe recipe;
    public boolean enoughEssence;
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public int craftingProgress;
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(
            RecipeType<T> type, Level level
    ) {
        if (getCraftingInv().isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager().getAllRecipesFor(type).stream().filter(a -> {
            if (a.value() instanceof IHasRequiredKnowledge recipe) {
                if (recipe.getEntry().equals(DataDrive.getEntry(dataDriveHandler.getStackInSlot(0)))) {
                    return false;
                }
            }
            return hasIngredients(a.value().getIngredients());
        }).findFirst();
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AutoFabricatorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            DataNEssenceUtil.getEssenceFromBuffersBelow(pBlockEntity);
            Optional<RecipeHolder<IFabricationRecipe>> recipe = pBlockEntity.getRecipeFor(RecipeRegistry.FABRICATIONCRAFTING.get(), pLevel);
            if (recipe.isPresent() && hasNotReachedStackLimit(pBlockEntity, recipe.get().value().getResultItem(pLevel.registryAccess()))) {
                pBlockEntity.recipe = recipe.get().value();
                pBlockEntity.essenceCost = recipe.get().value().getEssenceCost();
                pBlockEntity.lunarEssenceCost = recipe.get().value().getLunarEssenceCost();
                pBlockEntity.naturalEssenceCost = recipe.get().value().getNaturalEssenceCost();
                pBlockEntity.exoticEssenceCost = recipe.get().value().getExoticEssenceCost();
                pBlockEntity.item = recipe.get().value().getResultItem(pLevel.registryAccess());
                boolean enoughEssence = false;
                if (pBlockEntity.getEssence() >= pBlockEntity.essenceCost &&
                        pBlockEntity.getLunarEssence() >= pBlockEntity.lunarEssenceCost &&
                        pBlockEntity.getNaturalEssence() >= pBlockEntity.naturalEssenceCost &&
                        pBlockEntity.getExoticEssence() >= pBlockEntity.exoticEssenceCost) {
                    enoughEssence = true;
                }
                pBlockEntity.enoughEssence = enoughEssence;
                if (pBlockEntity.hasIngredients(recipe.get().value().getIngredients())) {
                    pBlockEntity.craftingProgress++;
                } else {
                    pBlockEntity.craftingProgress = -1;
                }
            } else {
                pBlockEntity.recipe = null;
                pBlockEntity.essenceCost = 0;
                pBlockEntity.lunarEssenceCost = 0;
                pBlockEntity.naturalEssenceCost = 0;
                pBlockEntity.exoticEssenceCost = 0;
                pBlockEntity.item = ItemStack.EMPTY;
                pBlockEntity.craftingProgress = -1;
            }
            if (pBlockEntity.craftingProgress >= 50) {
                pBlockEntity.tryCraft();
                pBlockEntity.craftingProgress = 0;
            }
            pBlockEntity.updateBlock();
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
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        if (craftingProgress >= 0) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.auto-fabricator.crafting", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("animation.auto-fabricator.idle", Animation.LoopType.LOOP));
        }
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
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new AutoFabricatorMenu(pContainerId, pInventory, this);
    }
}
