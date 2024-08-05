package com.cmdpro.datanessence.recipe;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.registry.RecipeRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class InfusionRecipe implements IHasEssenceCost, IHasRequiredKnowledge, Recipe<RecipeInput> {
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
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput pContainer, HolderLookup.Provider pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }


    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
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
        public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                ResourceLocation.CODEC.fieldOf("entry").forGetter((r) -> r.entry),
                Codec.FLOAT.optionalFieldOf("essenceCost", 0f).forGetter(r -> r.essenceCost),
                Codec.FLOAT.optionalFieldOf("lunarEssenceCost", 0f).forGetter(r -> r.lunarEssenceCost),
                Codec.FLOAT.optionalFieldOf("naturalEssenceCost", 0f).forGetter(r -> r.naturalEssenceCost),
                Codec.FLOAT.optionalFieldOf("exoticEssenceCost", 0f).forGetter(r -> r.exoticEssenceCost)
        ).apply(instance, (result, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost) -> new InfusionRecipe(result, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost)));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, obj) -> {
                    ItemStack.STREAM_CODEC.encode(buf, obj.output);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, obj.input);
                    buf.writeResourceLocation(obj.entry);
                    buf.writeFloat(obj.essenceCost);
                    buf.writeFloat(obj.lunarEssenceCost);
                    buf.writeFloat(obj.naturalEssenceCost);
                    buf.writeFloat(obj.exoticEssenceCost);
                },
                (buf) -> {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ResourceLocation entry = buf.readResourceLocation();
                    float essenceCost = buf.readFloat();
                    float lunarEssenceCost = buf.readFloat();
                    float naturalEssenceCost = buf.readFloat();
                    float exoticEssenceCost = buf.readFloat();
                    return new InfusionRecipe(output, input, entry, essenceCost, lunarEssenceCost, naturalEssenceCost, exoticEssenceCost);
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