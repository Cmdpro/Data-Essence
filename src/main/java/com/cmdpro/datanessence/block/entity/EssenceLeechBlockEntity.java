package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EssenceLeechBlockEntity extends EssenceContainer {
    public EssenceLeechBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ESSENCE_LEECH.get(), pos, state);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putFloat("essence", getEssence());
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        setEssence(nbt.getFloat("essence"));
    }
    @Override
    public float getMaxEssence() {
        return 1000;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EssenceLeechBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            if (pBlockEntity.getEssence() < pBlockEntity.getMaxEssence()) {
                for (LivingEntity i : pLevel.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pPos.getCenter().add(0, 2, 0), 3, 3, 3))) {
                    if (i.isAlive()) {
                        i.hurt(i.damageSources().source(DataNEssence.essenceSiphoned), 5);
                        if (!i.isAlive()) {
                            pBlockEntity.setEssence(Math.clamp(pBlockEntity.getEssence() + 20, 0, pBlockEntity.getMaxEssence()));
                        }
                    }
                }
            }
        }
    }
}
