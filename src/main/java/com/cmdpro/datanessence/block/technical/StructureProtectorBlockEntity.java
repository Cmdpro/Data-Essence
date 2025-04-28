package com.cmdpro.datanessence.block.technical;

import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StructureProtectorBlockEntity extends BlockEntity {
    public StructureProtectorBlockEntity(BlockPos pos, BlockState state) {
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
            if (!level.isClientSide) {
                level.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).remove(this);
            }
        }
        super.setLevel(pLevel);
        if (!pLevel.isClientSide) {
            if (!pLevel.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).contains(this)) {
                pLevel.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).add(this);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null) {
            if (!level.isClientSide) {
                level.getData(AttachmentTypeRegistry.STRUCTURE_CONTROLLERS).remove(this);
            }
        }
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StructureProtectorBlockEntity pBlockEntity) {
        if (pLevel.isClientSide) {
            ClientHandler.spawnParticles(pPos.getCenter());
        }
    }
    private static class ClientHandler {
        public static void spawnParticles(Vec3 pos) {
            for (int i = 0; i < 5; i++) {
                Vec3 dir = new Vec3(RandomUtils.nextFloat(0f, 2f) - 1f, RandomUtils.nextFloat(0f, 2f) - 1f, RandomUtils.nextFloat(0f, 2f) - 1f).normalize().multiply(0.1f, 0.1f, 0.1f);
                Vec3 offset = new Vec3(RandomUtils.nextFloat(0f, 2f) - 1f, RandomUtils.nextFloat(0f, 2f) - 1f, RandomUtils.nextFloat(0f, 2f) - 1f).normalize().multiply(0.1f, 0.1f, 0.1f);
                Minecraft.getInstance().particleEngine.createParticle(new CircleParticleOptions().setColor(Color.getHSBColor((float) (Minecraft.getInstance().level.getGameTime() % 100) / 100f, 1f, 1f)).setAdditive(true), pos.x + offset.x, pos.y + offset.y, pos.z + offset.z, dir.x, dir.y, dir.z);
            }
        }
    }
}
