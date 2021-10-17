/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.ResourceLoader;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ItemLoader {

    private static final org.bukkit.NamespacedKey customItemContainerKey = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");

    private ItemLoader() {
    }

    public static Ingredient loadIngredient(JsonNode node) {
        final Ingredient ingredient;
        if (node.isArray()) {
            ingredient = new Ingredient();
            node.elements().forEachRemaining(item -> {
                APIReference reference = loadAndConvertCorruptReference(item);
                if (reference != null) {
                    ingredient.getItems().add(reference);
                }
            });
        } else {
            ingredient = JacksonUtil.getObjectMapper().convertValue(node, Ingredient.class);
        }
        if (ingredient != null) {
            ingredient.buildChoices();
            return ingredient;
        }
        return new Ingredient();
    }

    public static Result loadResult(JsonNode node) {
        final Result result;
        if (node.isArray()) {
            result = new Result();
            node.elements().forEachRemaining(item -> {
                APIReference reference = loadAndConvertCorruptReference(item);
                if (reference != null) {
                    result.getItems().add(reference);
                }
            });
        } else {
            result = JacksonUtil.getObjectMapper().convertValue(node, Result.class);
        }
        if (result != null) {
            result.buildChoices();
            return result;
        }
        return new Result();
    }

    private static APIReference loadAndConvertCorruptReference(JsonNode itemNode) {
        APIReference reference = JacksonUtil.getObjectMapper().convertValue(itemNode, APIReference.class);
        if (CustomCrafting.inst().getConfigHandler().getConfig().getDataVersion() < CustomCrafting.CONFIG_VERSION && reference != null) {
            if (reference instanceof VanillaRef) {
                //Check for possible APIReference that could be used!
                CustomItem customItem = CustomItem.getReferenceByItemStack(reference.getLinkedItem());
                if (customItem != null && !(customItem.getApiReference() instanceof VanillaRef)) {
                    //Another APIReference type was found!
                    APIReference updatedReference = customItem.getApiReference();
                    updatedReference.setAmount(reference.getAmount());
                    reference = updatedReference;
                }
            }
            //Update NamespacedKey of old WolfyUtilityReference
            if (reference instanceof WolfyUtilitiesRef wolfyUtilitiesRef) {
                var oldNamespacedKey = wolfyUtilitiesRef.getNamespacedKey();
                if (!oldNamespacedKey.getKey().contains("/") && !Registry.CUSTOM_ITEMS.has(oldNamespacedKey)) {
                    var namespacedKey = NamespacedKeyUtils.fromInternal(wolfyUtilitiesRef.getNamespacedKey());
                    if (Registry.CUSTOM_ITEMS.has(namespacedKey)) {
                        var wuRef = new WolfyUtilitiesRef(namespacedKey);
                        wuRef.setAmount(wolfyUtilitiesRef.getAmount());
                        return wuRef;
                    }
                }
            }
        }
        return reference;
    }

    public static CustomItem load(JsonNode node) {
        return load(JacksonUtil.getObjectMapper().convertValue(node, APIReference.class));
    }

    public static CustomItem load(APIReference reference) {
        var customItem = CustomItem.of(reference);
        if (customItem != null && customItem.hasNamespacedKey()) {
            customItem = customItem.clone();
            customItem.setAmount(reference.getAmount());
        }
        return customItem;
    }

    public static void saveItem(NamespacedKey namespacedKey, CustomItem customItem) {
        saveItem(CustomCrafting.inst().getDataHandler().getActiveLoader(), namespacedKey, customItem);
    }

    public static void saveItem(ResourceLoader loader, NamespacedKey namespacedKey, CustomItem customItem) {
        if (namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            var internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
            customItem.setNamespacedKey(internalKey);
            loader.save(customItem);
            Registry.CUSTOM_ITEMS.register(NamespacedKeyUtils.fromInternal(internalKey), customItem);
        }
    }

    public static boolean deleteItem(ResourceLoader loader, NamespacedKey namespacedKey, @Nullable Player player) {
        if (namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            if (!Registry.CUSTOM_ITEMS.has(namespacedKey)) {
                if (player != null) CustomCrafting.inst().getApi().getChat().sendMessage(player, "error");
                return false;
            }
            CustomItem item = Registry.CUSTOM_ITEMS.get(namespacedKey);
            Registry.CUSTOM_ITEMS.remove(namespacedKey);
            loader.delete(item);
        }
        return false;
    }

    public static boolean deleteItem(NamespacedKey namespacedKey, @Nullable Player player) {
        return deleteItem(CustomCrafting.inst().getDataHandler().getActiveLoader(), namespacedKey, player);
    }

    public static void updateItem(ItemStack stack) {
        if (stack != null && stack.hasItemMeta()) {
            var itemMeta = stack.getItemMeta();
            if (itemMeta != null && !itemMeta.getPersistentDataContainer().isEmpty()) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(customItemContainerKey, PersistentDataType.STRING)) {
                    var itemKey = NamespacedKey.of(container.get(customItemContainerKey, PersistentDataType.STRING));
                    if (itemKey != null && !Registry.CUSTOM_ITEMS.has(itemKey)) {
                        var updatedKey = NamespacedKeyUtils.fromInternal(itemKey);
                        if (Registry.CUSTOM_ITEMS.has(updatedKey)) {
                            container.set(customItemContainerKey, PersistentDataType.STRING, updatedKey.toString());
                        }
                    }
                }
            }
        }
    }


}
