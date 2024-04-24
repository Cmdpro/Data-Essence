package com.cmdpro.datanessence.recipe;


import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.init.RecipeInit;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ShapelessFabricationRecipe implements IFabricationRecipe {
    private final float essenceCost;
    private final float lunarEssenceCost;
    private final float naturalEssenceCost;
    private final float exoticEssenceCost;
    private final ResourceLocation id;
    private final String entry;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public ShapelessFabricationRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> ingredients, String entry, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost) {
        this.id = id;
        this.entry = entry;
        this.essenceCost = essenceCost;
        this.lunarEssenceCost = lunarEssenceCost;
        this.naturalEssenceCost = naturalEssenceCost;
        this.exoticEssenceCost = exoticEssenceCost;
        this.result = result;
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }


    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack = pInv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }


    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeInit.FABRICATIONCRAFTING.get();
    }

    @Override
    public String getEntry() {
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
    public static class Serializer implements RecipeSerializer<ShapelessFabricationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(DataNEssence.MOD_ID,"shapelessrunicrecipe");

        @Override
        public ShapelessFabricationRecipe fromJson(ResourceLocation id, JsonObject json) {
            String entry = GsonHelper.getAsString(json, "entry");
            float essenceCost = GsonHelper.getAsFloat(json, "essenceCost", 0);
            float lunarEssenceCost = GsonHelper.getAsFloat(json, "lunarEssenceCost", 0);
            float naturalEssenceCost = GsonHelper.getAsFloat(json, "naturalEssenceCost", 0);
            float exoticEssenceCost = GsonHelper.getAsFloat(json, "exoticEssenceCost", 0);
            NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > ShapedFabricationRecipe.MAX_WIDTH * ShapedFabricationRecipe.MAX_HEIGHT) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is " + (ShapedFabricationRecipe.MAX_WIDTH * ShapedFabricationRecipe.MAX_HEIGHT));
            } else {
                ItemStack itemstack = ShapedFabricationRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                return new ShapelessFabricationRecipe(id, itemstack, nonnulllist, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i), false);
                if (true || !ingredient.isEmpty()) { // FORGE: Skip checking if an ingredient is empty during shapeless recipe deserialization to prevent complex ingredients from caching tags too early. Can not be done using a config value due to sync issues.
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        @Override
        public ShapelessFabricationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int i = buf.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(buf));
            }

            ItemStack itemstack = buf.readItem();
            String entry = buf.readUtf();
            float essenceCost = buf.readFloat();
            float lunarEssenceCost = buf.readFloat();
            float naturalEssenceCost = buf.readFloat();
            float exoticEssenceCost = buf.readFloat();
            return new ShapelessFabricationRecipe(id, itemstack, nonnulllist, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ShapelessFabricationRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.result);
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