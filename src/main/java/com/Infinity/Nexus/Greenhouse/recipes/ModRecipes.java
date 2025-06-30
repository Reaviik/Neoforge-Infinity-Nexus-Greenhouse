package com.Infinity.Nexus.Greenhouse.recipes;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, InfinityNexusGreenhouse.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, InfinityNexusGreenhouse.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GreenhouseRecipe>> GREENHOUSE_RECIPE_SERIALIZER = SERIALIZERS.register("greenhouse", GreenhouseRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<GreenhouseRecipe>> GREENHOUSE_RECIPE_TYPE = TYPES.register("greenhouse", () -> new RecipeType<GreenhouseRecipe>() {
                @Override
                public String toString() {
                    return "greenhouse";
                }
            });
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
