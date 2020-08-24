package me.wolfyscript.customcrafting.data.cache.global;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.Pair;

import java.util.HashMap;
import java.util.List;

public class RecipeListCache {

    private final CustomCrafting customCrafting;
    private final HashMap<Pair<Category, Category>, List<CustomItem>> cachedRecipeItems = new HashMap<>();

    public RecipeListCache(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;


    }

    public void updateList() {
        RecipeHandler recipeHandler = customCrafting.getRecipeHandler();

    }


    public HashMap<Pair<Category, Category>, List<CustomItem>> getCachedRecipeItems() {
        return cachedRecipeItems;
    }
}
