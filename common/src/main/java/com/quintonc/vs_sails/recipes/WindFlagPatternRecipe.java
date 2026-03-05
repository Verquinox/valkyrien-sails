package com.quintonc.vs_sails.recipes;

import com.quintonc.vs_sails.registration.SailsRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WindFlagPatternRecipe extends CustomRecipe {
    public WindFlagPatternRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack flagStack = ItemStack.EMPTY;
        ItemStack patternStack = ItemStack.EMPTY;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (WindFlagRecipeUtil.isWindFlagItem(stack)) {
                if (!flagStack.isEmpty()) {
                    return false;
                }
                flagStack = stack;
                continue;
            }

            if (WindFlagRecipeUtil.getPatternFromBannerPatternItem(stack) != WindFlagRecipeUtil.PATTERN_NONE) {
                if (!patternStack.isEmpty()) {
                    return false;
                }
                patternStack = stack;
                continue;
            }

            return false;
        }

        if (flagStack.isEmpty() || patternStack.isEmpty()) {
            return false;
        }

        int targetPattern = WindFlagRecipeUtil.getPatternFromBannerPatternItem(patternStack);
        int currentPattern = WindFlagRecipeUtil.readPattern(flagStack);
        return targetPattern != WindFlagRecipeUtil.PATTERN_NONE && currentPattern != targetPattern;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack flagStack = ItemStack.EMPTY;
        int targetPattern = WindFlagRecipeUtil.PATTERN_NONE;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (WindFlagRecipeUtil.isWindFlagItem(stack)) {
                flagStack = stack;
            } else {
                targetPattern = WindFlagRecipeUtil.getPatternFromBannerPatternItem(stack);
            }
        }

        if (flagStack.isEmpty() || targetPattern == WindFlagRecipeUtil.PATTERN_NONE) {
            return ItemStack.EMPTY;
        }

        ItemStack result = flagStack.copy();
        result.setCount(1);
        WindFlagRecipeUtil.writePattern(result, targetPattern);
        WindFlagRecipeUtil.writeOverlayColor(result, WindFlagRecipeUtil.readOverlayColor(flagStack));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SailsRecipes.WIND_FLAG_PATTERN.get();
    }
}
