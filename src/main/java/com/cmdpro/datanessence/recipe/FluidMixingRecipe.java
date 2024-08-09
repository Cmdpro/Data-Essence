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
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class FluidMixingRecipe implements Recipe<RecipeInputWithFluid>, IHasEssenceCost, IHasRequiredKnowledge {
    private final FluidStack output;
    private final FluidIngredient input;
    private final FluidIngredient input2;
    private final Ingredient input3;
    private final int time;
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;
    private final ResourceLocation entry;

    public FluidMixingRecipe(FluidStack output,
                             FluidIngredient input, FluidIngredient input2, Ingredient input3, int time, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost, ResourceLocation entry) {
        this.output = output;
        this.input = input;
        this.input2 = input2;
        this.input3 = input3;
        this.time = time;
        this.essenceCost = essenceCost;
        this.lunarEssenceCost = lunarEssenceCost;
        this.naturalEssenceCost = naturalEssenceCost;
        this.exoticEssenceCost = exoticEssenceCost;
        this.entry = entry;
    }
    @Override
    public float getEssenceCost() {
        return essenceCost;
    }

    @Override
    public float getLunarEssenceCost() {
        return lunarEssenceCost;
    }
    @Override
    public float getNaturalEssenceCost() {
        return naturalEssenceCost;
    }
    @Override
    public float getExoticEssenceCost() {
        return exoticEssenceCost;
    }
    public int getTime() {
        return time;
    }
    public FluidIngredient getInput1() {
        return input;
    }
    public FluidIngredient getInput2() {
        return input2;
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input3);
        return list;
    }
    @Override
    public boolean matches(RecipeInputWithFluid pContainer, Level pLevel) {
        return input.test(pContainer.getFluid(0)) && input2.test(pContainer.getFluid(1)) && input3.test(pContainer.getItem(0));
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
        return RecipeRegistry.SYNTHESIS_TYPE.get();
    }

    @Override
    public ResourceLocation getEntry() {
        return entry;
    }

    public static class Serializer implements RecipeSerializer<FluidMixingRecipe> {
        public static final MapCodec<FluidMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                FluidIngredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                FluidIngredient.CODEC.fieldOf("input2").forGetter(r -> r.input2),
                Ingredient.CODEC.fieldOf("input2").forGetter(r -> r.input3),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                Codec.FLOAT.optionalFieldOf("lunarEssenceCost", 0f).forGetter(r -> r.lunarEssenceCost),
                Codec.FLOAT.optionalFieldOf("naturalEssenceCost", 0f).forGetter(r -> r.naturalEssenceCost),
                Codec.FLOAT.optionalFieldOf("exoticEssenceCost", 0f).forGetter(r -> r.exoticEssenceCost),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry)
        ).apply(instance, FluidMixingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    FluidStack.STREAM_CODEC.encode(buf, obj.output);
                    FluidIngredient.STREAM_CODEC.encode(buf, obj.input);
                    FluidIngredient.STREAM_CODEC.encode(buf, obj.input2);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input3);
                    buf.writeInt(obj.time);
                    buf.writeFloat(obj.essenceCost);
                    buf.writeFloat(obj.lunarEssenceCost);
                    buf.writeFloat(obj.naturalEssenceCost);
                    buf.writeFloat(obj.exoticEssenceCost);
                    buf.writeResourceLocation(obj.entry);
                },
                (buf) -> {
                    FluidStack output = FluidStack.STREAM_CODEC.decode(buf);
                    FluidIngredient input = FluidIngredient.STREAM_CODEC.decode(buf);
                    FluidIngredient input2 = FluidIngredient.STREAM_CODEC.decode(buf);
                    Ingredient input3 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int time = buf.readInt();
                    float essenceCost = buf.readFloat();
                    float lunarEssenceCost = buf.readFloat();
                    float naturalEssenceCost = buf.readFloat();
                    float exoticEssenceCost = buf.readFloat();
                    ResourceLocation entry = buf.readResourceLocation();
                    return new FluidMixingRecipe(output, input, input2, input3, time, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost, entry);
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