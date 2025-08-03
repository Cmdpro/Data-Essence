package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.item.EssenceShard;
import com.cmdpro.datanessence.block.transmission.ItemFilterBlockEntity;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EssenceReaderBlockEntity extends BlockEntity {
    public EssenceReaderBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.ESSENCE_READER.get(), pPos, pBlockState);
    }
    public boolean selectFromItem(ItemStack stack) {
        if (stack == null) {
            selected = null;
            updateBlock();
            return true;
        } else {
            if (stack.getItem() instanceof EssenceShard shard) {
                selected = shard.essence.keySet().stream().map(Supplier::get).collect(Collectors.toSet());
                updateBlock();
                return true;
            }
        }
        return false;
    }
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof EssenceShard shard) {
            return true;
        }
        return false;
    }
    public Color getColor() {
        if (selected == null || selected.isEmpty()) {
            return Color.BLACK;
        }
        Color color = new Color(selected.stream().findFirst().orElseThrow().color);
        for (EssenceType i : selected) {
            Color newColor = new Color(i.color);
            if (!newColor.equals(color)) {
                color = ColorUtil.blendColors(color, newColor, 0.5f);
            }
        }
        return color;
    }
    public Set<EssenceType> getTypesToCheck(EssenceStorage storage) {
        if (selected != null) {
            return selected;
        }
        return storage.getSupportedEssenceTypes();
    }
    public Set<EssenceType> selected;
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, Block.UPDATE_ALL);
        this.setChanged();
    }
    private void updateNeighbors() {
        if (!hasLevel()) {
            return;
        }
        getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    }
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pLevel.isClientSide) {
            int newSignal = updateSignal();
            boolean update = newSignal != signal;
            signal = newSignal;
            if (update) {
                updateNeighbors();
            }
        }
    }
    private int signal;
    public int getSignal() {
        return signal;
    }
    private int updateSignal() {
        Direction detectDir = getBlockState().getValue(EssenceReader.FACING).getOpposite();
        BlockPos detectingPos = getBlockPos().relative(detectDir);
        if (level.getBlockEntity(detectingPos) instanceof EssenceBlockEntity essenceEntity) {
            EssenceStorage storage = essenceEntity.getStorage();
            float essence = 0;
            float maxEssence = 0;
            Set<EssenceType> typesToCheck = getTypesToCheck(storage);
            for (EssenceType i : typesToCheck) {
                essence += storage.getEssence(i);
                maxEssence += storage.getMaxEssence();
            }
            float amount = essence / maxEssence;
            return (int) Math.floor(amount * 15f);
        }
        return 0;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        handleData(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        handleData(tag);
    }

    public void handleData(CompoundTag tag) {
        if (tag.contains("selected")) {
            ListTag selected = tag.getList("selected", CompoundTag.TAG_STRING);
            List<EssenceType> types = new ArrayList<>();
            for (Tag i : selected) {
                StringTag str = (StringTag)i;
                types.add(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(ResourceLocation.tryParse(str.getAsString())));
            }
            this.selected = new HashSet<>(types);
        } else {
            this.selected = null;
        }
        updateBlock();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        if (selected != null) {
            ListTag list = new ListTag();
            for (EssenceType i : selected) {
                ResourceLocation location = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(i);
                if (location != null) {
                    list.add(StringTag.valueOf(location.toString()));
                }
            }
            tag.put("selected", list);
        }
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        if (selected != null) {
            ListTag list = new ListTag();
            for (EssenceType i : selected) {
                ResourceLocation location = DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(i);
                if (location != null) {
                    list.add(StringTag.valueOf(location.toString()));
                }
            }
            tag.put("selected", list);
        }
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        if (nbt.contains("selected")) {
            ListTag selected = nbt.getList("selected", CompoundTag.TAG_STRING);
            List<EssenceType> types = new ArrayList<>();
            for (Tag i : selected) {
                StringTag str = (StringTag)i;
                types.add(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(ResourceLocation.tryParse(str.getAsString())));
            }
            this.selected = new HashSet<>(types);
        } else {
            this.selected = null;
        }
    }
}
