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
import me.wolfyscript.lib.com.fasterxml.jackson.databind.InjectableValues;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ItemLoader {

    private static final org.bukkit.NamespacedKey customItemContainerKey = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");

    private ItemLoader() {
    }

    private static ObjectMapper getObjectMapper() {
        return CustomCrafting.inst().getApi().getJacksonMapperUtil().getGlobalMapper();
    }

    /**
     * Loads the {@link Ingredient} from the specified node.
     *
     * @param node The JsonNode to read from.
     * @return The Ingredient if available; otherwise a new Ingredient instance.
     */
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
            ingredient = getObjectMapper().convertValue(node, Ingredient.class);
        }
        if (ingredient != null) {
            ingredient.buildChoices();
            return ingredient;
        }
        return new Ingredient();
    }

    /**
     * Loads the result from the specified node
     *
     * @param node The JsonNode to read the result from.
     * @return The loaded result or a new Result instance.
     * @deprecated {@link #loadResult(JsonNode, CustomCrafting)} should be used instead if possible!
     */
    @NotNull
    @Deprecated
    public static Result loadResult(JsonNode node) {
        return loadResult(node, CustomCrafting.inst());
    }

    /**
     * Loads the {@link Result} from the specified node.
     *
     * @param node           The JsonNode to read from.
     * @param customCrafting The instance of the plugin.
     * @return The loaded Result if available; otherwise a new Result instance.
     */
    @NotNull
    public static Result loadResult(JsonNode node, CustomCrafting customCrafting) {
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
            var injects = new InjectableValues.Std();
            injects.addValue("customcrafting", customCrafting);
            Result desResult = null;
            try {
                desResult = getObjectMapper().reader(injects).readValue(node, Result.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                result = desResult;
            }
        }
        if (result != null) {
            result.buildChoices();
            return result;
        }
        return new Result();
    }

    /**
     * Fixes an {@link APIReference} that was possibly corrupted by an older version of CustomCrafting, that had a bug, that serialized them incorrectly.
     *
     * @param itemNode The JsonNode to load and fix the reference from.
     * @return The loaded and fixed APIReference or null.
     */
    @Nullable
    private static APIReference loadAndConvertCorruptReference(JsonNode itemNode) {
        APIReference reference = getObjectMapper().convertValue(itemNode, APIReference.class);
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
                var registry = WolfyUtilCore.getInstance().getRegistries().getCustomItems();
                if (!oldNamespacedKey.getKey().contains("/") && !registry.has(oldNamespacedKey)) {
                    var namespacedKey = NamespacedKeyUtils.fromInternal(wolfyUtilitiesRef.getNamespacedKey());
                    if (registry.has(namespacedKey)) {
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
        return load(getObjectMapper().convertValue(node, APIReference.class));
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
            customItem.setNamespacedKey(namespacedKey);
            loader.save(customItem);
            WolfyUtilCore.getInstance().getRegistries().getCustomItems().register(customItem);
        }
    }

    public static boolean deleteItem(ResourceLoader loader, NamespacedKey namespacedKey, @Nullable Player player) {
        if (namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            var registry = WolfyUtilCore.getInstance().getRegistries().getCustomItems();
            if (!registry.has(namespacedKey)) {
                if (player != null) CustomCrafting.inst().getApi().getChat().sendMessage(player, "error");
                return false;
            }
            CustomItem item = registry.get(namespacedKey);
            registry.remove(namespacedKey);
            try {
                if (loader.delete(item)) {
                    CustomCrafting.inst().getApi().getChat().sendMessage(player, Component.text("CustomItem deleted!", NamedTextColor.GREEN));
                    return true;
                }
            } catch (IOException e) {
                CustomCrafting.inst().getApi().getChat().sendMessage(player, Component.text("Couldn't delete CustomItem file! " + e.getMessage(), NamedTextColor.RED));
                CustomCrafting.inst().getApi().getChat().sendMessage(player, Component.text("For full error please see logs!", NamedTextColor.RED));
                e.printStackTrace();
            }
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
                    var registry = WolfyUtilCore.getInstance().getRegistries().getCustomItems();
                    if (itemKey != null && !registry.has(itemKey)) {
                        var updatedKey = NamespacedKeyUtils.fromInternal(itemKey);
                        if (registry.has(updatedKey)) {
                            container.set(customItemContainerKey, PersistentDataType.STRING, updatedKey.toString());
                        }
                    }
                }
            }
        }
    }


}
