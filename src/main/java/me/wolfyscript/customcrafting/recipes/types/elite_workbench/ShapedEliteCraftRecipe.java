package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class ShapedEliteCraftRecipe extends EliteCraftingRecipe implements ShapedCraftingRecipe<EliteCraftConfig> {

    private String[] shape;

    public ShapedEliteCraftRecipe(EliteCraftConfig config) {
        super(config);
        this.shape = WolfyUtilities.formatShape(config.getShape()).toArray(new String[0]);
    }

    @Override
    public void load() { }

    @Override
    public ShapedEliteCraftRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public ShapedEliteCraftRecipe save(EliteCraftConfig config) {
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
