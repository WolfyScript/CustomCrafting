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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClusterRecipeCreator extends CCCluster {

    public static final String KEY = "recipe_creator";

    //Buttons
    public static final NamespacedKey CONDITIONS = new NamespacedKey(KEY, "conditions");
    public static final NamespacedKey GROUP = new NamespacedKey(KEY, "group");
    public static final NamespacedKey TAGS = new NamespacedKey(KEY, "tags");
    public static final NamespacedKey SAVE = new NamespacedKey(KEY, "save");
    public static final NamespacedKey SAVE_AS = new NamespacedKey(KEY, "save_as");
    public static final NamespacedKey PRIORITY = new NamespacedKey(KEY, "priority");
    public static final NamespacedKey EXACT_META = new NamespacedKey(KEY, "exact_meta");
    public static final NamespacedKey HIDDEN = new NamespacedKey(KEY, "hidden");
    public static final NamespacedKey VANILLA_BOOK = new NamespacedKey(KEY, "vanilla_book");
    public static final String SHAPELESS = "crafting.shapeless";
    public static final String MIRROR_VERTICAL = "crafting.mirror_vertical";
    public static final String MIRROR_HORIZONTAL = "crafting.mirror_horizontal";
    public static final String MIRROR_ROTATION = "crafting.mirror_rotation";
    //Language Keys
    private static final String ENABLED = ".enabled";
    public static final NamespacedKey EXACT_META_ENABLED = enabledKey(EXACT_META.getKey());
    public static final NamespacedKey HIDDEN_ENABLED = enabledKey(HIDDEN.getKey());
    public static final NamespacedKey VANILLA_BOOK_ENABLED = enabledKey(VANILLA_BOOK.getKey());
    public static final NamespacedKey SHAPELESS_ENABLED = enabledKey(SHAPELESS);
    public static final NamespacedKey MIRROR_VERTICAL_ENABLED = enabledKey(MIRROR_VERTICAL);
    public static final NamespacedKey MIRROR_HORIZONTAL_ENABLED = enabledKey(MIRROR_HORIZONTAL);
    public static final NamespacedKey MIRROR_ROTATION_ENABLED = enabledKey(MIRROR_ROTATION);
    private static final String DISABLED = ".disabled";
    public static final NamespacedKey EXACT_META_DISABLED = disabledKey(EXACT_META.getKey());
    public static final NamespacedKey HIDDEN_DISABLED = disabledKey(HIDDEN.getKey());
    public static final NamespacedKey VANILLA_BOOK_DISABLED = disabledKey(VANILLA_BOOK.getKey());
    public static final NamespacedKey SHAPELESS_DISABLED = disabledKey(SHAPELESS);
    public static final NamespacedKey MIRROR_VERTICAL_DISABLED = disabledKey(MIRROR_VERTICAL);
    public static final NamespacedKey MIRROR_HORIZONTAL_DISABLED = disabledKey(MIRROR_HORIZONTAL);
    public static final NamespacedKey MIRROR_ROTATION_DISABLED = disabledKey(MIRROR_ROTATION);

    //Window keys
    public static final NamespacedKey ITEM_EDITOR = new NamespacedKey(KEY, "item_editor");

    private static NamespacedKey enabledKey(String key) {
        return new NamespacedKey(KEY, key + ENABLED);
    }

    private static NamespacedKey disabledKey(String key) {
        return new NamespacedKey(KEY, key + DISABLED);
    }

    public ClusterRecipeCreator(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new RecipeCreatorCrafting(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCooking(this, customCrafting));
        registerGuiWindow(new RecipeCreatorAnvil(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCauldron(this, customCrafting));
        registerGuiWindow(new RecipeCreatorStonecutter(this, customCrafting));
        registerGuiWindow(new RecipeCreatorGrindstone(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCraftingElite(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCraftingEliteSettings(this, customCrafting));
        registerGuiWindow(new RecipeCreatorBrewing(this, customCrafting));
        registerGuiWindow(new RecipeCreatorSmithing(this, customCrafting));
        //Other Menus
        registerGuiWindow(new MenuConditions(this, customCrafting));
        registerGuiWindow(new MenuConditionsAdd(this, customCrafting));
        registerGuiWindow(new MenuResult(this, customCrafting));
        registerGuiWindow(new MenuIngredient(this, customCrafting));
        //Tags
        registerGuiWindow(new MenuTagSettings(this, customCrafting));
        registerGuiWindow(new MenuTagChooseList(this, customCrafting));
        registerGuiWindow(new MenuItemEditor(this, customCrafting));

        registerButton(new ActionButton<>(CONDITIONS.getKey(), Material.CYAN_CONCRETE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("conditions");
            return true;
        }));

        registerButton(new ActionButton<>(TAGS.getKey(), Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow("tag_settings");
            return true;
        }));
        registerButton(new ChatInputButton<>(GROUP.getKey(), new ButtonState<>(GROUP.getKey(), Material.BOOKSHELF, (cache, guiHandler, player, guiInventory, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().isRightClick()) {
                cache.getRecipeCreatorCache().getRecipeCache().setGroup("");
                return false;
            }
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            values.put("%group%", cache.getRecipeCreatorCache().getRecipeCache().getGroup());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            if (args.length > 0) {
                guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().setGroup(args[0]);
            }
            return false;
        }, (guiHandler, player, args) -> {
            List<String> results = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().groups(), results);
            return results;
        }));
        registerButton(new ButtonExactMeta());
        registerButton(new ButtonPriority());
        registerButton(new ButtonHidden());
        registerButton(new ButtonVanillaBook());
        registerSaveButtons();
    }

    private void registerSaveButtons() {
        registerButton(new ActionButton<>(SAVE.getKey(), Material.WRITABLE_BOOK, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            if (guiHandler.getWindow() instanceof RecipeCreator && !cache.getRecipeCreatorCache().getRecipeCache().save(customCrafting, player, guiHandler)) {
                guiHandler.getApi().getChat().sendKey(player, KEY, "save.empty");
            }
            return true;
        }));
        registerButton(new ActionButton<>(SAVE_AS.getKey(), Material.WRITABLE_BOOK, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            if (guiHandler.getWindow() instanceof RecipeCreator recipeCreator) {
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    if (args.length > 0) {
                        if (args.length == 1) {
                            results.add("<namespace>");
                            StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().namespaces(), results);
                        } else if (args.length == 2) {
                            results.add("<key>");
                            StringUtil.copyPartialMatches(args[1], customCrafting.getRegistries().getRecipes().get(args[0]).stream().filter(recipe -> cache.getRecipeCreatorCache().getRecipeType().isInstance(recipe)).map(recipe -> recipe.getNamespacedKey().getKey()).toList(), results);
                        }
                    }
                    Collections.sort(results);
                    return results;
                });
                recipeCreator.openChat(guiHandler.getInvAPI().getGuiCluster(KEY), "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    var namespacedKey = ChatUtils.getInternalNamespacedKey(player1, s, args);
                    if (namespacedKey != null) {
                        cache.getRecipeCreatorCache().getRecipeCache().setKey(namespacedKey);
                        if (!cache.getRecipeCreatorCache().getRecipeCache().save(customCrafting, player, guiHandler)) {
                            guiHandler.getApi().getChat().sendKey(player, KEY, "save.empty");
                            return false;
                        }
                    } else {
                        guiHandler.getApi().getChat().sendKey(player, KEY, "save.key.invalid");
                        return false;
                    }
                    return true;
                });
            }
            return true;
        }));
    }
}
