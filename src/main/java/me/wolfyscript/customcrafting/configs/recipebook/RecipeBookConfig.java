package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.JsonConfig;

import java.io.File;

public class RecipeBookConfig extends JsonConfig<Categories> {

    public RecipeBookConfig(CustomCrafting customCrafting) {
        super(new File(customCrafting.getDataFolder(), "recipe_book.json"), Categories.class);
    }

    public Categories getCategories() {
        return value;
    }

    public void setCategories(Categories categories) {
        this.value = categories;
    }
}
