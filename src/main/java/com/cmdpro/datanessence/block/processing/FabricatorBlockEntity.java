package com.cmdpro.datanessence.block.processing;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.block.BaseFabricatorBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FabricatorBlockEntity extends BaseFabricatorBlockEntity implements MenuProvider {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("ready", (state, anim) -> {}, (state, anim) -> {}));
    private ClientHandler clientHandler;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }
    public FabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FABRICATOR.get(), pos, state);
    }
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FabricatorMenu(pContainerId, pInventory, this);
    }

    @Override
    public List<EssenceType> getSupportedEssenceTypes() {
        return List.of(EssenceTypeRegistry.ESSENCE.get());
    }

    @Override
    public float getMaxEssence() {
        return 1000;
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BaseFabricatorBlockEntity baseFabricator) {
        super.tick(world, pos, state, baseFabricator);

        if (baseFabricator instanceof FabricatorBlockEntity fabricator && world.isClientSide()) {
            if (fabricator.time >= 0) {
                if (!fabricator.clientHandler.isSoundPlaying()) {
                    fabricator.clientHandler.startSound();
                }
            } else {
                if (fabricator.clientHandler.isSoundPlaying()) {
                    fabricator.clientHandler.stopSound();
                }
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level.isClientSide) {
            clientHandler = new ClientHandler();
            clientHandler.createWorkingSound(getBlockPos());
        } else {
            checkRecipes();
        }
    }

    private static class ClientHandler {
        public SoundInstance workingSound;
        public void createWorkingSound(BlockPos pos) {
            workingSound = new SimpleSoundInstance(SoundRegistry.FABRICATOR_LOOP.value(), SoundSource.BLOCKS, 0.5f, 1.0f, SoundInstance.createUnseededRandom(), pos);
        }
        public void startSound() {
            Minecraft.getInstance().getSoundManager().play(workingSound);
        }
        public void stopSound() {
            Minecraft.getInstance().getSoundManager().stop(workingSound);
        }
        public boolean isSoundPlaying() {
            return Minecraft.getInstance().getSoundManager().isActive(workingSound);
        }
    }
}
