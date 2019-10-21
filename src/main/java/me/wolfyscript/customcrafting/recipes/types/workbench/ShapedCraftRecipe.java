package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ShapedCraftingRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

public class ShapedCraftRecipe extends AdvancedCraftingRecipe implements ShapedCraftingRecipe<AdvancedCraftConfig> {

    private String[] shape;

    public ShapedCraftRecipe(AdvancedCraftConfig config) {
        super(config);
        this.shape = WolfyUtilities.formatShape(config.getShape()).toArray(new String[0]);
    }

    @Override
    public void load() {
    }

    @Override
    public ShapedCraftRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public ShapedCraftRecipe save(AdvancedCraftConfig config) {
        return null;
    }

    @Override
    public String[] getShape() {
        return shape;
    }

    @Override
    public void setShape(String[] shape) {
        this.shape = shape;
    }
}
