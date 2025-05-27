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

public class GenderfluidTransitionRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final Ingredient input;
    private final boolean mergeComponents;

    public GenderfluidTransitionRecipe(ItemStack output,
                                       Ingredient input, boolean mergeComponents) {
        this.output = output;
        this.input = input;
        this.mergeComponents = mergeComponents;
    }
    public boolean getMergeComponents() {
        return mergeComponents;
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
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.GENDERFLUID_TRANSITION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GenderfluidTransitionRecipe> {
        public static final MapCodec<GenderfluidTransitionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Codec.BOOL.optionalFieldOf("mergeComponents", true).forGetter(r -> r.mergeComponents)
        ).apply(instance, GenderfluidTransitionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GenderfluidTransitionRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    buf.writeBoolean(obj.mergeComponents);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    boolean mergeComponents = buf.readBoolean();
                    return new GenderfluidTransitionRecipe(output, input, mergeComponents);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<GenderfluidTransitionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GenderfluidTransitionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}