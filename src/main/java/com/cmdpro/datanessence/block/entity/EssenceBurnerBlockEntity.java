package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.api.EssenceShard;
import com.cmdpro.datanessence.init.BlockEntityInit;
import com.cmdpro.datanessence.init.RecipeInit;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.screen.EssenceBurnerMenu;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
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

public class EssenceBurnerBlockEntity extends EssenceContainer implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return stack.getItem() instanceof EssenceShard;
            }
            if (slot == 1) {
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
            }
            return super.isItemValid(slot, stack);
        }
    };
    public float burnTime;
    public float maxBurnTime;
    public float essenceBurnCooldown;
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

    public EssenceBurnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.ESSENCE_BURNER.get(), pos, state);
        maxBurnTime = 1;
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap);
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
        maxBurnTime = tag.getFloat("maxBurnTime");
        burnTime = tag.getFloat("burnTime");
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putFloat("maxBurnTime", maxBurnTime);
        tag.putFloat("burnTime", burnTime);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.putFloat("maxBurnTime", maxBurnTime);
        tag.putFloat("burnTime", burnTime);
        tag.putFloat("essenceBurnCooldown", essenceBurnCooldown);
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        setEssence(nbt.getFloat("essence"));
        setLunarEssence(nbt.getFloat("lunarEssence"));
        setNaturalEssence(nbt.getFloat("naturalEssence"));
        setExoticEssence(nbt.getFloat("exoticEssence"));
        maxBurnTime = nbt.getFloat("maxBurnTime");
        burnTime = nbt.getFloat("burnTime");
        essenceBurnCooldown = nbt.getFloat("essenceBurnCooldown");
    }
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EssenceBurnerBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            DataNEssenceUtil.getItemsFromBuffersBelow(pBlockEntity);
            if (pBlockEntity.burnTime <= 0) {
                pBlockEntity.essenceBurnCooldown = 0;
                if (pBlockEntity.itemHandler.getStackInSlot(0).getItem() instanceof EssenceShard shard) {
                    if (pBlockEntity.getEssence()+shard.essenceAmount <= pBlockEntity.getMaxEssence() &&
                            pBlockEntity.getLunarEssence()+shard.lunarEssenceAmount <= pBlockEntity.getMaxLunarEssence() &&
                            pBlockEntity.getNaturalEssence()+shard.naturalEssenceAmount <= pBlockEntity.getMaxNaturalEssence() &&
                            pBlockEntity.getExoticEssence()+shard.exoticEssenceAmount <= pBlockEntity.getMaxExoticEssence()) {
                        if (ForgeHooks.getBurnTime(pBlockEntity.itemHandler.getStackInSlot(1), RecipeType.SMELTING) > 0) {
                            pBlockEntity.itemHandler.extractItem(1, 1, false);
                            pBlockEntity.maxBurnTime = ForgeHooks.getBurnTime(pBlockEntity.itemHandler.getStackInSlot(1), RecipeType.SMELTING);
                            pBlockEntity.burnTime = pBlockEntity.maxBurnTime;
                        }
                    }
                }
            } else {
                pBlockEntity.essenceBurnCooldown--;
                pBlockEntity.burnTime--;
                if (pBlockEntity.essenceBurnCooldown <= 0) {
                    if (pBlockEntity.itemHandler.getStackInSlot(0).getItem() instanceof EssenceShard shard) {
                        if (pBlockEntity.getEssence()+shard.essenceAmount <= pBlockEntity.getMaxEssence() &&
                                pBlockEntity.getLunarEssence()+shard.lunarEssenceAmount <= pBlockEntity.getMaxLunarEssence() &&
                                pBlockEntity.getNaturalEssence()+shard.naturalEssenceAmount <= pBlockEntity.getMaxNaturalEssence() &&
                                pBlockEntity.getExoticEssence()+shard.exoticEssenceAmount <= pBlockEntity.getMaxExoticEssence()) {
                            pBlockEntity.itemHandler.extractItem(0, 1, false);
                            pBlockEntity.setEssence(pBlockEntity.getEssence()+shard.essenceAmount);
                            pBlockEntity.setLunarEssence(pBlockEntity.getLunarEssence()+shard.lunarEssenceAmount);
                            pBlockEntity.setNaturalEssence(pBlockEntity.getNaturalEssence()+shard.naturalEssenceAmount);
                            pBlockEntity.setExoticEssence(pBlockEntity.getExoticEssence()+shard.exoticEssenceAmount);
                            pBlockEntity.essenceBurnCooldown = 50;
                        }
                    }
                }
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
        return new EssenceBurnerMenu(pContainerId, pInventory, this);
    }
}
