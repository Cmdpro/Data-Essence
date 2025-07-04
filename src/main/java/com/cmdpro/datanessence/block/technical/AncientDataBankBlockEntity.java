package com.cmdpro.datanessence.block.technical;

import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AncientDataBankBlockEntity extends BlockEntity {
    public AncientDataBankBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ANCIENT_DATA_BANK.get(), pos, state);
    }
    public ResourceLocation type;
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putString("type", type == null ? "" : type.toString());
    }
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        type = ResourceLocation.tryParse(tag.getString("type"));
    }
}
