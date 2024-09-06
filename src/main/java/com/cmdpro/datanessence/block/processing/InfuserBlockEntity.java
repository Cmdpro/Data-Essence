package com.cmdpro.datanessence.block.processing;

import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.InfuserMenu;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.Optional;

public class InfuserBlockEntity extends EssenceContainer implements MenuProvider, GeoBlockEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
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
    private Lazy<IItemHandler> lazyCombinedHandler = Lazy.of(() -> new CombinedInvWrapper(dataDriveHandler, itemHandler, outputItemHandler));

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
    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INFUSER.get(), pos, state);
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
        workTime = tag.getInt("workTime");
        item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putInt("workTime", workTime);
        tag.put("item", item.saveOptional(pRegistries));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("input", itemHandler.serializeNBT(pRegistries));
        tag.put("dataDrive", dataDriveHandler.serializeNBT(pRegistries));
        tag.put("output", outputItemHandler.serializeNBT(pRegistries));
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putInt("workTime", workTime);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("input"));
        dataDriveHandler.deserializeNBT(pRegistries, nbt.getCompound("dataDrive"));
        outputItemHandler.deserializeNBT(pRegistries, nbt.getCompound("output"));
        setEssence(nbt.getFloat("essence"));
        setLunarEssence(nbt.getFloat("lunarEssence"));
        setNaturalEssence(nbt.getFloat("naturalEssence"));
        setExoticEssence(nbt.getFloat("exoticEssence"));
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
        RecipeInput inventory = new SingleRecipeInput(itemHandler.getStackInSlot(0));
        return inventory;
    }
    public int workTime;
    public InfusionRecipe recipe;
    public boolean enoughEssence;
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, InfuserBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity);
            BufferUtil.getItemsFromBuffersBelow(pBlockEntity);
            pBlockEntity.item = pBlockEntity.itemHandler.getStackInSlot(0);
            boolean shouldReset = true;
            Optional<RecipeHolder<InfusionRecipe>> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeRegistry.INFUSION_TYPE.get(), pBlockEntity.getCraftingInv(), pLevel);
            if (recipe.isPresent()) {
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
                    Entry entry = DataDrive.getEntry(pBlockEntity.dataDriveHandler.getStackInSlot(0));
                    if (entry != null) {
                        if (pBlockEntity.recipe == null || pBlockEntity.recipe.getEntry().equals(entry.id)) {
                            if (hasNotReachedStackLimit(pBlockEntity, pBlockEntity.recipe.getResultItem(pLevel.registryAccess()))) {
                                if (pBlockEntity.workTime >= 50) {
                                    ItemStack stack = pBlockEntity.recipe.assemble(pBlockEntity.getCraftingInv(), pLevel.registryAccess()).copy();
                                    pBlockEntity.outputItemHandler.insertItem(0, stack, false);
                                    pBlockEntity.itemHandler.extractItem(0, 1, false);
                                    pBlockEntity.setEssence(pBlockEntity.getEssence() - pBlockEntity.essenceCost);
                                    pBlockEntity.setLunarEssence(pBlockEntity.getLunarEssence() - pBlockEntity.lunarEssenceCost);
                                    pBlockEntity.setNaturalEssence(pBlockEntity.getNaturalEssence() - pBlockEntity.naturalEssenceCost);
                                    pBlockEntity.setExoticEssence(pBlockEntity.getExoticEssence() - pBlockEntity.exoticEssenceCost);
                                    pLevel.playSound(null, pPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 2, 1);
                                    pBlockEntity.workTime = 0;
                                } else {
                                    pBlockEntity.workTime++;
                                }
                                shouldReset = false;
                            }
                        }
                    }
                }
                pBlockEntity.enoughEssence = enoughEssence;
            }
            if (shouldReset) {
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
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        if (!item.isEmpty()) {
            if (workTime >= 0) {
                event.getController().setAnimation(RawAnimation.begin().then("animation.infuser.active", Animation.LoopType.LOOP));
            } else {
                event.getController().setAnimation(RawAnimation.begin().then("animation.infuser.idle", Animation.LoopType.LOOP));
            }
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("animation.infuser.deactivated", Animation.LoopType.LOOP));
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
        return new InfuserMenu(pContainerId, pInventory, this);
    }
    private static boolean hasNotReachedStackLimit(InfuserBlockEntity entity, ItemStack toAdd) {
        if (toAdd.is(entity.outputItemHandler.getStackInSlot(0).getItem())) {
            return entity.outputItemHandler.getStackInSlot(0).getCount() + toAdd.getCount() <= entity.outputItemHandler.getStackInSlot(0).getMaxStackSize();
        } else return entity.outputItemHandler.getStackInSlot(0).isEmpty();
    }
}
