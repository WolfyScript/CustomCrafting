package me.wolfyscript.customcrafting.recipes;

import org.bukkit.inventory.Recipe;

public interface ICustomVanillaRecipe<T extends Recipe> {

    T getVanillaRecipe();

}
