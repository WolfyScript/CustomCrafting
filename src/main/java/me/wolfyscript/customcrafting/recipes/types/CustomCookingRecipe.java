package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.List;

public interface CustomCookingRecipe<T extends CookingConfig> extends CustomRecipe<T> {

    List<CustomItem> getSource();

    @Override
    T getConfig();
}
