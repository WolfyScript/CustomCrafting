package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ExtensionPackLoader extends ResourceLoader {

    public ExtensionPackLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "extension_loader"));
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public boolean save(CustomRecipe<?> recipe) {
        return false;
    }

    @Override
    public boolean save(CustomItem item) {
        return false;
    }

    @Override
    public boolean delete(CustomRecipe<?> recipe) {
        return false;
    }

    @Override
    public boolean delete(CustomItem item) {
        return false;
    }
}
