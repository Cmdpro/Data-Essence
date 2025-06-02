package com.cmdpro.datanessence.block.technical;

import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
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
    public Rotation getRotation(Direction direction) {
        Rotation rot = Rotation.NONE;
        if (direction.equals(Direction.EAST)) {
            rot = Rotation.CLOCKWISE_90;
        }
        if (direction.equals(Direction.SOUTH)) {
            rot = Rotation.CLOCKWISE_180;
        }
        if (direction.equals(Direction.WEST)) {
            rot = Rotation.COUNTERCLOCKWISE_90;
        }
        return rot;
    }
    public Rotation getRotation() {
        return getRotation(getBlockState().getValue(StructureProtector.FACING));
    }
    public BlockPos getCorner1() {
        if (offsetCorner1 == null) {
            return null;
        }
        return getBlockPos().offset(offsetCorner1.rotate(getRotation()));
    }
    public BlockPos getCorner2() {
        if (offsetCorner2 == null) {
            return null;
        }
        return getBlockPos().offset(offsetCorner2.rotate(getRotation()));
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        CompoundTag tag = pkt.getTag();
        if (tag.contains("offsetCorner1X") && tag.contains("offsetCorner1Y") && tag.contains("offsetCorner1Z")) {
            offsetCorner1 = new BlockPos(tag.getInt("offsetCorner1X"), tag.getInt("offsetCorner1Y"), tag.getInt("offsetCorner1Z"));
        } else {
            offsetCorner1 = null;
        }
        if (tag.contains("offsetCorner2X") && tag.contains("offsetCorner2Y") && tag.contains("offsetCorner2Z")) {
            offsetCorner2 = new BlockPos(tag.getInt("offsetCorner2X"), tag.getInt("offsetCorner2Y"), tag.getInt("offsetCorner2Z"));
        } else {
            offsetCorner2 = null;
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        if (offsetCorner1 != null) {
            tag.putInt("offsetCorner1X", offsetCorner1.getX());
            tag.putInt("offsetCorner1Y", offsetCorner1.getY());
            tag.putInt("offsetCorner1Z", offsetCorner1.getZ());
        }
        if (offsetCorner2 != null) {
            tag.putInt("offsetCorner2X", offsetCorner2.getX());
            tag.putInt("offsetCorner2Y", offsetCorner2.getY());
            tag.putInt("offsetCorner2Z", offsetCorner2.getZ());
        }
        return tag;
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        if (offsetCorner1 != null) {
            tag.putInt("offsetCorner1X", offsetCorner1.getX());
            tag.putInt("offsetCorner1Y", offsetCorner1.getY());
            tag.putInt("offsetCorner1Z", offsetCorner1.getZ());
        }
        if (offsetCorner2 != null) {
            tag.putInt("offsetCorner2X", offsetCorner2.getX());
            tag.putInt("offsetCorner2Y", offsetCorner2.getY());
            tag.putInt("offsetCorner2Z", offsetCorner2.getZ());
        }
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        if (tag.contains("offsetCorner1X") && tag.contains("offsetCorner1Y") && tag.contains("offsetCorner1Z")) {
            offsetCorner1 = new BlockPos(tag.getInt("offsetCorner1X"), tag.getInt("offsetCorner1Y"), tag.getInt("offsetCorner1Z"));
        } else {
            offsetCorner1 = null;
        }
        if (tag.contains("offsetCorner2X") && tag.contains("offsetCorner2Y") && tag.contains("offsetCorner2Z")) {
            offsetCorner2 = new BlockPos(tag.getInt("offsetCorner2X"), tag.getInt("offsetCorner2Y"), tag.getInt("offsetCorner2Z"));
        } else {
            offsetCorner2 = null;
        }
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StructureProtectorBlockEntity pBlockEntity) {
        if (pLevel.isClientSide) {
            ClientHandler.spawnParticles(pLevel.random, pPos.getCenter());
        }
    }
    public void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    private static class ClientHandler {
        public static void spawnParticles(RandomSource random, Vec3 pos) {
            for (int i = 0; i < 5; i++) {
                Vec3 dir = new Vec3(Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f)).normalize().multiply(0.1f, 0.1f, 0.1f);
                Vec3 offset = new Vec3(Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f), Mth.nextFloat(random, -1f, 1f)).normalize().multiply(0.1f, 0.1f, 0.1f);
                Minecraft.getInstance().particleEngine.createParticle(new CircleParticleOptions().setColor(Color.getHSBColor((float) (Minecraft.getInstance().level.getGameTime() % 100) / 100f, 1f, 1f)).setAdditive(true), pos.x + offset.x, pos.y + offset.y, pos.z + offset.z, dir.x, dir.y, dir.z);
            }
        }
    }
}
