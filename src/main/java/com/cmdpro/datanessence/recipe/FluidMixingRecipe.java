package com.cmdpro.datanessence.recipe;

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

import java.util.Optional;

public class FluidMixingRecipe implements Recipe<RecipeInputWithFluid>, IHasRequiredKnowledge {
    private final FluidStack output;
    private final FluidStack input;
    private final Optional<FluidStack> input2;
    private final Optional<Ingredient> input3;
    private final int time;
    private final float essenceCost;
    private final ResourceLocation entry;
    private final boolean allowIncomplete;
    private final boolean showIncompleteInEMI;

    public FluidMixingRecipe(FluidStack output,
                             FluidStack input, Optional<FluidStack> input2, Optional<Ingredient> input3, int time, float essenceCost, ResourceLocation entry, boolean allowIncomplete, boolean showIncompleteInEMI) {
        this.output = output;
        this.input = input;
        this.input2 = input2;
        this.input3 = input3;
        this.time = time;
        this.essenceCost = essenceCost;
        this.entry = entry;
        this.allowIncomplete = allowIncomplete;
        this.showIncompleteInEMI = showIncompleteInEMI;
    }
    @Override
    public boolean allowIncomplete() {
        return allowIncomplete;
    }

    @Override
    public boolean showIncompleteInEMI() {
        return showIncompleteInEMI;
    }

    public float getEssenceCost() {
        return essenceCost;
    }
    public int getTime() {
        return time;
    }
    public FluidStack getInput1() {
        return input;
    }
    public FluidStack getInput2() {
        return input2.orElse(FluidStack.EMPTY);
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        input3.ifPresent(list::add);
        return list;
    }
    @Override
    public boolean matches(RecipeInputWithFluid pContainer, Level pLevel) {
        if (FluidStack.isSameFluidSameComponents(input, pContainer.getFluid(0)) && (input2.isEmpty() || FluidStack.isSameFluidSameComponents(input2.get(), pContainer.getFluid(1))) && (input3.isEmpty() || input3.get().test(pContainer.getItem(0)))) {
            return true;
        }
        return FluidStack.isSameFluidSameComponents(input, pContainer.getFluid(1)) && (input2.isEmpty() || FluidStack.isSameFluidSameComponents(input2.get(), pContainer.getFluid(0))) && (input3.isEmpty() || input3.get().test(pContainer.getItem(0)));
    }

    @Override
    public ItemStack assemble(RecipeInputWithFluid pContainer, HolderLookup.Provider pRegistryAccess) {
        return ItemStack.EMPTY;
    }
    public FluidStack getOutput() {
        return output;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
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
        return RecipeRegistry.FLUID_MIXING_TYPE.get();
    }

    @Override
    public ResourceLocation getEntry() {
        return entry;
    }

    public static class Serializer implements RecipeSerializer<FluidMixingRecipe> {
        public static final MapCodec<FluidMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                FluidStack.CODEC.fieldOf("fluid_input").forGetter(r -> r.input),
                FluidStack.CODEC.optionalFieldOf("fluid_input_2").forGetter(r -> r.input2),
                Ingredient.CODEC.optionalFieldOf("item_input").forGetter(r -> r.input3),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.BOOL.optionalFieldOf("allow_incomplete", false).forGetter((r) -> r.allowIncomplete),
                Codec.BOOL.optionalFieldOf("show_incomplete_in_emi", false).forGetter((r) -> r.showIncompleteInEMI)
        ).apply(instance, FluidMixingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    FluidStack.STREAM_CODEC.encode(buf, obj.output);
                    FluidStack.STREAM_CODEC.encode(buf, obj.input);
                    buf.writeBoolean(obj.input2.isPresent());
                    obj.input2.ifPresent(fluidIngredient -> FluidStack.STREAM_CODEC.encode(buf, fluidIngredient));
                    buf.writeBoolean(obj.input3.isPresent());
                    obj.input3.ifPresent(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient));
                    buf.writeInt(obj.time);
                    buf.writeFloat(obj.essenceCost);
                    buf.writeResourceLocation(obj.entry);
                    buf.writeBoolean(obj.allowIncomplete);
                    buf.writeBoolean(obj.showIncompleteInEMI);
                },
                (buf) -> {
                    FluidStack output = FluidStack.STREAM_CODEC.decode(buf);
                    FluidStack input = FluidStack.STREAM_CODEC.decode(buf);
                    boolean input2Exists = buf.readBoolean();
                    Optional<FluidStack> input2 = Optional.empty();
                    if (input2Exists) {
                        input2 = Optional.of(FluidStack.STREAM_CODEC.decode(buf));
                    }
                    boolean input3Exists = buf.readBoolean();
                    Optional<Ingredient> input3 = Optional.empty();
                    if (input3Exists) {
                        input3 = Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    int time = buf.readInt();
                    float essenceCost = buf.readFloat();
                    ResourceLocation entry = buf.readResourceLocation();
                    boolean allowIncomplete = buf.readBoolean();
                    boolean showIncompleteInEMI = buf.readBoolean();
                    return new FluidMixingRecipe(output, input, input2, input3, time, essenceCost, entry, allowIncomplete, showIncompleteInEMI);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<FluidMixingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}