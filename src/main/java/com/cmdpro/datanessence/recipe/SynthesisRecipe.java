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

public class SynthesisRecipe implements Recipe<RecipeInput>, IHasEssenceCost, IHasRequiredKnowledge {
    private final ItemStack output;
    private final Ingredient input;
    private final Ingredient input2;
    private final int time;
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;
    private final ResourceLocation entry;

    public SynthesisRecipe(ItemStack output,
                           Ingredient input, Ingredient input2, int time, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost, ResourceLocation entry) {
        this.output = output;
        this.input = input;
        this.input2 = input2;
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
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        list.add(input2);
        return list;
    }
    @Override
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0)) && input2.test(pContainer.getItem(1));
    }

    @Override
    public ItemStack assemble(RecipeInput pContainer, HolderLookup.Provider pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }


    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
        return output.copy();
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

    public static class Serializer implements RecipeSerializer<SynthesisRecipe> {
        public static final MapCodec<SynthesisRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Ingredient.CODEC.fieldOf("input2").forGetter(r -> r.input2),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                Codec.FLOAT.optionalFieldOf("lunarEssenceCost", 0f).forGetter(r -> r.lunarEssenceCost),
                Codec.FLOAT.optionalFieldOf("naturalEssenceCost", 0f).forGetter(r -> r.naturalEssenceCost),
                Codec.FLOAT.optionalFieldOf("exoticEssenceCost", 0f).forGetter(r -> r.exoticEssenceCost),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry)
        ).apply(instance, SynthesisRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SynthesisRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input2);
                    buf.writeInt(obj.time);
                    buf.writeFloat(obj.essenceCost);
                    buf.writeFloat(obj.lunarEssenceCost);
                    buf.writeFloat(obj.naturalEssenceCost);
                    buf.writeFloat(obj.exoticEssenceCost);
                    buf.writeResourceLocation(obj.entry);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    Ingredient input2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int time = buf.readInt();
                    float essenceCost = buf.readFloat();
                    float lunarEssenceCost = buf.readFloat();
                    float naturalEssenceCost = buf.readFloat();
                    float exoticEssenceCost = buf.readFloat();
                    ResourceLocation entry = buf.readResourceLocation();
                    return new SynthesisRecipe(output, input, input2, time, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost, entry);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<SynthesisRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SynthesisRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}