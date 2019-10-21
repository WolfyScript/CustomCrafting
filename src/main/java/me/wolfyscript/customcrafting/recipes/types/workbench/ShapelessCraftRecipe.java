package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ShapedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ShapelessCraftingRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

public class ShapelessCraftRecipe extends AdvancedCraftingRecipe implements ShapelessCraftingRecipe<AdvancedCraftConfig> {

    public ShapelessCraftRecipe(AdvancedCraftConfig config) {
        super(config);
    }

    @Override
    public void load() { }

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
