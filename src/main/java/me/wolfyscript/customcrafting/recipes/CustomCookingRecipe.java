package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.items.CustomItem;

import java.util.List;

public interface CustomCookingRecipe<T extends CookingConfig> extends CustomRecipe {

    List<CustomItem> getSource();

    @Override
    T getConfig();
}
