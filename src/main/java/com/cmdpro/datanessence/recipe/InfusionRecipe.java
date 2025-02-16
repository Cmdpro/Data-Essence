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

public class InfusionRecipe implements IHasEssenceCost, IHasRequiredKnowledge, Recipe<RecipeInput> {
    private final ItemStack output;
    private final Ingredient input;
    private final ResourceLocation entry;
    private final boolean allowIncomplete;
    private final Map<ResourceLocation, Float> essenceCost;

    public InfusionRecipe(ItemStack output,
                          Ingredient input, ResourceLocation entry, boolean allowIncomplete, Map<ResourceLocation, Float> essenceCost) {
        this.output = output;
        this.input = input;
        this.entry = entry;
        this.allowIncomplete = allowIncomplete;
        this.essenceCost = essenceCost;
    }
    @Override
    public boolean allowIncomplete() {
        return allowIncomplete;
    }

    @Override
    public Map<ResourceLocation, Float> getEssenceCost() {
        return essenceCost;
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
        return RecipeRegistry.INFUSION_TYPE.get();
    }
    @Override
    public ResourceLocation getEntry() {
        return entry;
    }

    public static class Serializer implements RecipeSerializer<InfusionRecipe> {
        public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.BOOL.optionalFieldOf("allow_incomplete", false).forGetter((r) -> r.allowIncomplete),
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).fieldOf("essenceCost").forGetter(r -> r.essenceCost)
        ).apply(instance, InfusionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    buf.writeResourceLocation(obj.entry);
                    buf.writeBoolean(obj.allowIncomplete);
                    buf.writeMap(obj.essenceCost, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ResourceLocation entry = buf.readResourceLocation();
                    boolean allowIncomplete = buf.readBoolean();
                    Map<ResourceLocation, Float> essenceCost = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat);
                    return new InfusionRecipe(output, input, entry, allowIncomplete, essenceCost);
                }
        );

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<InfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}