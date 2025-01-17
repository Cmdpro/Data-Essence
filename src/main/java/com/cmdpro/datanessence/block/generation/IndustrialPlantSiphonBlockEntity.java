package com.cmdpro.datanessence.block.generation;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.item.EssenceShard;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.TagRegistry;
import com.cmdpro.datanessence.screen.EssenceBurnerMenu;
import com.cmdpro.datanessence.screen.IndustrialPlantSiphonMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class IndustrialPlantSiphonBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity {
    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get()), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return stack.is(TagRegistry.Items.LOW_ESSENCE_PLANTS) || stack.is(TagRegistry.Items.MEDIUM_ESSENCE_PLANTS) || stack.is(TagRegistry.Items.HIGH_ESSENCE_PLANTS);
            }
            return super.isItemValid(slot, stack);
        }
    };
    public float essenceBurnCooldown;

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);

    public IndustrialPlantSiphonBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INDUSTRIAL_PLANT_SIPHON.get(), pos, state);
    }
    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        essenceBurnCooldown = tag.getFloat("essenceBurnCooldown");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putFloat("essenceBurnCooldown", essenceBurnCooldown);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putFloat("essenceBurnCooldown", essenceBurnCooldown);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        essenceBurnCooldown = nbt.getFloat("essenceBurnCooldown");
    }
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, IndustrialPlantSiphonBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getItemsFromBuffersBelow(pBlockEntity);
            float essence = 0;
            if (pBlockEntity.itemHandler.getStackInSlot(0).is(TagRegistry.Items.LOW_ESSENCE_PLANTS)) {
                essence = DataNEssenceConfig.lowValuePlantEssence;
            }
            if (pBlockEntity.itemHandler.getStackInSlot(0).is(TagRegistry.Items.MEDIUM_ESSENCE_PLANTS)) {
                essence = DataNEssenceConfig.mediumValuePlantEssence;
            }
            if (pBlockEntity.itemHandler.getStackInSlot(0).is(TagRegistry.Items.HIGH_ESSENCE_PLANTS)) {
                essence = DataNEssenceConfig.highValuePlantEssence;
            }
            if (essence > 0) {
                boolean canFit = true;
                if (pBlockEntity.storage.getEssence(EssenceTypeRegistry.ESSENCE.get())+essence > pBlockEntity.storage.getMaxEssence()) {
                    canFit = false;
                }
                pBlockEntity.essenceBurnCooldown--;
                if (pBlockEntity.essenceBurnCooldown <= 0) {
                    if (canFit) {
                        pBlockEntity.itemHandler.extractItem(0, 1, false);
                        pBlockEntity.storage.addEssence(EssenceTypeRegistry.ESSENCE.get(), essence);
                        pBlockEntity.essenceBurnCooldown = 50;
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
        return new IndustrialPlantSiphonMenu(pContainerId, pInventory, this);
    }
}
