package com.cmdpro.datanessence.api.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.datatablet.Page;
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

public abstract class BaseCapabilityPointBlockEntity extends BlockEntity {
    public BlockPos link;
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
    public float getFinalSpeed(float value) {
        return value*getValue(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "speed_multiplier"), 1f);
    }
    public int getFinalSpeed(int value) {
        return (int)(value*getValue(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "speed_multiplier"), 1f));
    }

    public BaseCapabilityPointBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
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
        pTag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        pTag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseCapabilityPointBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BlockEntity ent = pLevel.getBlockEntity(pPos.relative(pBlockEntity.getDirection().getOpposite()));
            if (ent != null) {
                if (pBlockEntity.link == null) {
                    pBlockEntity.preTransferHooks(ent);
                    pBlockEntity.deposit(ent);
                    pBlockEntity.postTransferHooks(ent);
                } else {
                    pBlockEntity.preTakeHooks(ent);
                    pBlockEntity.take(ent);
                    pBlockEntity.postTakeHooks(ent);
                }
            }
            if (pBlockEntity.link != null) {
                BlockEntity ent2 = pLevel.getBlockEntity(pBlockEntity.link);
                if (ent2 != null) {
                    pBlockEntity.preTransferHooks(ent2);
                    pBlockEntity.deposit(ent2);
                    pBlockEntity.postTransferHooks(ent2);
                } else {
                    pBlockEntity.link = null;
                    pBlockEntity.updateBlock();
                    if (pState.getBlock() instanceof BaseCapabilityPoint block) {
                        ItemEntity item = new ItemEntity(pLevel, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, new ItemStack(block.getRequiredWire()));
                        pLevel.addFreshEntity(item);
                    }
                }
            }
        }
    }
    public boolean preTransferHooks(BlockEntity other) {
        boolean cancel = false;
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(this, other, cancel)) {
                cancel = true;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(this, other, cancel)) {
                cancel = true;
            }
        }
        return cancel;
    }
    public void postTransferHooks(BlockEntity other) {
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(this, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(this, other);
        }
    }
    public boolean preTakeHooks(BlockEntity other) {
        boolean cancel = false;
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
        return cancel;
    }
    public void postTakeHooks(BlockEntity other) {
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTake(this, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTake(this, other);
        }
    }
    public abstract void deposit(BlockEntity other);
    public abstract void transfer(BlockEntity other);
    public abstract void take(BlockEntity other);
    public Direction getDirection() {
        if (getBlockState().getValue(BaseCapabilityPoint.FACE).equals(AttachFace.CEILING)) {
            return Direction.DOWN;
        }
        if (getBlockState().getValue(BaseCapabilityPoint.FACE).equals(AttachFace.WALL)) {
            return getBlockState().getValue(BaseCapabilityPoint.FACING);
        }
        return Direction.UP;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        CompoundTag tag = pkt.getTag();
        if (tag.getBoolean("hasLink")) {
            link = new BlockPos(tag.getInt("linkX"), tag.getInt("linkY"), tag.getInt("linkZ"));
        } else {
            link = null;
        }
        uniqueUpgrade.deserializeNBT(pRegistries, tag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, tag.getCompound("universalUpgrade"));
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
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.getBoolean("hasLink")) {
            link = new BlockPos(pTag.getInt("linkX"), pTag.getInt("linkY"), pTag.getInt("linkZ"));
        }
    }

}
