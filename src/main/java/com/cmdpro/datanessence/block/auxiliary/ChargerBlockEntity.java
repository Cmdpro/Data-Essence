package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.util.item.EssenceChargeableItemUtil;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.ChargerMenu;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargerBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity {
    public AnimationDefinition anim;
    public AnimationState animState = new AnimationState();
    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }
    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.CHARGER.get(), pos, state);
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
        charging = tag.getBoolean("charging");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putBoolean("charging", charging);
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
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public boolean charging;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ChargerBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            pBlockEntity.charging = false;
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()));
            ItemStack stack = pBlockEntity.itemHandler.getStackInSlot(0).copy();
            pBlockEntity.item = stack;
            float maxEssence = EssenceChargeableItemUtil.getMaxEssence(stack);
            if (maxEssence > 0) {
                float essence = EssenceChargeableItemUtil.getEssence(stack);
                if (essence < maxEssence) {
                    float fill = Math.min(maxEssence-essence, Math.min(5f, pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get())));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillEssence(stack, fill);
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            float maxLunarEssence = EssenceChargeableItemUtil.getMaxLunarEssence(stack);
            if (maxLunarEssence > 0) {
                float essence = EssenceChargeableItemUtil.getLunarEssence(stack);
                if (essence < maxLunarEssence) {
                    float fill = Math.min(maxLunarEssence-essence, Math.min(5f, pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get())));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillLunarEssence(stack, fill);
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.LUNAR_ESSENCE.get(), fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            float maxNaturalEssence = EssenceChargeableItemUtil.getMaxNaturalEssence(stack);
            if (maxNaturalEssence > 0) {
                float essence = EssenceChargeableItemUtil.getNaturalEssence(stack);
                if (essence < maxNaturalEssence) {
                    float fill = Math.min(maxNaturalEssence-essence, Math.min(5f, pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.NATURAL_ESSENCE.get())));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillNaturalEssence(stack, fill);
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.NATURAL_ESSENCE.get(), fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            float maxExoticEssence = EssenceChargeableItemUtil.getMaxExoticEssence(stack);
            if (maxExoticEssence > 0) {
                float essence = EssenceChargeableItemUtil.getExoticEssence(stack);
                if (essence < maxExoticEssence) {
                    float fill = Math.min(maxExoticEssence-essence, Math.min(5f, pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.EXOTIC_ESSENCE.get())));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillExoticEssence(stack, fill);
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.EXOTIC_ESSENCE.get(), fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            pBlockEntity.itemHandler.setStackInSlot(0, stack);
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
        return new ChargerMenu(pContainerId, pInventory, this);
    }
}
