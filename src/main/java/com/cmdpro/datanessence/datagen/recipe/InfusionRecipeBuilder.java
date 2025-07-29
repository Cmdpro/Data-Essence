package com.cmdpro.datanessence.datagen.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.recipe.InfusionRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public class InfusionRecipeBuilder extends DnERecipeBuilder {
    private final Ingredient input;
    private final ResourceLocation entry;
    private final int completionStage;
    private final Map<ResourceLocation, Float> essenceCost;

    public InfusionRecipeBuilder(ItemStack result, Ingredient input, ResourceLocation entry, int completionStage, Map<ResourceLocation, Float> essenceCost) {
        super(result);
        this.input = input;
        this.entry = entry;
        this.completionStage = completionStage;
        this.essenceCost = essenceCost;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        id = DataNEssence.locate("infusion/"+id.getPath());
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        InfusionRecipe recipe = new InfusionRecipe(this.result, this.input, this.entry, this.completionStage, this.essenceCost);
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/infusion/")));
    }
}
