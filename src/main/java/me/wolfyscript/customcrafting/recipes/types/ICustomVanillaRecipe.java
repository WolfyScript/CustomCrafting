package me.wolfyscript.customcrafting.recipes.types;

import org.bukkit.inventory.Recipe;

public interface ICustomVanillaRecipe<T extends Recipe> {

    T getVanillaRecipe();

}
