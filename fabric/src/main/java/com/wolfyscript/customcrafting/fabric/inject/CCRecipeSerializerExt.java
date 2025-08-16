package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomSmithingRecipeProxy;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface CCRecipeSerializerExt {

    RecipeSerializer<CustomSmithingRecipeProxy> getSmithingCustomcrafting();

}
