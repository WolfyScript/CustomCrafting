package me.wolfyscript.customcrafting.recipes;

import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe {

    String getID();
    String getGroup();

    void load();
    void save();

}
