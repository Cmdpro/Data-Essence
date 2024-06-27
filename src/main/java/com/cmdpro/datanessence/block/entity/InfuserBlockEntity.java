package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.api.EssenceShard;
import com.cmdpro.datanessence.init.BlockEntityInit;
import com.cmdpro.datanessence.init.RecipeInit;
import com.cmdpro.datanessence.item.DataDrive;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import com.cmdpro.datanessence.screen.InfuserMenu;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
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
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyDataDriveHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyCombinedHandler = LazyOptional.empty();

    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.INFUSER.get(), pos, state);
        item = ItemStack.EMPTY;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyCombinedHandler.cast();
            }
            if (side == Direction.DOWN) {
                return lazyItemHandler.cast();
            }
            return lazyOutputHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt){
        CompoundTag tag = pkt.getTag();
        setEssence(tag.getFloat("essence"));
        setLunarEssence(tag.getFloat("lunarEssence"));
        setNaturalEssence(tag.getFloat("naturalEssence"));
        setExoticEssence(tag.getFloat("exoticEssence"));
        workTime = tag.getInt("workTime");
        item = ItemStack.of(tag.getCompound("item"));
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putInt("workTime", workTime);
        tag.put("item", item.save(new CompoundTag()));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("input", itemHandler.serializeNBT());
        tag.put("dataDrive", dataDriveHandler.serializeNBT());
        tag.put("output", outputItemHandler.serializeNBT());
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putInt("workTime", workTime);
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("input"));
        dataDriveHandler.deserializeNBT(nbt.getCompound("dataDrive"));
        outputItemHandler.deserializeNBT(nbt.getCompound("output"));
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
    public SimpleContainer getCraftingInv() {
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        return inventory;
    }
    public int workTime;
    @Override
    public void onLoad() {
        super.onLoad();
        lazyDataDriveHandler = LazyOptional.of(() -> dataDriveHandler);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyOutputHandler = LazyOptional.of(() -> outputItemHandler);
        lazyCombinedHandler = LazyOptional.of( () -> new CombinedInvWrapper(dataDriveHandler, itemHandler, outputItemHandler) {

        });
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyDataDriveHandler.invalidate();
        lazyItemHandler.invalidate();
        lazyOutputHandler.invalidate();
        lazyCombinedHandler.invalidate();
    }
    public InfusionRecipe recipe;
    public boolean enoughEssence;
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, InfuserBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            DataNEssenceUtil.getEssenceFromBuffersBelow(pBlockEntity);
            DataNEssenceUtil.getItemsFromBuffersBelow(pBlockEntity);
            pBlockEntity.item = pBlockEntity.itemHandler.getStackInSlot(0);
            Optional<InfusionRecipe> recipe = pLevel.getRecipeManager().getRecipeFor(InfusionRecipe.Type.INSTANCE, pBlockEntity.getCraftingInv(), pLevel);
            if (recipe.isPresent()) {
                pBlockEntity.recipe = recipe.get();
                pBlockEntity.essenceCost = recipe.get().getEssenceCost();
                pBlockEntity.lunarEssenceCost = recipe.get().getLunarEssenceCost();
                pBlockEntity.naturalEssenceCost = recipe.get().getNaturalEssenceCost();
                pBlockEntity.exoticEssenceCost = recipe.get().getExoticEssenceCost();
                boolean enoughEssence = false;
                if (pBlockEntity.getEssence() >= pBlockEntity.essenceCost &&
                        pBlockEntity.getLunarEssence() >= pBlockEntity.lunarEssenceCost &&
                        pBlockEntity.getNaturalEssence() >= pBlockEntity.naturalEssenceCost &&
                        pBlockEntity.getExoticEssence() >= pBlockEntity.exoticEssenceCost) {
                    enoughEssence = true;
                    Entry entry = DataDrive.getEntry(pBlockEntity.dataDriveHandler.getStackInSlot(0));
                    if (entry != null) {
                        if (pBlockEntity.recipe == null || pBlockEntity.recipe.getEntry().equals(entry.id.toString())) {
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
                            } else {
                                pBlockEntity.workTime = -1;
                            }
                        } else {
                            pBlockEntity.workTime = -1;
                        }
                    } else {
                        pBlockEntity.workTime = -1;
                    }
                } else {
                    pBlockEntity.workTime = -1;
                }
                pBlockEntity.enoughEssence = enoughEssence;
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
            event.getController().setAnimation(RawAnimation.begin().then("animation.infuser.idle", Animation.LoopType.LOOP));
            if (workTime >= 0) {
                event.getController().setAnimation(RawAnimation.begin().then("animation.infuser.active", Animation.LoopType.LOOP));
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
        }
        return true;
    }
}
