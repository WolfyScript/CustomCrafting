package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapelessCraftingRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class ShapelessEliteCraftRecipe extends EliteCraftingRecipe implements ShapelessCraftingRecipe<EliteCraftConfig> {

    public ShapelessEliteCraftRecipe(EliteCraftConfig config) {
        super(config);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public ShapelessEliteCraftRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public ShapelessEliteCraftRecipe save(EliteCraftConfig config) {
        return null;
    }
}
