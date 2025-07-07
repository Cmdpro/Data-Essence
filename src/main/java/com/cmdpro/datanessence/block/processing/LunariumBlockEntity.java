package com.cmdpro.datanessence.block.processing;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.block.BaseFabricatorBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.cmdpro.datanessence.screen.LunariumMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LunariumBlockEntity extends BaseFabricatorBlockEntity implements MenuProvider {
    public DatabankAnimationState animState = new DatabankAnimationState("pose")
            .addAnim(new DatabankAnimationReference("pose", (state, anim) -> {}, (state, anim) -> {}));
    private ClientHandler clientHandler;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public LunariumBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNARIUM.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new LunariumMenu(pContainerId, pInventory, this);
    }

    @Override
    public List<EssenceType> getSupportedEssenceTypes() {
        return List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get());
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BaseFabricatorBlockEntity baseFabricator) {
        super.tick(world, pos, state, baseFabricator);

        if (baseFabricator instanceof LunariumBlockEntity lunarium && world.isClientSide()) {
            if (lunarium.time >= 0 && lunarium.essenceCost != null) {
                if (!lunarium.clientHandler.isIndustrialSoundPlaying() && lunarium.essenceCost.containsKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()))) {
                    lunarium.clientHandler.startIndustrialSound();
                }
                if (!lunarium.clientHandler.isLunarSoundPlaying() && lunarium.essenceCost.containsKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()))) {
                    lunarium.clientHandler.startLunarSound();
                }
            } else {
                if (lunarium.clientHandler.isIndustrialSoundPlaying()) {
                    lunarium.clientHandler.stopIndustrialSound();
                }
                if (lunarium.clientHandler.isLunarSoundPlaying()) {
                    lunarium.clientHandler.stopLunarSound();
                }
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level.isClientSide) {
            clientHandler = new ClientHandler();
            clientHandler.createWorkingSounds(getBlockPos());
        } else {
            checkRecipes();
        }
    }

    @Override
    public float getMaxEssence() {
        return 2000;
    }

    private static class ClientHandler {
        public SoundInstance industrialWorkingSound;
        public SoundInstance lunarWorkingSound;

        public void createWorkingSounds(BlockPos pos) {
            industrialWorkingSound = new SimpleSoundInstance(SoundRegistry.FABRICATOR_LOOP.value(), SoundSource.BLOCKS, 0.5f, 1.0f, SoundInstance.createUnseededRandom(), pos);
            lunarWorkingSound = new SimpleSoundInstance(SoundRegistry.LUNARIUM_LOOP.value(), SoundSource.BLOCKS, 0.5f, 1.0f, SoundInstance.createUnseededRandom(), pos);
        }

        public void startIndustrialSound() {
            Minecraft.getInstance().getSoundManager().play(industrialWorkingSound);
        }
        public void startLunarSound() {
            Minecraft.getInstance().getSoundManager().play(lunarWorkingSound);
        }
        public void stopIndustrialSound() {
            Minecraft.getInstance().getSoundManager().stop(industrialWorkingSound);
        }
        public void stopLunarSound() {
            Minecraft.getInstance().getSoundManager().stop(lunarWorkingSound);
        }
        public boolean isIndustrialSoundPlaying() {
            return Minecraft.getInstance().getSoundManager().isActive(industrialWorkingSound);
        }
        public boolean isLunarSoundPlaying() {
            return Minecraft.getInstance().getSoundManager().isActive(lunarWorkingSound);
        }
    }
}
