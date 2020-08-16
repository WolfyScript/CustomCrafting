package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;

public class RecipeBookConfig {

    private final WolfyUtilities api;
    private Categories categories;

    public RecipeBookConfig(CustomCrafting customCrafting) {
        customCrafting.saveResource("recipe_book.json", false);
        this.api = WolfyUtilities.getAPI(customCrafting);
        try {
            JsonNode node = JacksonUtil.getObjectMapper().readTree(new File(customCrafting.getDataFolder(), "recipe_book.json"));
            this.categories = JacksonUtil.getObjectMapper().convertValue(node.get("categories"), Categories.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }
}
