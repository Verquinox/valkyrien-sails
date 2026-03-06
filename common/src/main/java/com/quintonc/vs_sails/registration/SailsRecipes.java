package com.quintonc.vs_sails.registration;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.recipes.WindFlagDyeRecipe;
import com.quintonc.vs_sails.recipes.WindFlagPatternRecipe;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;


//Sails need a custom recipe handler in order to handle dyeing and pattern application in menu
public class SailsRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ValkyrienSails.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static RegistrySupplier<RecipeSerializer<WindFlagPatternRecipe>> WIND_FLAG_PATTERN;
    public static RegistrySupplier<RecipeSerializer<WindFlagDyeRecipe>> WIND_FLAG_DYE;

    public static void register() {
        WIND_FLAG_PATTERN = RECIPE_SERIALIZERS.register(
                ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "wind_flag_pattern"),
                () -> new SimpleCraftingRecipeSerializer<>(WindFlagPatternRecipe::new)
        );
        WIND_FLAG_DYE = RECIPE_SERIALIZERS.register(
                ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "wind_flag_dye"),
                () -> new SimpleCraftingRecipeSerializer<>(WindFlagDyeRecipe::new)
        );
        RECIPE_SERIALIZERS.register();
    }
}
