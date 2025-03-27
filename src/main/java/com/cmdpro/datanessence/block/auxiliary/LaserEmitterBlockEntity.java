package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.api.item.ILaserEmitterModule;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.LaserEmitterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaserEmitterBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
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
    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }
    public IItemHandler getItemHandler() {
        return itemHandler;
    }
    
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LASER_EMITTER.get(), pos, state);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        storage.fromNbt(pkt.getTag());
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return storage.toNbt();
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
    }

    public Vec3 end;
    public int redstoneLevel;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, LaserEmitterBlockEntity pBlockEntity) {
        int oldRedstoneLevel = pBlockEntity.redstoneLevel;
        boolean updated = false;
        boolean drainsEssence = true;
        if (!(pBlockEntity.itemHandler.getStackInSlot(0).getItem() instanceof ILaserEmitterModule)) {
            drainsEssence = false;
        }
        if (!pLevel.isClientSide) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, EssenceTypeRegistry.ESSENCE.get());
        }
        if (pBlockEntity.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 0.5f || !drainsEssence) {
            float dist = 0;
            for (int i = 1; i <= 10; i++) {
                BlockPos pos = pPos.relative(pState.getValue(LaserEmitter.FACING), i);
                if (pLevel.getBlockState(pos).isSolid()) {
                    break;
                }
                dist = (float) i + 0.5f;
            }
            if (dist > 0) {
                pBlockEntity.end = pPos.getCenter().relative(pState.getValue(LaserEmitter.FACING), dist);
                if (!pLevel.isClientSide) {
                    if (drainsEssence) {
                        pBlockEntity.storage.removeEssence(EssenceTypeRegistry.ESSENCE.get(), 0.5f);
                    }
                    pBlockEntity.updateBlock();
                    updated = true;
                    AABB bounds = AABB.encapsulatingFullBlocks(pPos, pPos.relative(pState.getValue(LaserEmitter.FACING), (int) (dist - 0.5f)));
                    List<LivingEntity> ents = pLevel.getEntitiesOfClass(LivingEntity.class, bounds);
                    ILaserEmitterModule module = null;
                    if (pBlockEntity.itemHandler.getStackInSlot(0).getItem() instanceof ILaserEmitterModule mod) {
                        module = mod;
                    }
                    for (LivingEntity i : ents) {
                        if (module != null) {
                            module.applyToMob(pBlockEntity, i);
                        }
                    }
                    pBlockEntity.redstoneLevel = module == null ? (ents.size() > 0 ? 15 : 0) : module.getRedstoneLevel(pBlockEntity, ents);
                } else {
                    pBlockEntity.redstoneLevel = 0;
                }
            } else {
                pBlockEntity.end = null;
                pBlockEntity.redstoneLevel = 0;
            }
        } else {
            pBlockEntity.end = null;
            pBlockEntity.redstoneLevel = 0;
        }
        if (oldRedstoneLevel != pBlockEntity.redstoneLevel && !updated) {
            pBlockEntity.updateBlock();
            updated = true;
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
        return new LaserEmitterMenu(pContainerId, pInventory, this);
    }
}
