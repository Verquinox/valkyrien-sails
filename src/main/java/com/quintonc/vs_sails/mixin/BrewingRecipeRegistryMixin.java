package com.quintonc.vs_sails.mixin;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryMixin {
    @Invoker("registerItemRecipe")
    static void invokeRegisterItemRecipe(Item input, Item item, Item output) {
        throw new AssertionError();
    }
}
