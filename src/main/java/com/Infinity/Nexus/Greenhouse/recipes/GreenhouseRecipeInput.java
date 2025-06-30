package com.Infinity.Nexus.Greenhouse.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public record GreenhouseRecipeInput(ItemStack input, String plantID) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return input;
    }
    @Override
    public int size() {
        return 1;
    }

    public String getPlant() {
        return plantID;
    }
}
