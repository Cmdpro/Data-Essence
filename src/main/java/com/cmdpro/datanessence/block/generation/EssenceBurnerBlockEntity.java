package com.cmdpro.datanessence.block.generation;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.block.Machine;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.MultiEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.item.EssenceShard;
import com.cmdpro.datanessence.block.processing.EssenceFurnace;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.EssenceBurnerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class EssenceBurnerBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity, Machine {
    public MultiEssenceContainer storage = new MultiEssenceContainer(List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get(), EssenceTypeRegistry.NATURAL_ESSENCE.get(), EssenceTypeRegistry.EXOTIC_ESSENCE.get()), 1000);
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
                return stack.getItem() instanceof EssenceShard;
            }
            if (slot == 1) {
                return stack.getBurnTime(RecipeType.SMELTING) > 0;
            }
            return super.isItemValid(slot, stack);
        }
    };
    public float burnTime;
    public float maxBurnTime;
    public float essenceBurnCooldown;

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    

    public EssenceBurnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ESSENCE_BURNER.get(), pos, state);
        maxBurnTime = 1;
    }
    public IItemHandler getItemHandler() {
        return itemHandler;
    }
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        maxBurnTime = tag.getFloat("maxBurnTime");
        burnTime = tag.getFloat("burnTime");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putFloat("maxBurnTime", maxBurnTime);
        tag.putFloat("burnTime", burnTime);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putFloat("maxBurnTime", maxBurnTime);
        tag.putFloat("burnTime", burnTime);
        tag.putFloat("essenceBurnCooldown", essenceBurnCooldown);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
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
    public boolean lit;
    public static void tick(Level world, BlockPos pos, BlockState pState, EssenceBurnerBlockEntity burner) {
        if (!world.isClientSide()) {
            BufferUtil.getItemsFromBuffersBelow(burner, burner.itemHandler);
            burner.lit = false;
            if (burner.itemHandler.getStackInSlot(0).getItem() instanceof EssenceShard shard) {
                boolean hasSpaceToGenerate = true;
                for (Map.Entry<Supplier<EssenceType>, Float> i : shard.essence.entrySet()) {
                    if (burner.storage.getEssence(i.getKey().get())+i.getValue() > burner.storage.getMaxEssence()) {
                        hasSpaceToGenerate = false;
                        break;
                    }
                }
                if (burner.burnTime <= 0) {
                    burner.essenceBurnCooldown = 0;
                    if (hasSpaceToGenerate) {
                        var fuel = burner.itemHandler.getStackInSlot(1);

                        if (fuel.getBurnTime(RecipeType.SMELTING) > 0) {
                            burner.maxBurnTime = fuel.getBurnTime(RecipeType.SMELTING);
                            burner.itemHandler.extractItem(1, 1, false);

                            if ( fuel.hasCraftingRemainingItem() ) {
                                var remainder = fuel.getCraftingRemainingItem();
                                world.addFreshEntity(new ItemEntity(world, pos.getCenter().x, pos.getCenter().y+0.6, pos.getCenter().z, remainder));
                            }

                            burner.burnTime = burner.maxBurnTime;
                            burner.lit = true;
                        }
                    }
                } else {
                    burner.lit = true;
                    burner.essenceBurnCooldown--;
                    burner.burnTime--;
                    if (burner.essenceBurnCooldown <= 0) {
                        if (hasSpaceToGenerate) {
                            burner.itemHandler.extractItem(0, 1, false);
                            for (Map.Entry<Supplier<EssenceType>, Float> i : shard.essence.entrySet()) {
                                burner.storage.addEssence(i.getKey().get(), i.getValue());
                            }
                            burner.essenceBurnCooldown = 50;
                        }
                    }
                }
            }
            if (burner.lit != pState.getValue(EssenceBurner.LIT)) {
                BlockState state = pState.setValue(EssenceBurner.LIT, burner.lit);
                burner.level.setBlock(pos, state, 3);
            }
            burner.updateBlock();
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

    @Override
    public List<Direction> getValidInputDirections() {
        return List.of(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    }
}
