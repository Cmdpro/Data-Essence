package com.cmdpro.datanessence.block.generation;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.TagRegistry;
import com.cmdpro.datanessence.screen.IndustrialPlantSiphonMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class IndustrialPlantSiphonBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity {
    public AnimationState animState = new AnimationState();
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
    public static float essenceProducedFromLowValuePlants = 2.0f;
    public static float essenceProducedFromMediumValuePlants = 4.0f;
    public static float essenceProducedFromHighValuePlants = 8.0f;
    public float essenceGenerationTicks; // fuel timer
    public float generationRate; // current generation rate

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return stack.is(TagRegistry.Items.LOW_ESSENCE_PLANTS) || stack.is(TagRegistry.Items.MEDIUM_ESSENCE_PLANTS) || stack.is(TagRegistry.Items.HIGH_ESSENCE_PLANTS);
            }
            return super.isItemValid(slot, stack);
        }
    };

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    

    public IndustrialPlantSiphonBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.INDUSTRIAL_PLANT_SIPHON.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        essenceGenerationTicks = tag.getFloat("EssenceGenerationTicks");
        generationRate = tag.getFloat("GenerationRate");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putFloat("EssenceGenerationTicks", essenceGenerationTicks);
        tag.putFloat("GenerationRate", generationRate);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putFloat("EssenceGenerationTicks", essenceGenerationTicks);
        tag.putFloat("GenerationRate", generationRate);
        super.saveAdditional(tag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        essenceGenerationTicks = nbt.getFloat("EssenceGenerationTicks");
        generationRate = nbt.getFloat("GenerationRate");
    }

    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, IndustrialPlantSiphonBlockEntity tile) {
        if (!world.isClientSide()) {
            BufferUtil.getItemsFromBuffersBelow(tile, tile.itemHandler);
            if (tile.essenceGenerationTicks <= 0 ) {
                tile.essenceGenerationTicks = tile.getGenerationTicks(tile);
                tile.generationRate = tile.getEssenceProduced(tile);
                tile.itemHandler.extractItem(0, 1, false);
            }

            if (tile.essenceGenerationTicks > 0) {
                if ( !(tile.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) + tile.generationRate > tile.storage.getMaxEssence()) ) {
                    tile.storage.addEssence(EssenceTypeRegistry.ESSENCE.get(), tile.generationRate);
                    tile.essenceGenerationTicks--;
                }
            }
            tile.updateBlock();
        }
    }

    public float getGenerationTicks(IndustrialPlantSiphonBlockEntity tile) {
        if (tile.itemHandler.getStackInSlot(0).is(TagRegistry.Items.HIGH_ESSENCE_PLANTS))
            return DataNEssenceConfig.highValuePlantEssence;
        if (tile.itemHandler.getStackInSlot(0).is(TagRegistry.Items.MEDIUM_ESSENCE_PLANTS))
            return DataNEssenceConfig.mediumValuePlantEssence;
        if (tile.itemHandler.getStackInSlot(0).is(TagRegistry.Items.LOW_ESSENCE_PLANTS))
            return DataNEssenceConfig.lowValuePlantEssence;
        return 0f;
    }

    public float getEssenceProduced(IndustrialPlantSiphonBlockEntity tile) {
        if (tile.itemHandler.getStackInSlot(0).is(TagRegistry.Items.HIGH_ESSENCE_PLANTS))
            return essenceProducedFromHighValuePlants;
        if (tile.itemHandler.getStackInSlot(0).is(TagRegistry.Items.MEDIUM_ESSENCE_PLANTS))
            return essenceProducedFromMediumValuePlants;
        if (tile.itemHandler.getStackInSlot(0).is(TagRegistry.Items.LOW_ESSENCE_PLANTS))
            return essenceProducedFromLowValuePlants;
        return 0f;
    }

    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new IndustrialPlantSiphonMenu(pContainerId, pInventory, this);
    }
}
