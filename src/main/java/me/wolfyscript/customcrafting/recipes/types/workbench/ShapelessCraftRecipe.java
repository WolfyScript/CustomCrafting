package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapelessCraftingRecipe;

public class ShapelessCraftRecipe extends AdvancedCraftingRecipe implements ShapelessCraftingRecipe<AdvancedCraftConfig> {

    public ShapelessCraftRecipe(AdvancedCraftConfig config) {
        super(config);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
