package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DataBankBlockEntity extends BlockEntity {
    public DataBankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DATA_BANK.get(), pos, state);
    }
    public List<ResourceLocation> data = new ArrayList<>();
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (!this.data.isEmpty()) {
            ListTag data = new ListTag();
            for (ResourceLocation i : this.data) {
                data.add(StringTag.valueOf(i.toString()));
            }
            tag.put("data", data);
        }
    }
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        if (tag.contains("data")) {
            ListTag data = tag.getList("data", StringTag.TAG_STRING);
            this.data.clear();
            for (Tag i : data) {
                this.data.add(ResourceLocation.tryParse(i.getAsString()));
            }
        }
    }
}
