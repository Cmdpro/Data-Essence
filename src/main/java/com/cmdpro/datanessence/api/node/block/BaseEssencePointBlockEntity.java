package com.cmdpro.datanessence.api.node.block;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseEssencePointBlockEntity extends BlockEntity {
    public AnimationState animState = new AnimationState();
    public List<BlockPos> link;
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
        return value*getValue(DataNEssence.locate("speed_multiplier"), 1f);
    }
    public int getFinalSpeed(int value) {
        return (int)(value*getValue(DataNEssence.locate("speed_multiplier"), 1f));
    }

    public BaseEssencePointBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            if (!level.isClientSide) {
                BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
                if (!networks.graph.containsVertex(getBlockPos())) {
                    networks.graph.addVertex(getBlockPos());
                }
                updateBlock();
            }
        }
    }
    public abstract Color linkColor();
    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        pTag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
        ListTag list = new ListTag();
        if (link != null) {
            for (BlockPos target : link) {
                CompoundTag blockpos = new CompoundTag();
                blockpos.putInt("linkX", target.getX());
                blockpos.putInt("linkY", target.getY());
                blockpos.putInt("linkZ", target.getZ());
                list.add(blockpos);
            }
        }
        pTag.put("link", list);
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseEssencePointBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            if (pBlockEntity.link == null) {
                pBlockEntity.updateLinks();
            }
            BlockPosNetworks networks = pLevel.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
            Set<DefaultEdge> edges = networks.graph.edgesOf(pPos);
            if (edges.stream().noneMatch((edge) -> networks.graph.getEdgeTarget(edge).equals(pPos)) && !edges.isEmpty()) {
                ShortestPathAlgorithm.SingleSourcePaths<BlockPos, DefaultEdge> paths = networks.path.getPaths(pPos);
                List<GraphPath<BlockPos, DefaultEdge>> ends = networks.graph.vertexSet().stream().filter((vertex) -> pLevel.isLoaded(vertex) && networks.graph.edgesOf(vertex).stream().noneMatch((edge) -> networks.graph.getEdgeSource(edge).equals(vertex)) && paths.getPath(vertex) != null).map(paths::getPath).toList();
                pBlockEntity.preTransferHooks(pBlockEntity, ends);
                pBlockEntity.transfer(pBlockEntity, ends);
                pBlockEntity.postTransferHooks(pBlockEntity, ends);
            }
        } else {
            if (pBlockEntity.link == null) {
                pBlockEntity.link = new ArrayList<>();
            }
        }
    }
    public void updateLinks() {
        if (link == null) {
            link = new ArrayList<>();
        }
        link.clear();
        BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
        if (networks.graph.containsVertex(getBlockPos())) {
            for (DefaultEdge i : networks.graph.edgesOf(getBlockPos())) {
                if (networks.graph.getEdgeSource(i).equals(getBlockPos())) {
                    BlockPos target = networks.graph.getEdgeTarget(i);
                    link.add(target);
                }
            }
        }
    }
    public boolean preTransferHooks(BlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {
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
    public void postTransferHooks(BlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other) {
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(universalUpgrade.getStackInSlot(0), from, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(uniqueUpgrade.getStackInSlot(0), from, other);
        }
    }
    public abstract void transfer(BaseEssencePointBlockEntity from, List<GraphPath<BlockPos, DefaultEdge>> other);
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
        if (link == null) {
            link = new ArrayList<>();
        }
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
        if (link != null) {
            for (BlockPos target : link) {
                CompoundTag blockpos = new CompoundTag();
                blockpos.putInt("linkX", target.getX());
                blockpos.putInt("linkY", target.getY());
                blockpos.putInt("linkZ", target.getZ());
                list.add(blockpos);
            }
        }
        tag.put("link", list);
        tag.put("link", list);
        tag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        tag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
        return tag;
    }
    public void updateBlock() {
        updateLinks();
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        uniqueUpgrade.deserializeNBT(pRegistries, pTag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, pTag.getCompound("universalUpgrade"));
        ListTag list = (ListTag)pTag.get("link");
        if (link == null) {
            link = new ArrayList<>();
        }
        link.clear();
        for (Tag i : list) {
            CompoundTag blockpos = (CompoundTag)i;
            link.add(new BlockPos(blockpos.getInt("linkX"), blockpos.getInt("linkY"), blockpos.getInt("linkZ")));
        }
    }

}
