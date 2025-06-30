package com.Infinity.Nexus.Greenhouse.compat;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import com.Infinity.Nexus.Greenhouse.compat.jei.GreenhouseCategory;
import com.Infinity.Nexus.Greenhouse.recipes.GreenhouseRecipe;
import com.Infinity.Nexus.Greenhouse.recipes.ModRecipes;
import com.Infinity.Nexus.Greenhouse.screen.greenhouse.GreenhouseScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;


@JeiPlugin
public class JEIModPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        //-----------------------------------------Registry--------------------------------------------------//
        registration.addRecipeCategories(new GreenhouseCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        //-----------------------------------------Registry--------------------------------------------------//
        List<GreenhouseRecipe> greenhouseRecipes = recipeManager.getAllRecipesFor(ModRecipes.GREENHOUSE_RECIPE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();

        try {
            registration.addRecipes(GreenhouseCategory.GREENHOUSE_TYPE, greenhouseRecipes);
            System.out.println("Registry: " + greenhouseRecipes.size() +" "+ Component.translatable("block.infinity_nexus_greenhouse.greenhouse"));
        }catch (Exception ignored){
        }
        registration.addItemStackInfo(new ItemStack(ModBlocksGreenhouse.GREENHOUSE.get()), Component.translatable("infinity_nexus_mod.jei_information"));

    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        //TODO
        IModPlugin.super.registerRecipeTransferHandlers(registration);
    }
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocksGreenhouse.GREENHOUSE.get().asItem().getDefaultInstance(), GreenhouseCategory.GREENHOUSE_TYPE);
    }
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        //-----------------------------------------Registry--------------------------------------------------//
        registration.addRecipeClickArea(GreenhouseScreen.class,162, -10,8,9, GreenhouseCategory.GREENHOUSE_TYPE);
    }

}
