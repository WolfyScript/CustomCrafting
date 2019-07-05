package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.items.CustomItem;

public interface CustomCookingRecipe<T extends CookingConfig> extends CustomRecipe{

    CustomItem getSource();

    @Override
    T getConfig();
}
