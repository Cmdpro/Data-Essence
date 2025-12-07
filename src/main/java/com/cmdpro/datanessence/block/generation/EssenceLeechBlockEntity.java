package com.cmdpro.datanessence.block.generation;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.block.processing.FabricatorBlockEntity;
import com.cmdpro.datanessence.client.ClientEvents;
import com.cmdpro.datanessence.client.FactorySong;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.DamageTypeRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class EssenceLeechBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }
    public int cooldown;

    public EssenceLeechBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ESSENCE_LEECH.get(), pos, state);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("cooldown", cooldown);
        tag.put("EssenceStorage", storage.toNbt());
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        cooldown = nbt.getInt("cooldown");
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EssenceLeechBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            if (pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) < pBlockEntity.getStorage().getMaxEssence() && pBlockEntity.cooldown <= 0) {
                for (LivingEntity i : pLevel.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pPos.getCenter().add(0, 2, 0), 3, 3, 3))) {
                    if (i.isAlive()) {
                        i.hurt(i.damageSources().source(DamageTypeRegistry.essenceSiphoned), 5);
                        if (!i.isAlive()) {
                            pBlockEntity.getStorage().addEssence(EssenceTypeRegistry.ESSENCE.get(), Math.clamp(i.getMaxHealth() * 5, 0f, 100f));
                        }
                    }
                }
                pBlockEntity.cooldown = 10;
            } else {
                pBlockEntity.cooldown -= 1;
            }
        } else {
            ClientHandler.markFactorySong(pPos);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    private static class ClientHandler {
        static FactorySong.FactoryLoop workingSound = FactorySong.getLoop(SoundRegistry.LEECH_LOOP.value());

        public static void markFactorySong(BlockPos pos) {
            workingSound.addSource(pos);
        }
    }
}
