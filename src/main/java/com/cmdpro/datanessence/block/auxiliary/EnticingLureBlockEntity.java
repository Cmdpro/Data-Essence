package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.screen.EnticingLureMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnticingLureBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }

    public EnticingLureBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ENTICING_LURE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
    }
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EnticingLureBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            ItemStack stack = pBlockEntity.itemHandler.getStackInSlot(0).copy();

            for (Animal i : pLevel.getEntitiesOfClass(Animal.class, AABB.ofSize(pPos.getCenter(), 10, 10, 10))) {
                if (i.isFood(pBlockEntity.itemHandler.getStackInSlot(0))) {
                    if (i.position().distanceTo(pPos.getCenter()) > 3) {
                        Vec3 pos = pPos.getCenter().add(i.position().subtract(pPos.getCenter()).normalize().multiply(2.5f, 2.5f, 2.5f));
                        if (i.getNavigation().getPath() == null || i.getNavigation().getPath().getTarget().distToCenterSqr(pos) > 0.25) {
                            i.getNavigation().stop();
                            i.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0);
                        }
                    }
                }
            }
        }
    }
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnticingLureMenu(pContainerId, pInventory, this);
    }
}
