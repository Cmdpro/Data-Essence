package com.cmdpro.datanessence.recipe;


import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.*;

public class ShapedFabricationRecipe implements IFabricationRecipe {
    private final ResourceLocation entry;
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;
    final ShapedRecipePattern pattern;
    final ItemStack result;

    public ShapedFabricationRecipe(ShapedRecipePattern pPattern, ItemStack pResult, ResourceLocation entry, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost) {
        this.entry = entry;
        this.essenceCost = essenceCost;
        this.naturalEssenceCost = naturalEssenceCost;
        this.exoticEssenceCost = exoticEssenceCost;
        this.lunarEssenceCost = lunarEssenceCost;
        this.pattern = pPattern;
        this.result = pResult;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.pattern.width() && pHeight >= this.pattern.height();
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        return this.pattern.matches(pInv);
    }

    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return this.getResultItem(pRegistryAccess).copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().filter(p_151277_ -> !p_151277_.isEmpty()).anyMatch(net.neoforged.neoforge.common.CommonHooks::hasNoElements);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FABRICATIONCRAFTING.get();
    }

    @Override
    public ResourceLocation getEntry() {
        return entry;
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

    public static class Serializer implements RecipeSerializer<ShapedFabricationRecipe> {
        public static final Codec<ShapedFabricationRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.result),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                Codec.FLOAT.optionalFieldOf("lunarEssenceCost", 0f).forGetter(r -> r.lunarEssenceCost),
                Codec.FLOAT.optionalFieldOf("naturalEssenceCost", 0f).forGetter(r -> r.naturalEssenceCost),
                Codec.FLOAT.optionalFieldOf("exoticEssenceCost", 0f).forGetter(r -> r.exoticEssenceCost)
        ).apply(instance, (pattern, result, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost) -> new ShapedFabricationRecipe(pattern, result, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost)));
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public Codec<ShapedFabricationRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapedFabricationRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            //noinspection deprecation
            return pBuffer.readWithCodecTrusted(NbtOps.INSTANCE, CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ShapedFabricationRecipe pRecipe) {
            pBuffer.writeWithCodec(NbtOps.INSTANCE, CODEC, pRecipe);
        }
    }
}