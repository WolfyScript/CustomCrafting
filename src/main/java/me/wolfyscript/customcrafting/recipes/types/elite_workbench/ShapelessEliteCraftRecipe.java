package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapelessCraftingRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class ShapelessEliteCraftRecipe extends EliteCraftingRecipe implements ShapelessCraftingRecipe<EliteCraftConfig> {

    public ShapelessEliteCraftRecipe(EliteCraftConfig config) {
        super(config);
        if(getIngredients().size() <= 9){
            requiredGridSize = 3;
        }else if (getIngredients().size() <= 16){
            requiredGridSize = 4;
        }else if (getIngredients().size() <= 25){
            requiredGridSize = 5;
        }else if (getIngredients().size() <= 36){
            requiredGridSize = 6;
        }
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
