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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MineralPurificationRecipe implements Recipe<RecipeInput>, DataNEssenceRecipe {
    private final ItemStack output;
    private final ItemStack nuggetOutput;
    private final Ingredient input;
    private final int time;

    public MineralPurificationRecipe(ItemStack output, ItemStack nuggetOutput,
                                     Ingredient input, int time) {
        this.output = output;
        this.nuggetOutput = nuggetOutput;
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
        return output.copy();
    }

    public ItemStack assembleNuggetOutput(RandomSource random, RecipeInput pContainer, HolderLookup.Provider pRegistryAccess) {
        ItemStack copy = nuggetOutput.copy();
        copy.setCount(copy.getCount()*random.nextInt(0, 9));
        return copy;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public ItemStack getNuggetOutput(HolderLookup.Provider pRegistryAccess) {
        return nuggetOutput;
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
        return RecipeRegistry.MINERAL_PURIFICATION_TYPE.get();
    }

    @Override
    public ResourceLocation getMachineEntry() {
        return DataNEssence.locate("machinery/mineral_purification_chamber");
    }

    public static class Serializer implements RecipeSerializer<MineralPurificationRecipe> {
        public static final MapCodec<MineralPurificationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                ItemStack.CODEC.fieldOf("nuggetResult").forGetter(r -> r.nuggetOutput),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time)
        ).apply(instance, MineralPurificationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MineralPurificationRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    ItemStack.STREAM_CODEC.encode(buf, obj.nuggetOutput);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    buf.writeInt(obj.time);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    ItemStack nuggetOutput = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int time = buf.readInt();
                    return new MineralPurificationRecipe(output, nuggetOutput, input, time);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<MineralPurificationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MineralPurificationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}