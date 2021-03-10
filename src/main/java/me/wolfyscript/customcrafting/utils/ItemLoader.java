package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.util.List;

public class ItemLoader {

    public static Ingredient loadRecipeItem(JsonNode node) {
        if (node.isArray()) {
            Ingredient ingredient = new Ingredient();
            node.elements().forEachRemaining(item -> ingredient.getItems().add(JacksonUtil.getObjectMapper().convertValue(node, APIReference.class)));
            return ingredient;
        }
        Ingredient ingredient = JacksonUtil.getObjectMapper().convertValue(node, Ingredient.class);
        if (ingredient != null) {
            ingredient.buildChoices();
        }
        return ingredient;
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
