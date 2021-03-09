package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.util.List;

public class ItemLoader {

    public static RecipeItemStack loadRecipeItem(JsonNode node) {
        if (node.isArray()) {
            RecipeItemStack recipeItemStack = new RecipeItemStack();
            node.elements().forEachRemaining(item -> recipeItemStack.getItems().add(JacksonUtil.getObjectMapper().convertValue(node, APIReference.class)));
            return recipeItemStack;
        }
        RecipeItemStack recipeItemStack = JacksonUtil.getObjectMapper().convertValue(node, RecipeItemStack.class);
        if (recipeItemStack != null) {
            recipeItemStack.buildChoices();
        }
        return recipeItemStack;
    }

    public static void loadToList(JsonNode node, List<CustomItem> items) {
        items.add(load(node));
    }

    public static CustomItem load(JsonNode node) {
        APIReference reference = JacksonUtil.getObjectMapper().convertValue(node, APIReference.class);
        CustomItem customItem = CustomItem.of(reference);
        if (customItem == null && reference instanceof WolfyUtilitiesRef) {
            customItem = Registry.CUSTOM_ITEMS.get(NamespacedKeyUtils.fromInternal(((WolfyUtilitiesRef) reference).getNamespacedKey()));
        }
        return customItem;
    }
}
