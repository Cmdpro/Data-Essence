package com.cmdpro.datanessence.api.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.item.INodeUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.awt.*;

public abstract class BaseEssencePointBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public BlockPos link;
    public SingleEssenceContainer storage;

    public final ItemStackHandler universalUpgrade = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final ItemStackHandler uniqueUpgrade = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    @SuppressWarnings("unchecked")
    public <T> T getValue(ResourceLocation id, T defaultValue) {
        T value = defaultValue;
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            Object modified = upgrade.getValue(id, value, this);
            if (modified != null) {
                value = (T)modified;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            Object modified = upgrade.getValue(id, value, this);
            if (modified != null) {
                value = (T)modified;
            }
        }
        return value;
    }
    public boolean preTransferHooks(BlockEntity other) {
        boolean cancel = false;
        /*
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(this, other, cancel)) {
                cancel = true;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(this, other, cancel)) {
                cancel = true;
            }
        }*/
        return cancel;
    }
    public void postTransferHooks(BlockEntity other) {
        /*
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(this, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(this, other);
        }
         */
    }
    public boolean preTakeHooks(BlockEntity other) {
        boolean cancel = false;
        /*
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTake(this, other, cancel)) {
                cancel = true;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTake(this, other, cancel)) {
                cancel = true;
            }
        }
         */
        return cancel;
    }
    public void postTakeHooks(BlockEntity other) {
        /*
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTake(this, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTake(this, other);
        }
         */
    }
    public float getFinalSpeed(float value) {
        return value*getValue(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "speed_multiplier"), 1f);
    }
    @Override
    public SingleEssenceContainer getStorage() {
        return storage;
    }

    public BaseEssencePointBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    public abstract Color linkColor();


    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putBoolean("hasLink", link != null);
        if (link != null) {
            pTag.putInt("linkX", link.getX());
            pTag.putInt("linkY", link.getY());
            pTag.putInt("linkZ", link.getZ());
        }
        pTag.put("EssenceStorage", storage.toNbt());
        pTag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        pTag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
    }
    public static void updateBlock(BlockEntity ent) {
        BlockState blockState = ent.getLevel().getBlockState(ent.getBlockPos());
        ent.getLevel().sendBlockUpdated(ent.getBlockPos(), blockState, blockState, 3);
        ent.setChanged();
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseEssencePointBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BlockEntity ent1 = pLevel.getBlockEntity(pPos.relative(pBlockEntity.getDirection().getOpposite()));
            if (ent1 instanceof EssenceBlockEntity ent) {
                if (pBlockEntity.link == null) {
                    pBlockEntity.preTransferHooks(ent1);
                    pBlockEntity.deposit(ent1, ent.getStorage());
                    pBlockEntity.postTransferHooks(ent1);
                    updateBlock(ent1);
                } else {
                    pBlockEntity.preTakeHooks(ent1);
                    pBlockEntity.take(ent1, ent.getStorage());
                    pBlockEntity.postTakeHooks(ent1);
                    updateBlock(ent1);
                }
            }
            if (pBlockEntity.link != null) {
                BlockEntity ent2 = pLevel.getBlockEntity(pBlockEntity.link);
                if (ent2 instanceof EssenceBlockEntity ent) {
                    pBlockEntity.preTransferHooks(ent2);
                    pBlockEntity.transfer(ent2, ent.getStorage());
                    pBlockEntity.postTransferHooks(ent2);
                    updateBlock(ent2);
                } else {
                    pBlockEntity.link = null;
                    pBlockEntity.updateBlock();
                    if (pState.getBlock() instanceof BaseEssencePoint block) {
                        ItemEntity item = new ItemEntity(pLevel, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, new ItemStack(block.getRequiredWire()));
                        pLevel.addFreshEntity(item);
                    }
                }
            }
        }
    }
    public abstract void deposit(BlockEntity otherEntity, EssenceStorage other);
    public abstract void transfer(BlockEntity otherEntity, EssenceStorage other);
    public abstract void take(BlockEntity otherEntity, EssenceStorage other);
    public Direction getDirection() {
        if (getBlockState().getValue(BaseEssencePoint.FACE).equals(AttachFace.CEILING)) {
            return Direction.DOWN;
        }
        if (getBlockState().getValue(BaseEssencePoint.FACE).equals(AttachFace.WALL)) {
            return getBlockState().getValue(BaseEssencePoint.FACING);
        }
        return Direction.UP;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        if (tag.getBoolean("hasLink")) {
            link = new BlockPos(tag.getInt("linkX"), tag.getInt("linkY"), tag.getInt("linkZ"));
        } else {
            link = null;
        }
        uniqueUpgrade.deserializeNBT(lookupProvider, tag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(lookupProvider, tag.getCompound("universalUpgrade"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("hasLink", link != null);
        if (link != null) {
            tag.putInt("linkX", link.getX());
            tag.putInt("linkY", link.getY());
            tag.putInt("linkZ", link.getZ());
        }
        tag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        tag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
        return tag;
    }
    public void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.getBoolean("hasLink")) {
            link = new BlockPos(pTag.getInt("linkX"), pTag.getInt("linkY"), pTag.getInt("linkZ"));
        }
        storage.fromNbt(pTag.getCompound("EssenceStorage"));
        uniqueUpgrade.deserializeNBT(pRegistries, pTag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, pTag.getCompound("universalUpgrade"));
    }

}
