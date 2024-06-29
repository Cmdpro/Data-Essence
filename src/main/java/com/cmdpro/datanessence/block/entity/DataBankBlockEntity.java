package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DataBankBlockEntity extends BlockEntity {
    public DataBankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DATA_BANK.get(), pos, state);
    }
    public ResourceLocation type;
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("type", type == null ? "" : type.toString());
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        type = ResourceLocation.tryParse(tag.getString("type"));
    }
}
