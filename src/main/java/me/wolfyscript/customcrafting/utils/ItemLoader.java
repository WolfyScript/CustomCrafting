package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class ItemLoader {

    private ItemLoader() {
    }

    public static Ingredient loadIngredient(JsonNode node) {
        final Ingredient ingredient;
        if (node.isArray()) {
            ingredient = new Ingredient();
            node.elements().forEachRemaining(item -> ingredient.getItems().add(JacksonUtil.getObjectMapper().convertValue(item, APIReference.class)));
        } else {
            ingredient = JacksonUtil.getObjectMapper().convertValue(node, Ingredient.class);
        }
        if (ingredient != null) {
            ingredient.buildChoices();
            return ingredient;
        }
        return new Ingredient();
    }

    public static <T extends ResultTarget> Result<T> loadResult(JsonNode node) {
        if (node.isArray()) {
            Result<T> result = new Result<>();
            node.elements().forEachRemaining(item -> {
                APIReference reference = JacksonUtil.getObjectMapper().convertValue(item, APIReference.class);
                if (reference != null) {
                    result.getItems().add(reference);
                }
            });
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

    public static CustomItem load(JsonNode node) {
        return load(JacksonUtil.getObjectMapper().convertValue(node, APIReference.class));
    }

    public static CustomItem load(APIReference reference) {
        CustomItem customItem = CustomItem.of(reference);
        if (customItem == null && reference instanceof WolfyUtilitiesRef) {
            customItem = Registry.CUSTOM_ITEMS.get(NamespacedKeyUtils.fromInternal(((WolfyUtilitiesRef) reference).getNamespacedKey()));
        }
        if (customItem != null && customItem.hasNamespacedKey()) {
            customItem = customItem.clone();
            customItem.setAmount(reference.getAmount());
        }
        return customItem;
    }

    public static void saveItem(NamespacedKey namespacedKey, CustomItem customItem) {
        if (namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            NamespacedKey internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
            if (CustomCrafting.inst().hasDataBaseHandler()) {
                CustomCrafting.inst().getDataBaseHandler().updateItem(internalKey, customItem);
            } else {
                try {
                    File file = new File(DataHandler.DATA_FOLDER + File.separator + internalKey.getNamespace() + File.separator + "items", internalKey.getKey() + ".json");
                    file.getParentFile().mkdirs();
                    if (file.exists() || file.createNewFile()) {
                        JacksonUtil.getObjectWriter(CustomCrafting.inst().getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, customItem);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            me.wolfyscript.customcrafting.Registry.CUSTOM_ITEMS.register(NamespacedKeyUtils.fromInternal(internalKey), customItem);
        }
    }

    public static boolean deleteItem(NamespacedKey namespacedKey, @Nullable Player player) {
        if (namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            if (!me.wolfyscript.customcrafting.Registry.CUSTOM_ITEMS.has(namespacedKey)) {
                if (player != null) CustomCrafting.inst().getApi().getChat().sendMessage(player, "error");
                return false;
            }
            me.wolfyscript.customcrafting.Registry.CUSTOM_ITEMS.remove(namespacedKey);
            System.gc();
            NamespacedKey internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
            if (CustomCrafting.inst().hasDataBaseHandler()) {
                CustomCrafting.inst().getDataBaseHandler().removeItem(internalKey);
                return true;
            } else {
                File file = new File(DataHandler.DATA_FOLDER + File.separator + internalKey.getNamespace() + File.separator + "items", internalKey.getKey() + ".json");
                if (file.delete()) {
                    if (player != null)
                        CustomCrafting.inst().getApi().getChat().sendMessage(player, "&aCustomItem deleted!");
                    return true;
                } else {
                    file.deleteOnExit();
                    if (player != null)
                        CustomCrafting.inst().getApi().getChat().sendMessage(player, "&cCouldn't delete CustomItem on runtime! File is being deleted on restart!");
                }
            }
        }
        return false;
    }

}
