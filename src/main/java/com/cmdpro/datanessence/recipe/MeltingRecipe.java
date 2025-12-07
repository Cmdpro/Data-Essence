package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class MeltingRecipe implements Recipe<RecipeInput>, DataNEssenceRecipe {
    private final FluidStack output;
    private final Ingredient input;
    private final int time;

    public MeltingRecipe(FluidStack output,
                         Ingredient input, int time) {
        this.output = output;
        this.input = input;
        this.time = time;
    }
    public int getTime() {
        return time;
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }
    @Override
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput pContainer, HolderLookup.Provider pRegistryAccess) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    public FluidStack getOutput() {
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.MELTING_TYPE.get();
    }

    @Override
    public ResourceLocation getMachineEntry() {
        return DataNEssence.locate("machinery/melter");
    }

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {
        public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time)
        ).apply(instance, MeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    FluidStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    buf.writeInt(obj.time);
                },
                (buf) -> {
                    FluidStack output = FluidStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int time = buf.readInt();
                    return new MeltingRecipe(output, input, time);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<MeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}