package com.cmdpro.datanessence.block.processing;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.block.Machine;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.recipe.EntropicProcessingRecipe;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.screen.EntropicProcessorMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntropicProcessorBlockEntity extends BlockEntity implements MenuProvider, EssenceBlockEntity, Machine {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("working", (state, anim) -> {}, (state, anim) -> {}));

    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            checkRecipes();
        }
    };
    private final ItemStackHandler outputItemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }
    public IItemHandler getOutputHandler() {
        return outputItemHandler;
    }
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(itemHandler, outputItemHandler);
    public CombinedInvWrapper getCombinedInvWrapper() {
        return combinedInvWrapper;
    }
    public EntropicProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ENTROPIC_PROCESSOR.get(), pos, state);
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

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        workTime = tag.getInt("workTime");
        maxTime = tag.getInt("maxTime");
        if (tag.contains("item")) {
            item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
        } else {
            item = null;
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("workTime", workTime);
        tag.putInt("maxTime", recipe == null ? -1 : recipe.getTime());
        if (item != null) {
            tag.put("item", item.saveOptional(pRegistries));
        }
        return tag;
    }
    public ItemStack item;

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("input", itemHandler.serializeNBT(pRegistries));
        tag.put("output", outputItemHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("workTime", workTime);
        super.saveAdditional(tag, pRegistries);
    }
    public float maxTime;
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("input"));
        outputItemHandler.deserializeNBT(pRegistries, nbt.getCompound("output"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        workTime = nbt.getInt("workTime");
    }
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots()+outputItemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < outputItemHandler.getSlots(); i++) {
            inventory.setItem(i+itemHandler.getSlots(), outputItemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public RecipeInput getCraftingInv() {
        RecipeInput inventory = new SingleRecipeInput(itemHandler.getStackInSlot(0));
        return inventory;
    }
    public void checkRecipes() {
        Optional<RecipeHolder<EntropicProcessingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ENTROPIC_PROCESSING_TYPE.get(), getCraftingInv(), level);
        if (recipe.isPresent()) {
            if (!recipe.get().value().equals(this.recipe))
                workTime = 0;

            this.recipe = recipe.get().value();
        } else {
            this.recipe = null;
        }
    }
    public int workTime;
    public EntropicProcessingRecipe recipe;
    public float essenceCost;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EntropicProcessorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, EssenceTypeRegistry.ESSENCE.get());
            BufferUtil.getItemsFromBuffersBelow(pBlockEntity, pBlockEntity.itemHandler);
            boolean resetWorkTime = true;
            if (pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 1) {
                if (pBlockEntity.recipe != null) {
                    ItemStack assembled = pBlockEntity.recipe.assemble(pBlockEntity.getCraftingInv(), pLevel.registryAccess());
                    if (pBlockEntity.outputItemHandler.insertItem(0, assembled, true).isEmpty()) {
                        for (LivingEntity victim : pLevel.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pPos.getCenter(), 0.9f, 0.9f, 0.9f))) {
                            victim.hurt(victim.damageSources().source(DamageTypeRegistry.crushed), 2f);
                        }
                        resetWorkTime = false;
                        pBlockEntity.workTime++;
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), 1);

                        if (pBlockEntity.workTime >= pBlockEntity.recipe.getTime()) {
                            pBlockEntity.outputItemHandler.insertItem(0, assembled, false);
                            pBlockEntity.itemHandler.extractItem(0, 1, false);
                            pBlockEntity.workTime = 0;
                        }
                    }
                }
            }
            if (resetWorkTime) {
                pBlockEntity.workTime = -1;
            }
            pBlockEntity.item = pBlockEntity.itemHandler.getStackInSlot(0);
            pBlockEntity.updateBlock();
        } else {
            if (pBlockEntity.workTime >= 0) {
                if (!pBlockEntity.clientHandler.isSoundPlaying()) {
                    pBlockEntity.clientHandler.startSound();
                }
                if (pBlockEntity.item != null && !pBlockEntity.item.isEmpty()) {
                    Vec3 particlePos = pPos.getCenter().add(0, 0.25, 0);
                    Vec3 particleSpeed = Vec3.directionFromRotation(0, pLevel.getRandom().nextIntBetweenInclusive(-180, 180)).scale(0.1).add(0, 0.25, 0);
                    pLevel.addParticle(new ItemParticleOption(ParticleTypes.ITEM, pBlockEntity.item), particlePos.x, particlePos.y, particlePos.z, particleSpeed.x, particleSpeed.y, particleSpeed.z);
                }
            } else {
                if (pBlockEntity.clientHandler.isSoundPlaying()) {
                    pBlockEntity.clientHandler.stopSound();
                }
            }
        }
    }
    private ClientHandler clientHandler;
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
        return new EntropicProcessorMenu(pContainerId, pInventory, this);
    }
    private static boolean hasNotReachedStackLimit(EntropicProcessorBlockEntity entity, ItemStack toAdd) {
        if (toAdd.is(entity.outputItemHandler.getStackInSlot(0).getItem())) {
            return entity.outputItemHandler.getStackInSlot(0).getCount() + toAdd.getCount() <= entity.outputItemHandler.getStackInSlot(0).getMaxStackSize();
        } else return entity.outputItemHandler.getStackInSlot(0).isEmpty();
    }
    private static class ClientHandler {
        public SoundInstance workingSound;
        public void createWorkingSound(BlockPos pos) {
            workingSound = new SimpleSoundInstance(SoundRegistry.ENTROPIC_PROCESSOR_WORKING.value(), SoundSource.BLOCKS, 0.5f, 1.25f, SoundInstance.createUnseededRandom(), pos);
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

    @Override
    public List<Direction> getValidInputDirections() {
        return List.of(Direction.DOWN, Direction.UP);
    }
}
