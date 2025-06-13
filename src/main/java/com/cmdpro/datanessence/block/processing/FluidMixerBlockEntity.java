package com.cmdpro.datanessence.block.processing;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.api.MultiFluidTank;
import com.cmdpro.datanessence.api.MultiFluidTankNoDuplicateFluids;
import com.cmdpro.datanessence.recipe.FluidMixingRecipe;
import com.cmdpro.datanessence.recipe.RecipeInputWithFluid;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.FluidMixerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidMixerBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("working", (state, anim) -> {}, (state, anim) -> {}));

    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
    public FluidMixingRecipe recipe;
    public boolean enoughEssence;
    public float essenceCost;
    public int workTime, maxWorkTime;
    public ItemStack item;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            checkRecipes();
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

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            checkRecipes();
        }
    }

    private final MultiFluidTank fluidHandler = new MultiFluidTankNoDuplicateFluids(List.of(new FluidTank(1000) { @Override protected void onContentsChanged() { checkRecipes(); } }, new FluidTank(1000) { @Override protected void onContentsChanged() { checkRecipes(); } }));
    private final FluidTank outputFluidHandler = new FluidTank(1000);
    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }
    

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
    public IFluidHandler getOutputHandler() {
        return outputFluidHandler;
    }

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler, dataDriveHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }

    public FluidMixerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FLUID_MIXER.get(), pos, state);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        workTime = tag.getInt("workTime");
        maxWorkTime = tag.getInt("maxWorkTime");
        itemHandler.deserializeNBT(pRegistries, tag.getCompound("itemHandler"));
        fluidHandler.readFromNBT(pRegistries, tag.getCompound("fluidHandler"));
        outputFluidHandler.readFromNBT(pRegistries, tag.getCompound("outputFluidHandler"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("workTime", workTime);
        tag.putInt("maxWorkTime", recipe != null ? recipe.getTime() : -1);
        tag.put("itemHandler", itemHandler.serializeNBT(pRegistries));
        tag.put("fluidHandler", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        tag.put("outputFluidHandler", outputFluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("inventoryDrive", dataDriveHandler.serializeNBT(pRegistries));
        tag.put("fluids", fluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        tag.put("outputFluid", outputFluidHandler.writeToNBT(pRegistries, new CompoundTag()));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("workTime", workTime);
        super.saveAdditional(tag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        dataDriveHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryDrive"));
        fluidHandler.readFromNBT(pRegistries, nbt.getCompound("fluids"));
        outputFluidHandler.readFromNBT(pRegistries, nbt.getCompound("outputFluid"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        workTime = nbt.getInt("workTime");
    }

    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots()+dataDriveHandler.getSlots());
        for (int i = 0; i < dataDriveHandler.getSlots(); i++) {
            inventory.setItem(i, dataDriveHandler.getStackInSlot(i));
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i+dataDriveHandler.getSlots(), itemHandler.getStackInSlot(i));
        }
        return inventory;
    }

    public RecipeInputWithFluid getCraftingInv() {
        RecipeInput inventory = new SingleRecipeInput(itemHandler.getStackInSlot(0));
        List<FluidStack> fluids = new ArrayList<>();
        for (int i = 0; i < fluidHandler.getTanks(); i++) {
            fluids.add(fluidHandler.getFluidInTank(i));
        }
        return new RecipeInputWithFluid(inventory, fluids);
    }

    public void checkRecipes() {
        Optional<RecipeHolder<FluidMixingRecipe>> recipe = level.getRecipeManager().getRecipesFor(RecipeRegistry.FLUID_MIXING_TYPE.get(), getCraftingInv(), level).stream().filter((a) -> a.value().getEntry().equals(DataDrive.getEntryId(dataDriveHandler.getStackInSlot(0)))).findFirst();
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FluidMixerBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, EssenceTypeRegistry.ESSENCE.get());
            BufferUtil.getItemsFromBuffersBelow(pBlockEntity, pBlockEntity.itemHandler);
            BufferUtil.getFluidsFromBuffersBelow(pBlockEntity, pBlockEntity.fluidHandler);
            boolean resetWorkTime = true;
            if (pBlockEntity.recipe != null) {
                boolean enoughEssence = false;
                if (pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) >= pBlockEntity.essenceCost/pBlockEntity.recipe.getTime()) {
                    enoughEssence = true;
                }
                pBlockEntity.enoughEssence = enoughEssence;
                if (enoughEssence) {
                    FluidStack result = pBlockEntity.recipe.getOutput();
                    if (pBlockEntity.outputFluidHandler.fill(result, IFluidHandler.FluidAction.SIMULATE) >= result.getAmount()) {
                        resetWorkTime = false;
                        pBlockEntity.workTime++;
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), pBlockEntity.essenceCost/pBlockEntity.recipe.getTime());
                        if (pBlockEntity.workTime >= pBlockEntity.recipe.getTime()) {
                            FluidStack input1 = pBlockEntity.recipe.getInput1();
                            FluidStack input2 = pBlockEntity.recipe.getInput2();
                            pBlockEntity.outputFluidHandler.fill(result, IFluidHandler.FluidAction.EXECUTE);
                            pBlockEntity.itemHandler.extractItem(0, 1, false);
                            pBlockEntity.fluidHandler.drain(input1, IFluidHandler.FluidAction.EXECUTE);
                            pBlockEntity.fluidHandler.drain(input2, IFluidHandler.FluidAction.EXECUTE);
                            pBlockEntity.workTime = 0;
                        }
                    }
                }
            }
            if (resetWorkTime) {
                pBlockEntity.workTime = -1;
            }
            pBlockEntity.updateBlock();
        } else {
            Optional<RecipeHolder<FluidMixingRecipe>> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeRegistry.FLUID_MIXING_TYPE.get(), pBlockEntity.getCraftingInv(), pLevel);
            recipe.ifPresentOrElse(recipeHolder -> pBlockEntity.recipe = recipeHolder.value(), () -> pBlockEntity.recipe = null);
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
        return new FluidMixerMenu(pContainerId, pInventory, this);
    }
}
