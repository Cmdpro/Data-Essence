package com.cmdpro.datanessence.block.entity;

import com.cmdpro.datanessence.api.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.DataNEssenceUtil;
import com.cmdpro.datanessence.api.EssenceContainer;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;

public class NaturalEssencePointBlockEntity extends BaseEssencePointBlockEntity implements GeoBlockEntity {
    public NaturalEssencePointBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.NATURALESSENCEPOINT.get(), pos, state);
    }

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private <E extends GeoAnimatable> PlayState predicate(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().then("animation.essencepoint.idle", Animation.LoopType.LOOP));
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
    public void transfer(EssenceContainer other) {
        DataNEssenceUtil.transferNaturalEssence(this, other, DataNEssenceConfig.essencePointTransfer);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("naturalEssence", getNaturalEssence());
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        setNaturalEssence(nbt.getFloat("naturalEssence"));
    }
    @Override
    public Color linkColor() {
        return new Color(0x57f36c);
    }

    @Override
    public float getMaxNaturalEssence() {
        return DataNEssenceConfig.essencePointTransfer;
    }

    @Override
    public void take(EssenceContainer other) {
        DataNEssenceUtil.transferNaturalEssence(other, this, DataNEssenceConfig.essencePointTransfer);
    }
}
