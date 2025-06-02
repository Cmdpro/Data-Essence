package com.cmdpro.datanessence.api.pearlnetwork;

import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

public class PearlNetworkBlockEntity extends BlockEntity {
    public PearlNetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public List<BlockPos> link;
    public void updateLinks() {
        if (link == null) {
            link = new ArrayList<>();
        }
        link.clear();
        BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.CAPABILITY_NODE_NETWORKS);
        if (networks.graph.containsVertex(getBlockPos())) {
            for (DefaultEdge i : networks.graph.edgesOf(getBlockPos())) {
                if (networks.graph.getEdgeSource(i).equals(getBlockPos())) {
                    BlockPos target = networks.graph.getEdgeTarget(i);
                    link.add(target);
                }
            }
        }
    }
    public void updateBlock() {
        updateLinks();
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
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
        return tag;
    }
    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
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
    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
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
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            if (!level.isClientSide) {
                BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ENDER_PEARL_NETWORKS);
                if (!networks.graph.containsVertex(getBlockPos())) {
                    networks.graph.addVertex(getBlockPos());
                }
            }
        }
    }
}
