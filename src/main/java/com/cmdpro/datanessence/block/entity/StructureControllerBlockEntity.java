package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.block.FluidCollector;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class StructureControllerBlockEntity extends BlockEntity {
    public StructureControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.STRUCTURE_PROTECTOR.get(), pos, state);
        offsetCorner1 = pos;
        offsetCorner2 = pos;
        bindProcess = 0;
    }

    public BlockPos offsetCorner1;
    public BlockPos offsetCorner2;
    public int bindProcess;
    @Override
    public void setLevel(Level pLevel) {
        if (level != null) {
            level.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).remove(this);
        }
        super.setLevel(pLevel);
        pLevel.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).add(this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.putInt("offsetCorner1X", offsetCorner1.getX());
        tag.putInt("offsetCorner1Y", offsetCorner1.getY());
        tag.putInt("offsetCorner1Z", offsetCorner1.getZ());
        tag.putInt("offsetCorner2X", offsetCorner2.getX());
        tag.putInt("offsetCorner2Y", offsetCorner2.getY());
        tag.putInt("offsetCorner2Z", offsetCorner2.getZ());
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        offsetCorner1 = new BlockPos(nbt.getInt("offsetCorner1X"), nbt.getInt("offsetCorner1Y"), nbt.getInt("offsetCorner1Z"));
        offsetCorner2 = new BlockPos(nbt.getInt("offsetCorner2X"), nbt.getInt("offsetCorner2Y"), nbt.getInt("offsetCorner2Z"));
    }
}
