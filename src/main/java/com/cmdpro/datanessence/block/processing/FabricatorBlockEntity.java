package com.cmdpro.datanessence.block.processing;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class FabricatorBlockEntity extends EssenceContainer implements MenuProvider, GeoBlockEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 9) {
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };
    public IItemHandler getItemHandler() {
        return lazyItemHandler.get();
    }

    @Override
    public float getMaxEssence() {
        return 1000;
    }
    @Override
    public float getMaxLunarEssence() {
        return 1000;
    }
    @Override
    public float getMaxNaturalEssence() {
        return 1000;
    }
    @Override
    public float getMaxExoticEssence() {
        return 1000;
    }

    public void drops() {
        SimpleContainer inventory = getInv();

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);

    public FabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FABRICATOR.get(), pos, state);
        item = ItemStack.EMPTY;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        setEssence(tag.getFloat("essence"));
        setLunarEssence(tag.getFloat("lunarEssence"));
        setNaturalEssence(tag.getFloat("naturalEssence"));
        setExoticEssence(tag.getFloat("exoticEssence"));
        item = ItemStack.parseOptional(pRegistries, tag.getCompound("item"));
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.put("item", item.saveOptional(pRegistries));
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider pRegistries) {
        tag.put("inventory", itemHandler.serializeNBT(pRegistries));
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        super.saveAdditional(tag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        itemHandler.deserializeNBT(pRegistries, nbt.getCompound("inventory"));
        setEssence(nbt.getFloat("essence"));
        setLunarEssence(nbt.getFloat("lunarEssence"));
        setNaturalEssence(nbt.getFloat("naturalEssence"));
        setExoticEssence(nbt.getFloat("exoticEssence"));
    }
    public ItemStack item;
    public SimpleContainer getInv() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        return inventory;
    }
    public CraftingContainer getCraftingInv() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            items.add(itemHandler.getStackInSlot(i));
        }
        CraftingContainer inventory = new NonMenuCraftingContainer(items, 3, 3);
        return inventory;
    }
    public static InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                        Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof FabricatorBlockEntity ent) {
            if (ent.recipe != null) {
                if (ent.enoughEssence) {
                    IFabricationRecipe fabricationRecipe = null;
                    if (ent.recipe instanceof IFabricationRecipe) {
                        fabricationRecipe = (IFabricationRecipe)ent.recipe;
                    }
                    if (fabricationRecipe == null || DataNEssenceUtil.DataTabletUtil.playerHasEntry(pPlayer, fabricationRecipe.getEntry())) {
                        ItemStack stack = ent.recipe.assemble(ent.getCraftingInv(), pLevel.registryAccess()).copy();
                        for (int i = 0; i < 9; i++) {
                            ent.itemHandler.extractItem(i, 1, false);
                        }
                        ent.setEssence(ent.getEssence()-ent.essenceCost);
                        ent.setLunarEssence(ent.getLunarEssence()-ent.lunarEssenceCost);
                        ent.setNaturalEssence(ent.getNaturalEssence()-ent.naturalEssenceCost);
                        ent.setExoticEssence(ent.getExoticEssence()-ent.exoticEssenceCost);
                        ItemEntity entity = new ItemEntity(pLevel, (float) pPos.getX() + 0.5f, (float) pPos.getY() + 1f, (float) pPos.getZ() + 0.5f, stack);
                        pLevel.addFreshEntity(entity);
                        pLevel.playSound(null, pPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 2, 1);
                    } else {
                        pPlayer.sendSystemMessage(Component.translatable("block.datanessence.fabricator.dont_know_how"));
                    }
                } else {
                    pPlayer.sendSystemMessage(Component.translatable("block.datanessence.fabricator.not_enough_essence"));
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    public CraftingRecipe recipe;
    public boolean enoughEssence;
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FabricatorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            DataNEssenceUtil.getEssenceFromBuffersBelow(pBlockEntity);
            Optional<RecipeHolder<IFabricationRecipe>> recipe = pLevel.getRecipeManager().getRecipeFor(RecipeRegistry.FABRICATIONCRAFTING.get(), pBlockEntity.getCraftingInv(), pLevel);
            if (recipe.isPresent()) {
                pBlockEntity.recipe = recipe.get().value();
                pBlockEntity.essenceCost = recipe.get().value().getEssenceCost();
                pBlockEntity.lunarEssenceCost = recipe.get().value().getLunarEssenceCost();
                pBlockEntity.naturalEssenceCost = recipe.get().value().getNaturalEssenceCost();
                pBlockEntity.exoticEssenceCost = recipe.get().value().getExoticEssenceCost();
                pBlockEntity.item = recipe.get().value().getResultItem(pLevel.registryAccess());
                boolean enoughEssence = false;
                if (pBlockEntity.getEssence() >= pBlockEntity.essenceCost &&
                        pBlockEntity.getLunarEssence() >= pBlockEntity.lunarEssenceCost &&
                        pBlockEntity.getNaturalEssence() >= pBlockEntity.naturalEssenceCost &&
                        pBlockEntity.getExoticEssence() >= pBlockEntity.exoticEssenceCost) {
                    enoughEssence = true;
                }
                pBlockEntity.enoughEssence = enoughEssence;
            } else {
                Optional<RecipeHolder<CraftingRecipe>> recipe2 = pLevel.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pBlockEntity.getCraftingInv(), pLevel);
                if (recipe2.isPresent()) {
                    pBlockEntity.recipe = recipe2.get().value();
                    pBlockEntity.item = recipe2.get().value().getResultItem(pLevel.registryAccess());
                    pBlockEntity.enoughEssence = true;
                    pBlockEntity.essenceCost = 0;
                    pBlockEntity.lunarEssenceCost = 0;
                    pBlockEntity.naturalEssenceCost = 0;
                    pBlockEntity.exoticEssenceCost = 0;
                } else {
                    pBlockEntity.recipe = null;
                    pBlockEntity.essenceCost = 0;
                    pBlockEntity.lunarEssenceCost = 0;
                    pBlockEntity.naturalEssenceCost = 0;
                    pBlockEntity.exoticEssenceCost = 0;
                    pBlockEntity.item = ItemStack.EMPTY;
                }
            }
            pBlockEntity.updateBlock();
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        if (!item.isEmpty()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.fabricator.ready", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("animation.fabricator.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 0, this::predicate));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
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
    private static boolean hasNotReachedStackLimit(FabricatorBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(2).getCount() < entity.itemHandler.getStackInSlot(2).getMaxStackSize();
    }
}
