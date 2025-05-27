package com.cmdpro.datanessence.fluid;

import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.client.particle.RhombusParticleOptions;
import com.cmdpro.datanessence.client.particle.SmallCircleParticleOptions;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.PlayGenderfluidTransitionEffect;
import com.cmdpro.datanessence.recipe.GenderfluidTransitionRecipe;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.awt.*;
import java.util.Map;
import java.util.Optional;

public class GenderfluidType extends FluidType {
    public GenderfluidType() {
        super(FluidType.Properties.create()
                .descriptionId("block.datanessence.genderfluid")
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        super.setItemMovement(entity);
        Level level = entity.level();
        if (!level.isClientSide) {
            RecipeInput input = new SingleRecipeInput(entity.getItem());
            Optional<RecipeHolder<GenderfluidTransitionRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.GENDERFLUID_TRANSITION_TYPE.get(), input, level);
            if (recipe.isPresent()) {
                ItemStack result = recipe.get().value().assemble(input, level.registryAccess());
                ItemStack entityItem = entity.getItem();
                DataComponentPatch resultComponents = result.getComponentsPatch();
                DataComponentPatch entityComponents = entityItem.getComponentsPatch();
                ItemStack finalItem = new ItemStack(result.getItem());
                finalItem.setCount(result.getCount() * entityItem.getCount());
                if (recipe.get().value().getMergeComponents()) {
                    copyComponents(entityComponents, finalItem);
                }
                copyComponents(resultComponents, finalItem);
                entity.setItem(finalItem);
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0.5, entity.getDeltaMovement().z);
                ModMessages.sendToPlayersNear(new PlayGenderfluidTransitionEffect(entity.blockPosition(), entity.position()), (ServerLevel)level, entity.blockPosition().getCenter(), 128);
            }
        }
    }
    private static <T> void copyComponents(DataComponentPatch from, ItemStack to) {
        for (Map.Entry<DataComponentType<?>, Optional<?>> i : from.entrySet()) {
            if (i.getValue().isPresent()) {
                copyComponent(i.getKey(), from, to);
            }
        }
    }
    private static <T> void copyComponent(DataComponentType<T> type, DataComponentPatch from, ItemStack to) {
        Optional<? extends T> value = from.get(type);
        if (value != null && value.isPresent()) {
            to.set(type, value.get());
        }
    }
}
