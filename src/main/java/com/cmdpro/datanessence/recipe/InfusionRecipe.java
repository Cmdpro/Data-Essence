package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class InfusionRecipe implements IHasEssenceCost, IHasRequiredKnowledge, Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input;
    private final String entry;
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;

    public InfusionRecipe(ResourceLocation id, ItemStack output,
                          Ingredient input, String entry, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost) {
        this.id = id;
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    @Override
    public String getEntry() {
        return entry;
    }

    public static class Type implements RecipeType<InfusionRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "infusion";
    }

    public static class Serializer implements RecipeSerializer<InfusionRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(DataNEssence.MOD_ID,"infusion");

        @Override
        public InfusionRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            String entry = GsonHelper.getAsString(json, "entry");
            float essenceCost = GsonHelper.getAsFloat(json, "essenceCost", 0);
            float lunarEssenceCost = GsonHelper.getAsFloat(json, "lunarEssenceCost", 0);
            float naturalEssenceCost = GsonHelper.getAsFloat(json, "naturalEssenceCost", 0);
            float exoticEssenceCost = GsonHelper.getAsFloat(json, "exoticEssenceCost", 0);
            return new InfusionRecipe(id, output, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost);
        }

        @Override
        public InfusionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            String entry = buf.readUtf();
            float essenceCost = buf.readFloat();
            float lunarEssenceCost = buf.readFloat();
            float naturalEssenceCost = buf.readFloat();
            float exoticEssenceCost = buf.readFloat();
            return new InfusionRecipe(id, output, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InfusionRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItemStack(recipe.getResultItem(RegistryAccess.EMPTY), false);
            buf.writeUtf(recipe.entry);
            buf.writeFloat(recipe.essenceCost);
            buf.writeFloat(recipe.lunarEssenceCost);
            buf.writeFloat(recipe.naturalEssenceCost);
            buf.writeFloat(recipe.exoticEssenceCost);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}