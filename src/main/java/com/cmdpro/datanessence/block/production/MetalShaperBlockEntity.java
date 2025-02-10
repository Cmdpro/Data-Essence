package com.cmdpro.datanessence.block.production;

import com.cmdpro.datanessence.api.LockableItemHandler;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.api.misc.ILockableContainer;
import com.cmdpro.datanessence.api.util.BufferUtil;
import com.cmdpro.datanessence.recipe.MetalShaperRecipe;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.MetalShaperMenu;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MetalShaperBlockEntity extends BlockEntity implements MenuProvider, ILockableContainer, EssenceBlockEntity {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
    public AnimationState animState = new AnimationState();
    public AnimationDefinition anim;
    @Override
    public EssenceStorage getStorage() {
        return storage;
    }
    private final LockableItemHandler itemHandler = new LockableItemHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public void setLockedSlots() {
            super.setLockedSlots();
            setChanged();
        }

        @Override
        public void setLocked(boolean locked) {
            super.setLocked(locked);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return super.isItemValid(slot, stack);
        }
    };
    private final ItemStackHandler moldHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.has(DataComponentRegistry.MOLD);
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
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
    private Lazy<IItemHandler> lazyMoldHandler = Lazy.of(() -> moldHandler);
    private Lazy<IItemHandler> lazyOutputHandler = Lazy.of(() -> outputItemHandler);
    private Lazy<IItemHandler> lazyCombinedHandler = Lazy.of(() -> new CombinedInvWrapper(itemHandler, outputItemHandler, moldHandler));

    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }
    public IItemHandler getMoldHandler() {
        return lazyMoldHandler.get();
    }
    public IItemHandler getOutputHandler() {
        return lazyOutputHandler.get();
    }
    public IItemHandler getCombinedHandler() {
        return lazyCombinedHandler.get();
    }

    public MetalShaperBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.METAL_SHAPER.get(), pos, state);
        item = ItemStack.EMPTY;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
        workTime = tag.getInt("workTime");
        itemHandler.deserializeNBT(pRegistries, tag.getCompound("itemHandler"));
        maxWorkTime = tag.getInt("maxWorkTime");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putInt("workTime", workTime);
        tag.putInt("maxWorkTime", recipe == null ? -1 : recipe.getTime());
        tag.put("itemHandler", itemHandler.serializeNBT(pRegistries));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.put("inventoryOutput", outputItemHandler.serializeNBT(pRegistries));
        tag.put("inventoryDrive", moldHandler.serializeNBT(pRegistries));
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("workTime", workTime);
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        outputItemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryOutput"));
        moldHandler.deserializeNBT(pRegistries, nbt.getCompound("inventoryDrive"));
        storage.fromNbt(nbt.getCompound("EssenceStorage"));
        workTime = nbt.getInt("workTime");
    }
    public ItemStack item;
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots()+outputItemHandler.getSlots()+moldHandler.getSlots());
        for (int i = 0; i < moldHandler.getSlots(); i++) {
            inventory.setItem(i, moldHandler.getStackInSlot(i));
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i+moldHandler.getSlots(), itemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < outputItemHandler.getSlots(); i++) {
            inventory.setItem(i+moldHandler.getSlots()+itemHandler.getSlots(), outputItemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public RecipeInput getCraftingInv() {
        RecipeInput inventory = new RecipeInput() {
            @Override
            public ItemStack getItem(int pIndex) {
                return itemHandler.getStackInSlot(pIndex);
            }

            @Override
            public int size() {
                return 1;
            }
        };
        return inventory;
    }
    public MetalShaperRecipe recipe;
    public int workTime;
    public int maxWorkTime;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MetalShaperBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BufferUtil.getEssenceFromBuffersBelow(pBlockEntity, EssenceTypeRegistry.ESSENCE.get());
            BufferUtil.getItemsFromBuffersBelow(pBlockEntity);
            boolean resetWorkTime = true;
            List<RecipeHolder<MetalShaperRecipe>> recipes = pLevel.getRecipeManager().getRecipesFor(RecipeRegistry.METAL_SHAPING_TYPE.get(), pBlockEntity.getCraftingInv(), pLevel);
            RecipeHolder<MetalShaperRecipe> recipe = null;
            ResourceLocation moldRecipe = null;
            if (!pBlockEntity.moldHandler.getStackInSlot(0).isEmpty()) {
                moldRecipe = pBlockEntity.moldHandler.getStackInSlot(0).get(DataComponentRegistry.MOLD);
            }
            for (RecipeHolder<MetalShaperRecipe> i : recipes) {
                if (i.id().equals(moldRecipe)) {
                    recipe = i;
                }
            }
            if (recipe != null) {
                if (!recipe.value().equals(pBlockEntity.recipe)) {
                    pBlockEntity.workTime = 0;
                }
                if (pBlockEntity.getStorage().getEssence(EssenceTypeRegistry.ESSENCE.get()) > 0) {
                    if (!recipe.value().equals(pBlockEntity.recipe)) {
                        pBlockEntity.workTime = 0;
                    }
                    pBlockEntity.recipe = recipe.value();
                    ItemStack assembled = recipe.value().assemble(pBlockEntity.getCraftingInv(), pLevel.registryAccess());
                    if (pBlockEntity.outputItemHandler.insertItem(0, assembled, true).isEmpty()) {
                        resetWorkTime = false;
                        // working vfx
                        if (pLevel.random.nextInt() % 20 == 0) {
                            pLevel.playSound(null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.75f);
                            ((ServerLevel) pLevel).sendParticles(ParticleTypes.SMOKE, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, 5, 0, 0, 0, 0);
                        }

                        pBlockEntity.workTime++;
                        pBlockEntity.getStorage().removeEssence(EssenceTypeRegistry.ESSENCE.get(), 1);
                        if (pBlockEntity.workTime >= recipe.value().getTime()) {
                            pBlockEntity.outputItemHandler.insertItem(0, assembled, false);
                            pBlockEntity.itemHandler.extractItem(0, 1, false);
                            pBlockEntity.workTime = 0;
                        }
                    }
                } else {
                    pBlockEntity.recipe = null;
                }
            } else {
                pBlockEntity.recipe = null;
            }
            if (resetWorkTime) {
                pBlockEntity.workTime = -1;
            }
            pBlockEntity.updateBlock();
        }
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
        return new MetalShaperMenu(pContainerId, pInventory, this);
    }

    @Override
    public List<LockableItemHandler> getLockable() {
        return List.of(itemHandler);
    }
}
