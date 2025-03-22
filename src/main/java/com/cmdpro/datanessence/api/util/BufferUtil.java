package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.block.transmission.EssenceBufferBlockEntity;
import com.cmdpro.datanessence.block.transmission.FluidBufferBlockEntity;
import com.cmdpro.datanessence.block.transmission.ItemBufferBlockEntity;
import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.PlayBufferTransferParticle;
import com.cmdpro.datanessence.registry.TagRegistry;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Math;

import java.awt.*;

public class BufferUtil {
    public static void getEssenceFromBuffersBelow(BlockEntity container, EssenceType type) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof EssenceBufferBlockEntity buffer) {
                var transferredAmount = EssenceStorage.transferEssence(buffer.getStorage(), ((EssenceBlockEntity)container).getStorage(), type, buffer.getStorage().getMaxEssence());
                if (transferredAmount > 0f)
                    PacketDistributor.sendToPlayersNear((ServerLevel) buffer.getLevel(), null, buffer.getBlockPos().getX(), buffer.getBlockPos().getY(), buffer.getBlockPos().getZ(), 16d, new PlayBufferTransferParticle(buffer.getBlockPos(), new Color(0xE236EF)));
            }
            if (!container.getLevel().getBlockState(container.getBlockPos().offset(0, -i, 0)).is(TagRegistry.Blocks.BUFFER_DETECTION_PASS)) {
                break;
            }
        }
    }
    public static void getEssenceFromBuffersBelow(BlockEntity container, Iterable<EssenceType> types) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof EssenceBufferBlockEntity buffer) {
                for (EssenceType o : types) {
                    var transferredAmount = EssenceStorage.transferEssence(buffer.getStorage(), ((EssenceBlockEntity)container).getStorage(), o, buffer.getStorage().getMaxEssence());
                    if (transferredAmount > 0f)
                        PacketDistributor.sendToPlayersNear((ServerLevel) buffer.getLevel(), null, buffer.getBlockPos().getX(), buffer.getBlockPos().getY(), buffer.getBlockPos().getZ(), 16d, new PlayBufferTransferParticle(buffer.getBlockPos(), new Color(0xE236EF)));
                }
            }
            if (!container.getLevel().getBlockState(container.getBlockPos().offset(0, -i, 0)).is(TagRegistry.Blocks.BUFFER_DETECTION_PASS)) {
                break;
            }
        }
    }

    public static void getItemsFromBuffersBelow(BlockEntity container) {
        BufferUtil.getItemsFromBuffersBelow(container, container.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, container.getBlockPos(), Direction.DOWN));
    }

    public static void getItemsFromBuffersBelow(BlockEntity container, IItemHandler handler) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof ItemBufferBlockEntity buffer) {
                var transferredSomething = buffer.transfer(handler);
                if (transferredSomething)
                    PacketDistributor.sendToPlayersNear((ServerLevel) buffer.getLevel(), null, buffer.getBlockPos().getX(), buffer.getBlockPos().getY(), buffer.getBlockPos().getZ(), 16d, new PlayBufferTransferParticle(buffer.getBlockPos(), new Color(0xEF4D3D)));
            }
            if (!container.getLevel().getBlockState(container.getBlockPos().offset(0, -i, 0)).is(TagRegistry.Blocks.BUFFER_DETECTION_PASS)) {
                break;
            }
        }
    }

    public static void getFluidsFromBuffersBelow(BlockEntity container) {
        BufferUtil.getFluidsFromBuffersBelow(container, container.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, container.getBlockPos(), Direction.DOWN));
    }

    public static void getFluidsFromBuffersBelow(BlockEntity container, IFluidHandler handler) {
        for (int i = 1; i <= 5; i++) {
            BlockEntity ent = container.getLevel().getBlockEntity(container.getBlockPos().offset(0, -i, 0));
            if (ent instanceof FluidBufferBlockEntity buffer) {
                var transferredSomething = buffer.transfer(handler);
                if (transferredSomething)
                    PacketDistributor.sendToPlayersNear((ServerLevel) buffer.getLevel(), null, buffer.getBlockPos().getX(), buffer.getBlockPos().getY(), buffer.getBlockPos().getZ(), 16d, new PlayBufferTransferParticle(buffer.getBlockPos(), new Color(0x56BAE9)));
            }
            if (!container.getLevel().getBlockState(container.getBlockPos().offset(0, -i, 0)).is(TagRegistry.Blocks.BUFFER_DETECTION_PASS)) {
                break;
            }
        }
    }
}
