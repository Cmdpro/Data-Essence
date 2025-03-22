package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class DryingRecipe implements Recipe<RecipeInputWithFluid> {
    private final FluidStack input;
    private final ItemStack output;
    private final Optional<Ingredient> additive;
    private final int time;

    public DryingRecipe(FluidStack input, ItemStack output, Optional<Ingredient> additive, int time) {
        this.input = input;
        this.output = output;
        this.additive = additive;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public FluidStack getInput() {
        return input;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        additive.ifPresent(list::add);
        return list;
    }

    @Override
    public boolean matches(RecipeInputWithFluid input, Level level) {
        return (FluidStack.isSameFluidSameComponents(this.input, input.getFluid(0)) && (this.additive.isEmpty() || additive.get().test(input.getItem(0))));
    }

    @Override
    public ItemStack assemble(RecipeInputWithFluid input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DRYING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<DryingRecipe> {
        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStack.CODEC.fieldOf("input").forGetter(r -> r.input),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.optionalFieldOf("additive").forGetter(r -> r.additive),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time)
        ).apply(instance, DryingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    FluidStack.STREAM_CODEC.encode(buf, obj.input);
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    buf.writeBoolean(obj.additive.isPresent());
                    obj.additive.ifPresent(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient));
                    buf.writeInt(obj.time);
                },

                (buf) -> {
                    FluidStack input = FluidStack.STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    boolean additivePresent = buf.readBoolean();
                    Optional<Ingredient> additive = Optional.empty();
                    if (additivePresent) {
                        additive = Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    int time = buf.readInt();
                    return new DryingRecipe(input, result, additive, time);
                }
        );

        public static final Serializer INSTANCE = new DryingRecipe.Serializer();

        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
