package com.cmdpro.datanessence.fluid;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.block.SpreadingPlant;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.vfx.PlayGenderfluidTransitionEffect;
import com.cmdpro.datanessence.recipe.GenderfluidTransitionRecipe;
import com.cmdpro.datanessence.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Map;
import java.util.Optional;

public class Genderfluid extends ModFluidType {
    @Override
    public FluidType createFluidType(ModFluidRegistryObject obj) {
        return new GenderfluidType();
    }

    @Override
    public FlowingFluid createSourceFluid(ModFluidRegistryObject obj) {
        return new GenderfluidFluid.Source(obj);
    }

    @Override
    public FlowingFluid createFlowingFluid(ModFluidRegistryObject obj) {
        return new GenderfluidFluid.Flowing(obj);
    }

    @Override
    public LiquidBlock createBlock(ModFluidRegistryObject obj) {
        return new GenderfluidBlock(obj);
    }

    @Override
    public Item createBucket(ModFluidRegistryObject obj) {
        return new BucketItem(obj.source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    }

    @Override
    public boolean hasBucket() {
        return true;
    }

    public static class GenderfluidType extends FluidType {
        public GenderfluidType() {
            super(Properties.create()
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

    public abstract static class GenderfluidFluid {
        public static final IClientFluidTypeExtensions EXTENSIONS = new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return DataNEssence.locate("block/fluid/genderfluid_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return DataNEssence.locate("block/fluid/genderfluid_flowing");
            }
        };
        private static BaseFlowingFluid.Properties properties(ModFluidRegistryObject obj) {
            return new BaseFlowingFluid.Properties(obj.type, obj.source, obj.flowing)
                    .block(obj.block).bucket(obj.bucket);
        }
        public static class Flowing extends BaseFlowingFluid.Flowing {
            public Flowing(ModFluidRegistryObject obj) {
                super(properties(obj));
            }
        }

        public static class Source extends BaseFlowingFluid.Source {
            public Source(ModFluidRegistryObject obj) {
                super(properties(obj));
            }
        }
    }

    public static class GenderfluidBlock extends LiquidBlock {
        public GenderfluidBlock(ModFluidRegistryObject obj) {
            super(obj.source.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(DyeColor.BLACK));
        }

        @Override
        protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
            super.entityInside(pState, pLevel, pPos, pEntity);
            if (pEntity instanceof LivingEntity ent) {
                ent.addEffect(new MobEffectInstance(MobEffectRegistry.GENDER_EUPHORIA, 20*5));
            }
        }

        @Override
        protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
            // in a volume of 9x3x9 centered around the fluid (as if you were hydrating farmland with it),
            // grow SpreadingPlants somewhat often.
            for (BlockPos queryPos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
                BlockState queryState = world.getBlockState(queryPos);

                if (queryState.getBlock() instanceof SpreadingPlant plant) {
                    plant.grow(queryState, world, queryPos, world.getRandom(), 3);
                }
            }
        }

        @Override
        protected boolean isRandomlyTicking(BlockState pState) {
            return true;
        }
    }
}
