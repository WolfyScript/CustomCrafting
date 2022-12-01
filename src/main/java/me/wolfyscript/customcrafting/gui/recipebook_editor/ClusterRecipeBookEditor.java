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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.language.LanguageAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.chat.CollectionEditor;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

public class ClusterRecipeBookEditor extends CCCluster {

    public static final String KEY = "recipe_book_editor";

    private CollectionEditor<CCCache, String> descriptionChatEditor;
    private CollectionEditor<CCCache, NamespacedKey> recipesChatEditor;
    private CollectionEditor<CCCache, String> foldersChatEditor;
    private CollectionEditor<CCCache, String> groupsChatEditor;

    public static final NamespacedKey BACK = new BukkitNamespacedKey(KEY, "back");
    public static final NamespacedKey NEXT_PAGE = new BukkitNamespacedKey(KEY, "next_page");
    public static final NamespacedKey PREVIOUS_PAGE = new BukkitNamespacedKey(KEY, "previous_page");
    public static final NamespacedKey SAVE = new BukkitNamespacedKey(KEY, "save");
    public static final NamespacedKey SAVE_AS = new BukkitNamespacedKey(KEY, "save_as");
    public static final NamespacedKey ICON = new BukkitNamespacedKey(KEY, "icon");
    public static final NamespacedKey NAME = new BukkitNamespacedKey(KEY, "name");
    public static final NamespacedKey DESCRIPTION_EDIT = new BukkitNamespacedKey(KEY, "description.edit");
    public static final NamespacedKey RECIPES = new BukkitNamespacedKey(KEY, "recipes");
    public static final NamespacedKey FOLDERS = new BukkitNamespacedKey(KEY, "folders");
    public static final NamespacedKey GROUPS = new BukkitNamespacedKey(KEY, "groups");

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

        this.descriptionChatEditor = new CollectionEditor<>(getInventoryAPI(),
                (guiHandler, player, cache) -> cache.getRecipeBookEditor().getCategorySetting().getDescription(),
                (guiHandler, player, cache, line) -> getChat().getMiniMessage().deserialize(line),
                (guiHandler, player, cache, msg, args) -> BukkitComponentSerializer.legacy().serialize(getChat().getMiniMessage().deserialize(msg)))
                .onAdd((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditor().getCategorySetting().getDescription().add(entry))
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditor().getCategorySetting().getDescription().remove(index))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> cache.getRecipeBookEditor().getCategorySetting().getDescription().set(index, newEntry))
                .onMove((guiHandler, player, cache, fromIndex, toIndex) -> {
                    List<String> description = cache.getRecipeBookEditor().getCategorySetting().getDescription();
                    String prevTo = description.get(toIndex);
                    description.set(toIndex, description.get(fromIndex));
                    description.set(fromIndex, prevTo);
                })
                .setSendInputInfoMessages((guiHandler, player, cache) -> {
                    getChat().sendMessage(player, getChat().translated("msg.input.mini_message"));
                    getChat().sendMessage(player, getChat().translated("msg.input.wui_command"));
                });

        this.recipesChatEditor = new CollectionEditor<>(getInventoryAPI(), (guiHandler, player, cache) -> cache.getRecipeBookEditor().getCategorySetting().getRecipes(), (guiHandler, player, cache, recipeId) -> BukkitComponentSerializer.legacy().deserialize(recipeId.toString()), (guiHandler, player, cache, msg, args) -> {
            if (args.length > 0) {
                var namespacedKey = customCrafting.getApi().getIdentifiers().getNamespaced(args[0]);
                if (customCrafting.getRegistries().getRecipes().get(namespacedKey) != null) {
                    return namespacedKey;
                }
                getChat().sendMessage(player, translatedMsgKey("not_existing", Placeholder.unparsed("recipe", args[0])));
            }
            return null;
        })
                .onAdd((guiHandler, player, cache, index, entry) -> guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getRecipes().add(entry))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> {
                    cache.getRecipeBookEditor().getCategorySetting().getRecipes().remove(previousEntry);
                    cache.getRecipeBookEditor().getCategorySetting().getRecipes().add(newEntry);
                })
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditor().getCategorySetting().getRecipes().remove(entry))
                .setTabComplete((guiHandler, sender, args) -> StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().keySet().stream().map(NamespacedKey::toString).toList(), new ArrayList<>()));

        this.foldersChatEditor = new CollectionEditor<>(getInventoryAPI(), (guiHandler, player, cache) -> cache.getRecipeBookEditor().getCategorySetting().getFolders(), (guiHandler, player, cache, folder) -> BukkitComponentSerializer.legacy().deserialize(folder), (guiHandler, player, cache, msg, args) -> {
            if (args.length > 0) {
                String namespace = args[0];
                if (namespace != null && !namespace.isEmpty()) {
                    return namespace;
                }
            }
            return null;
        })
                .onAdd((guiHandler, player, cache, index, entry) -> guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getFolders().add(entry))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> {
                    cache.getRecipeBookEditor().getCategorySetting().getFolders().remove(previousEntry);
                    cache.getRecipeBookEditor().getCategorySetting().getFolders().add(newEntry);
                })
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditor().getCategorySetting().getFolders().remove(entry))
                .setTabComplete((guiHandler, sender, args) -> StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().folders(NamespacedKeyUtils.NAMESPACE), new ArrayList<>()));

        this.groupsChatEditor = new CollectionEditor<>(getInventoryAPI(), (guiHandler, player, cache) -> cache.getRecipeBookEditor().getCategorySetting().getGroups(), (guiHandler, player, cache, group) -> BukkitComponentSerializer.legacy().deserialize(group), (guiHandler, player, cache, msg, args) -> {
            if (args.length > 0) {
                String group = args[0];
                if (group != null && !group.isEmpty()) {
                    return group;
                }
            }
            return null;
        })
                .onAdd((guiHandler, player, cache, index, entry) -> guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getGroups().add(entry))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> {
                    cache.getRecipeBookEditor().getCategorySetting().getGroups().remove(previousEntry);
                    cache.getRecipeBookEditor().getCategorySetting().getGroups().add(newEntry);
                })
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditor().getCategorySetting().getGroups().remove(entry))
                .setTabComplete((guiHandler, sender, args) -> StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().groups(), new ArrayList<>()));

        getButtonBuilder().action(PREVIOUS_PAGE.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"))).register();
        getButtonBuilder().action(NEXT_PAGE.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"))).register();

        getButtonBuilder().action(ClusterRecipeBookEditor.SAVE.getKey()).state(state -> state.icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, guiInventory, button, i, inventoryInteractEvent) -> {
            var recipeBookEditor = cache.getRecipeBookEditor();
            if (recipeBookEditor.hasCategoryID()) {
                WolfyUtilsBukkit api = guiHandler.getWolfyUtils();
                if (saveCategorySetting(recipeBookEditor, customCrafting)) {
                    guiHandler.openPreviousWindow();
                } else {
                    api.getChat().sendKey(player, ClusterRecipeBookEditor.KEY, "save.error");
                }
            }
            return true;
        })).register();
        getButtonBuilder().chatInput(ClusterRecipeBookEditor.SAVE_AS.getKey()).state(state -> state.icon(Material.WRITABLE_BOOK)).tabComplete((guiHandler, player, args) -> {
            List<String> results = new ArrayList<>();
            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], customCrafting.getConfigHandler().getRecipeBookConfig().getCategories().keySet(), results);
            }
            Collections.sort(results);
            return results;
        }).message(translatedMsgKey(("save.input"))).inputAction((guiHandler, player, s, strings) -> {
            var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
            if (s != null && !s.isEmpty() && recipeBookEditor.setCategoryID(s)) {
                if (saveCategorySetting(recipeBookEditor, customCrafting)) {
                    guiHandler.openPreviousWindow();
                    return true;
                }
                getChat().sendKey(player, ClusterRecipeBookEditor.KEY, "save.error");
            }
            return false;
        }).register();


        var btnBld = getButtonBuilder();
        btnBld.action(BACK.getKey()).state(state -> state.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, guiInventory, btn, i, inventoryInteractEvent) -> {
            cache.getRecipeBookEditor().setFilter(null);
            cache.getRecipeBookEditor().setCategory(null);
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        btnBld.itemInput(ICON.getKey()).state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, inventory, btn, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getRecipeBookEditor().getCategorySetting().setIconStack(inventory.getItem(slot));
                } else {
                    cache.getRecipeBookEditor().getCategorySetting().setIconStack(new ItemStack(Material.AIR));
                }
            });
            return false;
        }).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> {
            var categorySettings = guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting();
            return CallbackButtonRender.UpdateResult.of(categorySettings != null ? categorySettings.getIconStack() : new ItemStack(Material.AIR));
        })).register();
        btnBld.chatInput(NAME.getKey()).state(state -> state.icon(Material.NAME_TAG).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("name", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getName())))).inputAction((guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().setName(s);
            return false;
        }).register();
        btnBld.action(DESCRIPTION_EDIT.getKey()).state(state -> state.icon(Material.WRITTEN_BOOK).action((cache, guiHandler, player, guiInventory, btn, i, inventoryInteractEvent) -> {
            descriptionChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> {
                    List<String> description = guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getDescription();
                    LanguageAPI langAPI = wolfyUtilities.getLanguageAPI();
                    MiniMessage miniMsg = getChat().getMiniMessage();
                    return CallbackButtonRender.UpdateResult.of(TagResolverUtil.entries(langAPI.replaceKeys(description).stream().map(s -> miniMsg.deserialize(langAPI.convertLegacyToMiniMessage(s))).toList()));
                }
        )).register();

        btnBld.action(RECIPES.getKey()).state(state -> state.icon(Material.CRAFTING_TABLE).action((cache, guiHandler, player, guiInventory, btn, i, event) -> {
            recipesChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("recipes", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getRecipes().stream().map(recipe -> "<grey> - </grey><yellow>" + recipe + "</yellow>").toList()))))).register();
        btnBld.action(FOLDERS.getKey()).state(state -> state.icon(Material.ENDER_CHEST).action((cache, guiHandler, player, guiInventory, btn, i, event) -> {
            foldersChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("folders", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getFolders().stream().map(namespacedKey -> "<grey> - </grey><yellow>" + namespacedKey + "</yellow>").toList()))))).register();
        btnBld.action(GROUPS.getKey()).state(state -> state.icon(Material.BOOKSHELF).action((cache, guiHandler, player, guiInventory, btn, i, event) -> {
            groupsChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("groups", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getGroups().stream().map(group -> "<grey> - </grey><yellow>" + group + "</yellow>").toList()))))).register();
    }

    private static boolean saveCategorySetting(RecipeBookEditor recipeBookEditor, CustomCrafting customCrafting) {
        var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
        CategorySettings category = recipeBookEditor.getCategorySetting();
        if (ItemUtils.isAirOrNull(category.getIconStack())) {
            return false;
        }
        if (category instanceof CategoryFilter filter) {
            recipeBook.registerFilter(recipeBookEditor.getCategoryID(), filter);
            recipeBookEditor.setFilter(null);
        } else {
            recipeBook.registerCategory(recipeBookEditor.getCategoryID(), (Category) category);
            recipeBookEditor.setCategory(null);
        }
        recipeBookEditor.setCategoryID("");
        return true;
    }
}
