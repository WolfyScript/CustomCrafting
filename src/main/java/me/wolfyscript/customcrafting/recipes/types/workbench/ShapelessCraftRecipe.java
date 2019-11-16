package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapelessCraftingRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class ShapelessCraftRecipe extends AdvancedCraftingRecipe implements ShapelessCraftingRecipe<AdvancedCraftConfig> {

    public ShapelessCraftRecipe(AdvancedCraftConfig config) {
        super(config);
    }

    @Override
    public void load() {
    }

    @Override
    public ShapelessCraftRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public ShapelessCraftRecipe save(AdvancedCraftConfig config) {
        return null;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
