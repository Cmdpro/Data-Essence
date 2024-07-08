package com.cmdpro.datanessence.recipe;


import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
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
    private final ResourceLocation entry;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public ShapelessFabricationRecipe(ItemStack result, NonNullList<Ingredient> ingredients, ResourceLocation entry, float essenceCost, float lunarEssenceCost, float naturalEssenceCost, float exoticEssenceCost) {
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

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, null) : net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= this.ingredients.size();
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
    public static class Serializer implements RecipeSerializer<ShapelessFabricationRecipe> {
        public static final Codec<ShapelessFabricationRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.result),
                Ingredient.CODEC_NONEMPTY
                        .listOf()
                        .fieldOf("ingredients")
                        .flatXmap(
                                p_301021_ -> {
                                    Ingredient[] aingredient = p_301021_
                                            .toArray(Ingredient[]::new); //Forge skip the empty check and immediatly create the array.
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else {
                                        return aingredient.length > ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()
                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()))
                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(p_300975_ -> p_300975_.ingredients),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                Codec.FLOAT.optionalFieldOf("lunarEssenceCost", 0f).forGetter(r -> r.lunarEssenceCost),
                Codec.FLOAT.optionalFieldOf("naturalEssenceCost", 0f).forGetter(r -> r.naturalEssenceCost),
                Codec.FLOAT.optionalFieldOf("exoticEssenceCost", 0f).forGetter(r -> r.exoticEssenceCost)
        ).apply(instance, (result, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost) -> new ShapelessFabricationRecipe(result, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost)));
        public static final ShapelessFabricationRecipe.Serializer INSTANCE = new ShapelessFabricationRecipe.Serializer();

        @Override
        public Codec<ShapelessFabricationRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapelessFabricationRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            //noinspection deprecation
            return pBuffer.readWithCodecTrusted(NbtOps.INSTANCE, CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ShapelessFabricationRecipe pRecipe) {
            pBuffer.writeWithCodec(NbtOps.INSTANCE, CODEC, pRecipe);
        }
    }
}