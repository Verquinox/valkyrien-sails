package com.quintonc.vs_sails.recipes;

import com.quintonc.vs_sails.registration.SailsRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WindFlagDyeRecipe extends CustomRecipe {
    public WindFlagDyeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    //If a flag is crafted with a dye and has no pattern, dye the flag
    //If a flag is crafted with a pattern, pattern the flag
    //If a flag is crafted with a dye and has a pattern, dye the pattern
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack flagStack = ItemStack.EMPTY;
        DyeColor dyeColor = null;

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

            if (stack.getItem() instanceof DyeItem dyeItem) {
                if (dyeColor != null) {
                    return false;
                }
                dyeColor = dyeItem.getDyeColor();
                continue;
            }

            return false;
        }

        if (flagStack.isEmpty() || dyeColor == null) {
            return false;
        }

        int pattern = WindFlagRecipeUtil.readPattern(flagStack);
        if (pattern == WindFlagRecipeUtil.PATTERN_NONE) {
            Item targetFlagItem = WindFlagRecipeUtil.getFlagItemForDye(dyeColor);
            return flagStack.getItem() != targetFlagItem;
        }

        int currentOverlayColor = WindFlagRecipeUtil.readOverlayColor(flagStack);
        return currentOverlayColor != dyeColor.getId();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack flagStack = ItemStack.EMPTY;
        DyeColor dyeColor = null;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (WindFlagRecipeUtil.isWindFlagItem(stack)) {
                flagStack = stack;
            } else if (stack.getItem() instanceof DyeItem dyeItem) {
                dyeColor = dyeItem.getDyeColor();
            }
        }

        if (flagStack.isEmpty() || dyeColor == null) {
            return ItemStack.EMPTY;
        }

        int pattern = WindFlagRecipeUtil.readPattern(flagStack);
        if (pattern == WindFlagRecipeUtil.PATTERN_NONE) {
            Item targetFlagItem = WindFlagRecipeUtil.getFlagItemForDye(dyeColor);
            ItemStack result = new ItemStack(targetFlagItem);
            if (flagStack.hasTag()) {
                result.setTag(flagStack.getTag().copy());
            }
            WindFlagRecipeUtil.writePattern(result, WindFlagRecipeUtil.PATTERN_NONE);
            WindFlagRecipeUtil.writeOverlayColor(result, WindFlagRecipeUtil.readOverlayColor(flagStack));
            return result;
        }

        ItemStack result = flagStack.copy();
        result.setCount(1);
        WindFlagRecipeUtil.writeOverlayColor(result, dyeColor.getId());
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SailsRecipes.WIND_FLAG_DYE.get();
    }
}
