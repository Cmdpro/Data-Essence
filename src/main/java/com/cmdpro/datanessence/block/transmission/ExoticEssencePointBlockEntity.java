package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;

public class ExoticEssencePointBlockEntity extends BaseEssencePointBlockEntity implements GeoBlockEntity {
    public ExoticEssencePointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.EXOTIC_ESSENCE_POINT.get(), pos, state);
        storage = new SingleEssenceContainer(EssenceTypeRegistry.EXOTIC_ESSENCE.get(), DataNEssenceConfig.essencePointTransfer);
    }

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().then("animation.essence_point.idle", Animation.LoopType.LOOP));
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
    public Color linkColor() {
        return new Color(EssenceTypeRegistry.EXOTIC_ESSENCE.get().getColor());
    }

    @Override
    public void transfer(EssenceStorage other) {
        EssenceStorage.transferEssence(storage, other, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), DataNEssenceConfig.essencePointTransfer);
    }
    @Override
    public void take(EssenceStorage other) {
        EssenceStorage.transferEssence(other, storage, EssenceTypeRegistry.EXOTIC_ESSENCE.get(), DataNEssenceConfig.essencePointTransfer);
    }
}
