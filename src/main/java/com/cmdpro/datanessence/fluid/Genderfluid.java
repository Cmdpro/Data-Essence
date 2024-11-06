package com.cmdpro.datanessence.fluid;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.BlockRegistry;
import com.cmdpro.datanessence.registry.FluidRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class Genderfluid {
    public static final IClientFluidTypeExtensions EXTENSIONS = new IClientFluidTypeExtensions() {
        @Override
        public ResourceLocation getStillTexture() {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "block/fluid/genderfluid_still");
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "block/fluid/genderfluid_flowing");
        }
    };
    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(FluidRegistry.GENDERFLUID_TYPE, FluidRegistry.GENDERFLUID, FluidRegistry.GENDERFLUID_FLOWING)
                .block(BlockRegistry.GENDERFLUID).bucket(ItemRegistry.GENDERFLUID_BUCKET);
    }
    public static class Flowing extends BaseFlowingFluid.Flowing {
        public Flowing() {
            super(properties());
        }
    }

    public static class Source extends BaseFlowingFluid.Source {
        public Source() {
            super(properties());
        }
    }
}
