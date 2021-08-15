package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class ExtensionPackLoader extends ResourceLoader {

    public ExtensionPackLoader(CustomCrafting customCrafting) {
        super(customCrafting);
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public boolean save(ICustomRecipe<?> recipe) {
        return false;
    }

    @Override
    public boolean save(CustomItem item) {
        return false;
    }

    @Override
    public boolean delete(ICustomRecipe<?> recipe) {
        return false;
    }

    @Override
    public boolean delete(CustomItem item) {
        return false;
    }
}
