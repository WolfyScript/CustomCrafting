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

package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ClusterRecipeBookEditor extends CCCluster {

    public static final String KEY = "recipe_book_editor";

    public static final NamespacedKey BACK = new NamespacedKey(KEY, "back");
    public static final NamespacedKey SAVE = new NamespacedKey(KEY, "save");
    public static final NamespacedKey SAVE_AS = new NamespacedKey(KEY, "save_as");
    public static final NamespacedKey ICON = new NamespacedKey(KEY, "icon");
    public static final NamespacedKey NAME = new NamespacedKey(KEY, "name");
    public static final NamespacedKey DESCRIPTION_ADD = new NamespacedKey(KEY, "description.add");
    public static final NamespacedKey DESCRIPTION_REMOVE = new NamespacedKey(KEY, "description.remove");
    public static final NamespacedKey RECIPES = new NamespacedKey(KEY, "recipes");
    public static final NamespacedKey FOLDERS = new NamespacedKey(KEY, "folders");
    public static final NamespacedKey GROUPS = new NamespacedKey(KEY, "groups");

    public ClusterRecipeBookEditor(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new EditorMain(this, customCrafting));
        registerGuiWindow(new OverviewCategories(this, customCrafting));
        registerGuiWindow(new OverviewFilters(this, customCrafting));
        registerGuiWindow(new EditCategory(this, customCrafting));
        registerGuiWindow(new EditFilter(this, customCrafting));

        registerButton(new ButtonSaveCategory(false, customCrafting));
        registerButton(new ButtonSaveCategory(true, customCrafting));
        var btnBld = getButtonBuilder();
        btnBld.action(BACK.getKey()).state(state -> state.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeBookEditor().setFilter(null);
            cache.getRecipeBookEditor().setCategory(null);
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        btnBld.itemInput(ICON.getKey()).state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, inventory, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getRecipeBookEditor().getCategorySetting().setIconStack(inventory.getItem(slot));
                } else {
                    cache.getRecipeBookEditor().getCategorySetting().setIconStack(new ItemStack(Material.AIR));
                }
            });
            return false;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var categorySettings = guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting();
            return CallbackButtonRender.UpdateResult.of(categorySettings != null ? categorySettings.getIconStack() : new ItemStack(Material.AIR));
        })).register();
        btnBld.chatInput(NAME.getKey()).state(state -> state.icon(Material.NAME_TAG).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("name", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getName())))).inputAction((guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().setName(s);
            return false;
        }).register();
        btnBld.chatInput(DESCRIPTION_ADD.getKey()).state(state -> state.icon(Material.WRITABLE_BOOK).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("description", String.join("<newline> ", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getDescription()))))).inputAction((guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getDescription().add(s.equals("&empty") ? "" : ChatColor.convert(s));
            return false;
        }).register();
        btnBld.action(DESCRIPTION_REMOVE.getKey()).state(state -> state.icon(Material.WRITTEN_BOOK).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            ChatUtils.sendCategoryDescription(player);
            guiHandler.close();
            return true;
        })).register();
        btnBld.action(RECIPES.getKey()).state(state -> state.icon(Material.CRAFTING_TABLE).action((cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            if (event instanceof InventoryClickEvent clickEvent) {
                boolean remove = clickEvent.isRightClick();
                List<String> recipeKeys = customCrafting.getRegistries().getRecipes().keySet().stream().map(NamespacedKey::toString).toList();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> StringUtil.copyPartialMatches(args[0], recipeKeys, new ArrayList<>()));
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 0) {
                        var namespacedKey = NamespacedKey.of(args[0]);
                        if (customCrafting.getRegistries().getRecipes().get(namespacedKey) == null) {
                            getChat().sendMessage(player, translatedMsgKey("not_existing", Placeholder.unparsed("recipe", args[0])));
                            return true;
                        }
                        if (remove) {
                            cache.getRecipeBookEditor().getCategorySetting().getRecipes().remove(namespacedKey);
                        } else {
                            cache.getRecipeBookEditor().getCategorySetting().getRecipes().add(namespacedKey);
                        }
                    }
                    return false;
                });
                Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("recipes", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getRecipes().stream().map(recipe -> "<grey> - </grey><yellow>" + recipe + "</yellow>").toList()))))).register();
        btnBld.action(FOLDERS.getKey()).state(state -> state.icon(Material.ENDER_CHEST).action((cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            if (event instanceof InventoryClickEvent clickEvent) {
                boolean remove = clickEvent.isRightClick();
                List<String> namespaces = customCrafting.getRegistries().getRecipes().namespaces();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> StringUtil.copyPartialMatches(args[0], namespaces, new ArrayList<>()));
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 0) {
                        String namespace = args[0];
                        if (namespace == null || namespace.isEmpty()) {
                            return true;
                        }
                        if (remove) {
                            cache.getRecipeBookEditor().getCategorySetting().getFolders().remove(namespace);
                        } else {
                            cache.getRecipeBookEditor().getCategorySetting().getFolders().add(namespace);
                        }
                    }
                    return false;
                });
                Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("folders", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getFolders().stream().map(namespacedKey -> "<grey> - </grey><yellow>" + namespacedKey + "</yellow>").toList()))))).register();
        btnBld.action(GROUPS.getKey()).state(state -> state.icon(Material.BOOKSHELF).action((cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            if (event instanceof InventoryClickEvent clickEvent) {
                boolean remove = clickEvent.isRightClick();
                List<String> groups = customCrafting.getRegistries().getRecipes().groups();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> StringUtil.copyPartialMatches(args[0], groups, new ArrayList<>()));
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 0) {
                        String group = args[0];
                        if (group == null || group.isEmpty()) {
                            return true;
                        }
                        if (remove) {
                            cache.getRecipeBookEditor().getCategorySetting().getGroups().remove(group);
                        } else {
                            cache.getRecipeBookEditor().getCategorySetting().getGroups().add(group);
                        }
                    }
                    return false;
                });
                Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("groups", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getGroups().stream().map(group -> "<grey> - </grey><yellow>" + group + "</yellow>").toList()))))).register();
    }
}
