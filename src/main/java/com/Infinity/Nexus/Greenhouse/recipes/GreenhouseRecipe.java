package com.Infinity.Nexus.Greenhouse.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public record GreenhouseRecipe(Integer energy, Integer size, Ingredient tier, String plant, List<Property> properties, List<OutputWithChance> outputs)
        implements Recipe<GreenhouseRecipeInput> {

    public record Property(String property, String replace) {
        public static final Codec<Property> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.STRING.optionalFieldOf("property", "none").forGetter(Property::property),
                Codec.STRING.optionalFieldOf("replace", "none").forGetter(Property::replace)
        ).apply(inst, Property::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Property> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Property::property,
                ByteBufCodecs.STRING_UTF8, Property::replace,
                Property::new
        );
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(tier);
        return list;
    }

    @Override
    public boolean matches(GreenhouseRecipeInput greenhouseRecipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return tier.test(greenhouseRecipeInput.getItem(0)) && plant.equals(greenhouseRecipeInput.getPlant());
    }

    @Override
    public ItemStack assemble(GreenhouseRecipeInput greenhouseRecipeInput, HolderLookup.Provider provider) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).stack().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).stack().copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GREENHOUSE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GREENHOUSE_RECIPE_TYPE.get();
    }

    public List<ItemStack> getRandomResults(float rd) {
        List<ItemStack> results = new ArrayList<>();
        for (int i = 1; i < outputs.size(); i++) {
            if (rd <= outputs.get(i).chance()) {
                results.add(outputs.get(i).stack().copy());
            }
        }
        return results;
    }

    public Ingredient getIngredient() {
        return tier;
    }

    public List<OutputWithChance> getOutputs() {
        return outputs;
    }

    public String getProperties(String type) {
        return switch (type) {
            case "replace" -> properties.isEmpty() ? null : properties.get(0).replace;
            case "property" -> properties.isEmpty() ? null : properties.get(0).property;
            default -> null;
        };
    }


    public String getPlant() {
        return plant;
    }

    public Integer getEnergy() {
        return energy;
    }

    public Integer getSize() {
        return size.intValue();

    }

    public Ingredient getTier() {
        return tier;
    }

    public static class Serializer implements RecipeSerializer<GreenhouseRecipe> {
        public static final MapCodec<GreenhouseRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("energy").forGetter(GreenhouseRecipe::energy),
                Codec.INT.fieldOf("size").forGetter(GreenhouseRecipe::size),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(GreenhouseRecipe::tier),
                Codec.STRING.fieldOf("plant").forGetter(GreenhouseRecipe::plant),
                Property.CODEC.listOf().fieldOf("properties").forGetter(GreenhouseRecipe::properties),
                OutputWithChance.CODEC.listOf().fieldOf("outputs").forGetter(GreenhouseRecipe::outputs)
        ).apply(inst, GreenhouseRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, OutputWithChance> OUTPUT_WITH_CHANCE_STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, OutputWithChance::stack,
                ByteBufCodecs.FLOAT, OutputWithChance::chance,
                OutputWithChance::new
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, GreenhouseRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, GreenhouseRecipe::energy,
                ByteBufCodecs.INT, GreenhouseRecipe::size,
                Ingredient.CONTENTS_STREAM_CODEC, GreenhouseRecipe::tier,
                ByteBufCodecs.STRING_UTF8, GreenhouseRecipe::plant,
                Property.STREAM_CODEC.apply(ByteBufCodecs.list()), GreenhouseRecipe::properties,
                OUTPUT_WITH_CHANCE_STREAM_CODEC.apply(ByteBufCodecs.list()), GreenhouseRecipe::outputs,
                GreenhouseRecipe::new
        );

        @Override
        public MapCodec<GreenhouseRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GreenhouseRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}