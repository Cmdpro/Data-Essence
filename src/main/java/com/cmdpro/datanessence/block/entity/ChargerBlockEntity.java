package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.recipe.IFabricationRecipe;
import com.cmdpro.datanessence.recipe.NonMenuCraftingContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.cmdpro.datanessence.screen.ChargerMenu;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
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

import static com.cmdpro.datanessence.api.DataNEssenceUtil.ItemUtil.EssenceChargeableItemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChargerBlockEntity extends EssenceContainer implements MenuProvider, GeoBlockEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
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

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.CHARGER.get(), pos, state);
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
        charging = tag.getBoolean("charging");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("essence", getEssence());
        tag.putFloat("lunarEssence", getLunarEssence());
        tag.putFloat("naturalEssence", getNaturalEssence());
        tag.putFloat("exoticEssence", getExoticEssence());
        tag.put("item", item.saveOptional(pRegistries));
        tag.putBoolean("charging", charging);
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
    public float essenceCost;
    public float lunarEssenceCost;
    public float naturalEssenceCost;
    public float exoticEssenceCost;
    public boolean charging;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ChargerBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            pBlockEntity.charging = false;
            DataNEssenceUtil.getEssenceFromBuffersBelow(pBlockEntity);
            ItemStack stack = pBlockEntity.itemHandler.getStackInSlot(0).copy();
            pBlockEntity.item = stack;
            float maxEssence = EssenceChargeableItemUtil.getMaxEssence(stack);
            if (maxEssence > 0) {
                float essence = EssenceChargeableItemUtil.getEssence(stack);
                if (essence < maxEssence) {
                    float fill = Math.min(maxEssence-essence, Math.min(5f, pBlockEntity.getEssence()));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillEssence(stack, fill);
                        pBlockEntity.setEssence(pBlockEntity.getEssence() - fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            float maxLunarEssence = EssenceChargeableItemUtil.getMaxLunarEssence(stack);
            if (maxLunarEssence > 0) {
                float essence = EssenceChargeableItemUtil.getLunarEssence(stack);
                if (essence < maxLunarEssence) {
                    float fill = Math.min(maxLunarEssence-essence, Math.min(5f, pBlockEntity.getLunarEssence()));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillLunarEssence(stack, fill);
                        pBlockEntity.setLunarEssence(pBlockEntity.getLunarEssence() - fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            float maxNaturalEssence = EssenceChargeableItemUtil.getMaxNaturalEssence(stack);
            if (maxNaturalEssence > 0) {
                float essence = EssenceChargeableItemUtil.getNaturalEssence(stack);
                if (essence < maxNaturalEssence) {
                    float fill = Math.min(maxNaturalEssence-essence, Math.min(5f, pBlockEntity.getNaturalEssence()));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillNaturalEssence(stack, fill);
                        pBlockEntity.setNaturalEssence(pBlockEntity.getNaturalEssence() - fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            float maxExoticEssence = EssenceChargeableItemUtil.getMaxExoticEssence(stack);
            if (maxExoticEssence > 0) {
                float essence = EssenceChargeableItemUtil.getExoticEssence(stack);
                if (essence < maxExoticEssence) {
                    float fill = Math.min(maxExoticEssence-essence, Math.min(5f, pBlockEntity.getExoticEssence()));
                    if (fill > 0) {
                        EssenceChargeableItemUtil.fillExoticEssence(stack, fill);
                        pBlockEntity.setExoticEssence(pBlockEntity.getExoticEssence()-fill);
                        pBlockEntity.charging = true;
                    }
                }
            }
            pBlockEntity.itemHandler.setStackInSlot(0, stack);
            pBlockEntity.updateBlock();
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    private RawAnimation orb_spin = RawAnimation.begin().then("animation.charger.orb_spin", Animation.LoopType.LOOP);
    private RawAnimation orb_rise = RawAnimation.begin().then("animation.charger.orb_rise", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private RawAnimation orb_fall = RawAnimation.begin().then("animation.charger.orb_fall", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private RawAnimation idle_exciters_out = RawAnimation.begin().then("animation.charger.idle_exciters_out", Animation.LoopType.LOOP);
    private RawAnimation extend_exciters = RawAnimation.begin().then("animation.charger.extend_exciters", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private RawAnimation retract_exciters = RawAnimation.begin().then("animation.charger.retract_exciters", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private RawAnimation idle_empty = RawAnimation.begin().then("animation.charger.idle_empty", Animation.LoopType.LOOP);
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        if (!item.isEmpty()) {
            if (event.isCurrentAnimation(idle_empty)) {
                event.setAnimation(extend_exciters);
            }
            if (event.isCurrentAnimation(extend_exciters) && event.getController().getAnimationState().equals(AnimationController.State.PAUSED)) {
                event.setAnimation(idle_exciters_out);
            }
            if (charging) {
                if (event.isCurrentAnimation(idle_exciters_out)) {
                    event.setAnimation(orb_rise);
                }
                if (event.isCurrentAnimation(orb_rise) && event.getController().getAnimationState().equals(AnimationController.State.PAUSED)) {
                    event.setAnimation(orb_spin);
                }
            } else {
                if (event.isCurrentAnimation(orb_spin) || event.isCurrentAnimation(orb_rise)) {
                    event.setAnimation(orb_fall);
                }
                if (event.isCurrentAnimation(orb_fall) && event.getController().getAnimationState().equals(AnimationController.State.PAUSED)) {
                    event.setAnimation(idle_exciters_out);
                }
            }
        } else {
            if (event.isCurrentAnimation(orb_spin) || event.isCurrentAnimation(orb_rise)) {
                event.setAnimation(orb_fall);
            }
            if (event.isCurrentAnimation(idle_exciters_out) || event.isCurrentAnimation(extend_exciters) || (event.isCurrentAnimation(orb_fall) && event.getController().getAnimationState().equals(AnimationController.State.PAUSED))) {
                event.setAnimation(retract_exciters);
            }
            if ((!event.isCurrentAnimation(retract_exciters) && !event.isCurrentAnimation(orb_fall)) || event.getController().getAnimationState().equals(AnimationController.State.PAUSED)) {
                event.getController().setAnimation(idle_empty);
            }
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
        return new ChargerMenu(pContainerId, pInventory, this);
    }
}
