package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Map;

public class SynthesisRecipe implements Recipe<RecipeInput>, IHasEssenceCost, IHasRequiredKnowledge {
    private final ItemStack output;
    private final Ingredient input;
    private final Ingredient input2;
    private final int time;
    private final Map<ResourceLocation, Float> essenceCost;
    private final ResourceLocation entry;
    private final boolean allowIncomplete;

    public SynthesisRecipe(ItemStack output,
                           Ingredient input, Ingredient input2, int time, Map<ResourceLocation, Float> essenceCost, ResourceLocation entry, boolean allowIncomplete) {
        this.output = output;
        this.input = input;
        this.input2 = input2;
        this.time = time;
        this.essenceCost = essenceCost;
        this.entry = entry;
        this.allowIncomplete = allowIncomplete;
    }
    @Override
    public Map<ResourceLocation, Float> getEssenceCost() {
        return essenceCost;
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
        return RecipeRegistry.SYNTHESIS_TYPE.get();
    }

    @Override
    public ResourceLocation getEntry() {
        return entry;
    }

    @Override
    public boolean allowIncomplete() {
        return allowIncomplete;
    }

    public static class Serializer implements RecipeSerializer<SynthesisRecipe> {
        public static final MapCodec<SynthesisRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Ingredient.CODEC.fieldOf("input2").forGetter(r -> r.input2),
                Codec.INT.fieldOf("time").forGetter((r) -> r.time),
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).fieldOf("essenceCost").forGetter(r -> r.essenceCost),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.BOOL.optionalFieldOf("allow_incomplete", false).forGetter((r) -> r.allowIncomplete)
        ).apply(instance, SynthesisRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SynthesisRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input2);
                    buf.writeInt(obj.time);
                    buf.writeMap(obj.essenceCost, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);
                    buf.writeResourceLocation(obj.entry);
                    buf.writeBoolean(obj.allowIncomplete);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    Ingredient input2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int time = buf.readInt();
                    Map<ResourceLocation, Float> essenceCost = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat);
                    ResourceLocation entry = buf.readResourceLocation();
                    boolean allowIncomplete = buf.readBoolean();
                    return new SynthesisRecipe(output, input, input2, time, essenceCost, entry, allowIncomplete);
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