package com.cmdpro.datanessence.fluid;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.vfx.PlayGenderfluidTransitionEffect;
import com.cmdpro.datanessence.recipe.GenderfluidTransitionRecipe;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Map;
import java.util.Optional;

public class CooledWater extends ModFluidType {
    @Override
    public FluidType createFluidType(ModFluidRegistryObject obj) {
        return new CooledWaterType();
    }

    @Override
    public FlowingFluid createSourceFluid(ModFluidRegistryObject obj) {
        return new CooledWaterFluid.Source(obj);
    }

    @Override
    public FlowingFluid createFlowingFluid(ModFluidRegistryObject obj) {
        return new CooledWaterFluid.Flowing(obj);
    }

    @Override
    public LiquidBlock createBlock(ModFluidRegistryObject obj) {
        return new CooledWaterBlock(obj);
    }

    @Override
    public Item createBucket(ModFluidRegistryObject obj) {
        return new BucketItem(obj.source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    }

    @Override
    public boolean hasBucket() {
        return true;
    }

    public static class CooledWaterType extends FluidType {
        public CooledWaterType() {
            super(Properties.create()
                    .descriptionId("block.datanessence.cooled_water")
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
        }
    }

    public abstract static class CooledWaterFluid {
        public static final IClientFluidTypeExtensions EXTENSIONS = new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return DataNEssence.locate("block/fluid/cooled_water_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return DataNEssence.locate("block/fluid/cooled_water_flowing");
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

    public static class CooledWaterBlock extends LiquidBlock {
        public CooledWaterBlock(ModFluidRegistryObject obj) {
            super(obj.source.get(), Properties.ofFullCopy(Blocks.WATER).mapColor(DyeColor.BLACK));
        }
    }
}
