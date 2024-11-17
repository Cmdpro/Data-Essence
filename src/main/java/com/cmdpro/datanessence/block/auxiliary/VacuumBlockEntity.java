package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class VacuumBlockEntity extends BlockEntity {
    public VacuumBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.VACUUM.get(), pPos, pBlockState);
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VacuumBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            List<ItemEntity> ents = pLevel.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(pPos.getCenter(), 20, 20, 20));
            for (ItemEntity i : ents) {
                i.setDeltaMovement(pPos.getCenter().subtract(i.position()).normalize().multiply(0.2, 0.2, 0.2));
                i.hasImpulse = true;
            }
            List<ItemEntity> ents2 = pLevel.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(pPos.getCenter(), 3, 3, 3));
            IItemHandler itemHandler = pLevel.getCapability(Capabilities.ItemHandler.BLOCK, pPos.above(), Direction.DOWN);
            if (itemHandler != null) {
                for (ItemEntity i : ents2) {
                    ItemStack copy = i.getItem().copy();
                    if (!copy.isEmpty()) {
                        for (int o = 0; o < itemHandler.getSlots(); o++) {
                            ItemStack stack = itemHandler.getStackInSlot(o);
                            if (stack.is(copy.getItem()) || stack.isEmpty()) {
                                copy = itemHandler.insertItem(o, copy, false);
                            }
                        }
                    }
                    i.setItem(copy);
                }
            }
        } else {
            Color[] particleColors = new Color[] { new Color(0x005c3c), new Color(0x033328), new Color(0x006653) };
            Vec3 pos1 = pPos.getCenter().add(pLevel.random.nextFloat()-0.5f, pLevel.random.nextFloat()-0.5f, pLevel.random.nextFloat()-0.5f);
            Vec3 vel1 = pPos.getCenter().subtract(pos1).multiply(0.2f, 0.2f, 0.2f);
            Vec3 pos2 = pPos.getCenter().add(pLevel.random.nextFloat()-0.5f, pLevel.random.nextFloat()-0.5f, pLevel.random.nextFloat()-0.5f);
            Vec3 vel2 = pPos.getCenter().subtract(pos2).multiply(0.2f, 0.2f, 0.2f);
            Color color1 = particleColors[pLevel.random.nextIntBetweenInclusive(0, particleColors.length-1)];
            Color color2 = particleColors[pLevel.random.nextIntBetweenInclusive(0, particleColors.length-1)];
            pLevel.addParticle(new RhombusParticleOptions(color1), pos1.x, pos1.y, pos1.z, vel1.x, vel1.y, vel1.z);
            pLevel.addParticle(new SmallCircleParticleOptions(color2), pos2.x, pos2.y, pos2.z, vel2.x, vel2.y, vel2.z);
        }
    }
}
