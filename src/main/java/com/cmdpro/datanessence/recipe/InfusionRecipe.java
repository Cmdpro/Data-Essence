package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class InfusionRecipe implements IHasEssenceCost, IHasRequiredKnowledge, Recipe<SimpleContainer> {
    private final ItemStack output;
    private final Ingredient input;
    private final ResourceLocation entry;
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;

    public InfusionRecipe(ItemStack output,
                          Ingredient input, ResourceLocation entry, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost) {
        this.output = output;
        this.input = input;
        this.entry = entry;
        this.essenceCost = essenceCost;
        this.lunarEssenceCost = lunarEssenceCost;
        this.naturalEssenceCost = naturalEssenceCost;
        this.exoticEssenceCost = exoticEssenceCost;
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
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }
    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
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
        public static final Codec<InfusionRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                Codec.FLOAT.optionalFieldOf("lunarEssenceCost", 0f).forGetter(r -> r.lunarEssenceCost),
                Codec.FLOAT.optionalFieldOf("naturalEssenceCost", 0f).forGetter(r -> r.naturalEssenceCost),
                Codec.FLOAT.optionalFieldOf("exoticEssenceCost", 0f).forGetter(r -> r.exoticEssenceCost)
        ).apply(instance, (result, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost) -> new InfusionRecipe(result, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost)));
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public Codec<InfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public InfusionRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            return pBuffer.readWithCodecTrusted(NbtOps.INSTANCE, CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InfusionRecipe pRecipe) {
            pBuffer.writeWithCodec(NbtOps.INSTANCE, CODEC, pRecipe);
        }
    }
}