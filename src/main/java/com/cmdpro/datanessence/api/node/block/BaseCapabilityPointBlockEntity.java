package com.cmdpro.datanessence.api.node.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.node.NodePathEnd;
import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import com.cmdpro.datanessence.api.node.CapabilityNodePath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCapabilityPointBlockEntity extends BlockEntity {
    public List<BlockPos> link = new ArrayList<>();
    public List<BlockPos> linkFrom = new ArrayList<>();
    public CapabilityNodePath path;
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
            Object modified = upgrade.getValue(universalUpgrade.getStackInSlot(0), id, value, this);
            if (modified != null) {
                value = (T)modified;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            Object modified = upgrade.getValue(uniqueUpgrade.getStackInSlot(0), id, value, this);
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

    @Override
    public void onLoad() {
        super.onLoad();
        path = CapabilityNodePath.calculate(this);
    }
    public abstract Color linkColor();
    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        ListTag list = new ListTag();
        for (BlockPos i : link) {
            CompoundTag blockpos = new CompoundTag();
            blockpos.putInt("linkX", i.getX());
            blockpos.putInt("linkY", i.getY());
            blockpos.putInt("linkZ", i.getZ());
            list.add(blockpos);
        }
        pTag.put("link", list);
        ListTag list2 = new ListTag();
        for (BlockPos i : linkFrom) {
            CompoundTag blockpos = new CompoundTag();
            blockpos.putInt("linkX", i.getX());
            blockpos.putInt("linkY", i.getY());
            blockpos.putInt("linkZ", i.getZ());
            list2.add(blockpos);
        }
        pTag.put("linkFrom", list2);
        pTag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        pTag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseCapabilityPointBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            if (pBlockEntity.linkFrom.isEmpty()) {
                List<NodePathEnd> ends = Arrays.stream(pBlockEntity.path.ends).toList();
                pBlockEntity.preTransferHooks(pBlockEntity, ends);
                pBlockEntity.transfer(pBlockEntity, ends);
                pBlockEntity.postTransferHooks(pBlockEntity, ends);
            }
        }
    }
    public boolean preTransferHooks(BlockEntity from, List<NodePathEnd> other) {
        boolean cancel = false;
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(universalUpgrade.getStackInSlot(0), from, other, cancel)) {
                cancel = true;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(uniqueUpgrade.getStackInSlot(0), from, other, cancel)) {
                cancel = true;
            }
        }
        return cancel;
    }
    public void postTransferHooks(BlockEntity from, List<NodePathEnd> other) {
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(universalUpgrade.getStackInSlot(0), from, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(uniqueUpgrade.getStackInSlot(0), from, other);
        }
    }
    public abstract void transfer(BaseCapabilityPointBlockEntity from, List<NodePathEnd> other);
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
        ListTag list = (ListTag)tag.get("link");
        link.clear();
        for (Tag i : list) {
            CompoundTag blockpos = (CompoundTag)i;
            link.add(new BlockPos(blockpos.getInt("linkX"), blockpos.getInt("linkY"), blockpos.getInt("linkZ")));
        }
        uniqueUpgrade.deserializeNBT(pRegistries, tag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, tag.getCompound("universalUpgrade"));
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (BlockPos i : link) {
            CompoundTag blockpos = new CompoundTag();
            blockpos.putInt("linkX", i.getX());
            blockpos.putInt("linkY", i.getY());
            blockpos.putInt("linkZ", i.getZ());
            list.add(blockpos);
        }
        tag.put("link", list);
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
        link.clear();
        if (pTag.contains("link")) {
            ListTag list = (ListTag) pTag.get("link");
            for (Tag i : list) {
                CompoundTag blockpos = (CompoundTag) i;
                link.add(new BlockPos(blockpos.getInt("linkX"), blockpos.getInt("linkY"), blockpos.getInt("linkZ")));
            }
        }
        linkFrom.clear();
        if (pTag.contains("linkFrom")) {
            ListTag list2 = (ListTag) pTag.get("linkFrom");
            for (Tag i : list2) {
                CompoundTag blockpos = (CompoundTag) i;
                linkFrom.add(new BlockPos(blockpos.getInt("linkX"), blockpos.getInt("linkY"), blockpos.getInt("linkZ")));
            }
        }
        uniqueUpgrade.deserializeNBT(pRegistries, pTag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, pTag.getCompound("universalUpgrade"));
    }

}
