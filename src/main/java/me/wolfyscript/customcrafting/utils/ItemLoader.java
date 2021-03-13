package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.util.List;

public class ItemLoader {

    public static Ingredient loadIngredient(JsonNode node) {
        if (node.isArray()) {
            Ingredient ingredient = new Ingredient();
            node.elements().forEachRemaining(item -> ingredient.getItems().add(JacksonUtil.getObjectMapper().convertValue(item, APIReference.class)));
            ingredient.buildChoices();
            return ingredient;
        }
        Ingredient ingredient = JacksonUtil.getObjectMapper().convertValue(node, Ingredient.class);
        if (ingredient != null) {
            ingredient.buildChoices();
        }
        return ingredient;
    }

    public static <T extends ResultTarget> Result<T> loadResult(JsonNode node) {
        if (node.isArray()) {
            Result<T> result = new Result<>();
            node.elements().forEachRemaining(item -> result.getItems().add(JacksonUtil.getObjectMapper().convertValue(item, APIReference.class)));
            result.buildChoices();
            return result;
        }
        Result<T> result = JacksonUtil.getObjectMapper().convertValue(node, new TypeReference<Result<T>>() {});
        if (result != null) {
            result.buildChoices();
            return result;
        }
        return new Result<>();
    }

    public static void loadToList(JsonNode node, List<CustomItem> items) {
        items.add(load(node));
    }

    public static CustomItem load(JsonNode node) {
        return load(JacksonUtil.getObjectMapper().convertValue(node, APIReference.class));
    }

    public static CustomItem load(APIReference reference) {
        CustomItem customItem = CustomItem.of(reference);
        if (customItem == null && reference instanceof WolfyUtilitiesRef) {
            customItem = Registry.CUSTOM_ITEMS.get(NamespacedKeyUtils.fromInternal(((WolfyUtilitiesRef) reference).getNamespacedKey()));
        }
        return customItem;
    }
}
