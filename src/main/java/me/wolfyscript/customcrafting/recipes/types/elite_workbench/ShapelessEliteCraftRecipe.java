package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapelessCraftingRecipe;

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

}
