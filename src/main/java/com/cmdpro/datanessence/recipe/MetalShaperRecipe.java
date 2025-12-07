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

public class MetalShaperRecipe implements Recipe<RecipeInput>, DataNEssenceRecipe {
    private final ItemStack output;
    private final Ingredient input;
    private final int time;

    public MetalShaperRecipe(ItemStack output,
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
        return RecipeRegistry.METAL_SHAPING_TYPE.get();
    }

    @Override
    public ResourceLocation getMachineEntry() {
        return DataNEssence.locate("machinery/metal_shaper");
    }

    public static class Serializer implements RecipeSerializer<MetalShaperRecipe> {
        public static final MapCodec<MetalShaperRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time)
        ).apply(instance, MetalShaperRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MetalShaperRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    buf.writeInt(obj.time);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int time = buf.readInt();
                    return new MetalShaperRecipe(output, input, time);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<MetalShaperRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MetalShaperRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}