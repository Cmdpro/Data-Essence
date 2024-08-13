package com.cmdpro.datanessence.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

import java.awt.*;

public abstract class BaseCapabilityPointBlockEntity extends BlockEntity {
    public BlockPos link;

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
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseCapabilityPointBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BlockEntity ent = pLevel.getBlockEntity(pPos.relative(pBlockEntity.getDirection().getOpposite()));
            if (ent != null) {
                if (pBlockEntity.link == null) {
                    pBlockEntity.transfer(ent);
                } else {
                    pBlockEntity.take(ent);
                }
            }
            if (pBlockEntity.link != null) {
                BlockEntity ent2 = pLevel.getBlockEntity(pBlockEntity.link);
                if (ent2 != null) {
                    pBlockEntity.transfer(ent2);
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
