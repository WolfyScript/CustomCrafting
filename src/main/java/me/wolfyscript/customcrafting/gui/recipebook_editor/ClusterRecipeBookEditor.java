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

import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.chat.CollectionEditor;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.MiniMessage;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ClusterRecipeBookEditor extends CCCluster {

    public static final String KEY = "recipe_book_editor";

    private CollectionEditor<CCCache, String> descriptionChatEditor;
    private CollectionEditor<CCCache, NamespacedKey> recipesChatEditor;
    private CollectionEditor<CCCache, String> foldersChatEditor;
    private CollectionEditor<CCCache, String> groupsChatEditor;

    public static final NamespacedKey BACK = new NamespacedKey(KEY, "back");
    public static final NamespacedKey NEXT_PAGE = new NamespacedKey(KEY, "next_page");
    public static final NamespacedKey PREVIOUS_PAGE = new NamespacedKey(KEY, "previous_page");
    public static final NamespacedKey SAVE = new NamespacedKey(KEY, "save");
    public static final NamespacedKey SAVE_AS = new NamespacedKey(KEY, "save_as");
    public static final NamespacedKey ICON = new NamespacedKey(KEY, "icon");
    public static final NamespacedKey NAME = new NamespacedKey(KEY, "name");
    public static final NamespacedKey DESCRIPTION_EDIT = new NamespacedKey(KEY, "description.edit");
    public static final NamespacedKey RECIPES = new NamespacedKey(KEY, "recipes");
    public static final NamespacedKey FOLDERS = new NamespacedKey(KEY, "folders");
    public static final NamespacedKey GROUPS = new NamespacedKey(KEY, "groups");
    public static final NamespacedKey NAMESPACES = new NamespacedKey(KEY, "namespaces");

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
                (guiHandler, player, cache) -> cache.getRecipeBookEditorCache().getCategorySetting().getDescription(),
                (guiHandler, player, cache, line) -> getChat().getMiniMessage().deserialize(line),
                (guiHandler, player, cache, msg, args) -> getChat().getMiniMessage().serialize(getChat().getMiniMessage().deserialize(msg)))
                .onAdd((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditorCache().getCategorySetting().getDescription().add(entry))
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditorCache().getCategorySetting().getDescription().remove(index))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> cache.getRecipeBookEditorCache().getCategorySetting().getDescription().set(index, newEntry))
                .onMove((guiHandler, player, cache, fromIndex, toIndex) -> {
                    List<String> description = cache.getRecipeBookEditorCache().getCategorySetting().getDescription();
                    String prevTo = description.get(toIndex);
                    description.set(toIndex, description.get(fromIndex));
                    description.set(fromIndex, prevTo);
                })
                .setSendInputInfoMessages((guiHandler, player, cache) -> {
                    getChat().sendMessage(player, getChat().translated("msg.input.mini_message"));
                    getChat().sendMessage(player, getChat().translated("msg.input.wui_command"));
                });

        this.recipesChatEditor = new CollectionEditor<>(getInventoryAPI(), (guiHandler, player, cache) -> cache.getRecipeBookEditorCache().getCategorySetting().getRecipes(), (guiHandler, player, cache, recipeId) -> BukkitComponentSerializer.legacy().deserialize(recipeId.toString()), (guiHandler, player, cache, msg, args) -> {
            if (args.length > 0) {
                var namespacedKey = NamespacedKey.of(args[0]);
                if (customCrafting.getRegistries().getRecipes().get(namespacedKey) != null) {
                    return namespacedKey;
                }
                getChat().sendMessage(player, translatedMsgKey("not_existing", Placeholder.unparsed("recipe", args[0])));
            }
            return null;
        })
                .onAdd((guiHandler, player, cache, index, entry) -> guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getRecipes().add(entry))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> {
                    cache.getRecipeBookEditorCache().getCategorySetting().getRecipes().remove(previousEntry);
                    cache.getRecipeBookEditorCache().getCategorySetting().getRecipes().add(newEntry);
                })
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditorCache().getCategorySetting().getRecipes().remove(entry))
                .setTabComplete((guiHandler, sender, args) -> StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().keySet().stream().map(NamespacedKey::toString).toList(), new ArrayList<>()));

        this.foldersChatEditor = new CollectionEditor<>(getInventoryAPI(), (guiHandler, player, cache) -> cache.getRecipeBookEditorCache().getCategorySetting().getFolders(), (guiHandler, player, cache, folder) -> BukkitComponentSerializer.legacy().deserialize(folder), (guiHandler, player, cache, msg, args) -> {
            if (args.length > 0) {
                String namespace = args[0];
                if (namespace != null && !namespace.isEmpty()) {
                    return namespace;
                }
            }
            return null;
        })
                .onAdd((guiHandler, player, cache, index, entry) -> guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getFolders().add(entry))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> {
                    cache.getRecipeBookEditorCache().getCategorySetting().getFolders().remove(previousEntry);
                    cache.getRecipeBookEditorCache().getCategorySetting().getFolders().add(newEntry);
                })
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditorCache().getCategorySetting().getFolders().remove(entry))
                .setTabComplete((guiHandler, sender, args) -> StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().dirs(NamespacedKeyUtils.NAMESPACE), new ArrayList<>()));

        this.groupsChatEditor = new CollectionEditor<>(getInventoryAPI(), (guiHandler, player, cache) -> cache.getRecipeBookEditorCache().getCategorySetting().getGroups(), (guiHandler, player, cache, group) -> BukkitComponentSerializer.legacy().deserialize(group), (guiHandler, player, cache, msg, args) -> {
            if (args.length > 0) {
                String group = args[0];
                if (group != null && !group.isEmpty()) {
                    return group;
                }
            }
            return null;
        })
                .onAdd((guiHandler, player, cache, index, entry) -> guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getGroups().add(entry))
                .onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> {
                    cache.getRecipeBookEditorCache().getCategorySetting().getGroups().remove(previousEntry);
                    cache.getRecipeBookEditorCache().getCategorySetting().getGroups().add(newEntry);
                })
                .onRemove((guiHandler, player, cache, index, entry) -> cache.getRecipeBookEditorCache().getCategorySetting().getGroups().remove(entry))
                .setTabComplete((guiHandler, sender, args) -> StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().groups(), new ArrayList<>()));

        getButtonBuilder().action(PREVIOUS_PAGE.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"))).register();
        getButtonBuilder().action(NEXT_PAGE.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"))).register();

        ButtonSaveCategory.registerSave(getButtonBuilder());
        ButtonSaveCategory.registerSaveAs(getButtonBuilder());

        var btnBld = getButtonBuilder();
        btnBld.action(BACK.getKey()).state(state -> state.key(ClusterMain.BACK_BOTTOM).icon(Material.BARRIER).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeBookEditorCache().setFilter(null);
            cache.getRecipeBookEditorCache().setCategory(null);
            cache.getRecipeBookEditorCache().setCategoryID("");
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        btnBld.itemInput(ICON.getKey()).state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, inventory, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getRecipeBookEditorCache().getCategorySetting().setIconStack(inventory.getItem(slot));
                } else {
                    cache.getRecipeBookEditorCache().getCategorySetting().setIconStack(new ItemStack(Material.AIR));
                }
            });
            return false;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var categorySettings = guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting();
            return CallbackButtonRender.UpdateResult.of(categorySettings != null ? categorySettings.getIconStack() : new ItemStack(Material.AIR));
        })).register();
        btnBld.chatInput(NAME.getKey()).state(state -> state.icon(Material.NAME_TAG)
                        .render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("name", guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getName())))
                        .action((cache, guiHandler, player, guiInventory, i, event) -> {
                            getChat().sendMessage(player, getChat().translated("msg.input.mini_message"));
                            getChat().sendMessage(player, getChat().translated("msg.input.wui_command"));
                            return true;
                        })
                )
                .inputAction((guiHandler, player, s, strings) -> {
                    guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().setName(s);
                    return false;
                }).register();
        btnBld.action(DESCRIPTION_EDIT.getKey()).state(state -> state.icon(Material.WRITTEN_BOOK).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            descriptionChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
                    List<String> description = guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getDescription();
                    LanguageAPI langAPI = wolfyUtilities.getLanguageAPI();
                    MiniMessage miniMsg = getChat().getMiniMessage();
                    return CallbackButtonRender.UpdateResult.of(TagResolverUtil.entries(langAPI.replaceKeys(description).stream().map(s -> miniMsg.deserialize(langAPI.convertLegacyToMiniMessage(s))).toList()));
                }
        )).register();

        btnBld.action(RECIPES.getKey()).state(state -> state.icon(Material.CRAFTING_TABLE).action((cache, guiHandler, player, guiInventory, i, event) -> {
            recipesChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("recipes", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getRecipes().stream().map(recipe -> "<grey> - </grey><yellow>" + recipe + "</yellow>").toList()))))).register();
        btnBld.action(FOLDERS.getKey()).state(state -> state.icon(Material.ENDER_CHEST).action((cache, guiHandler, player, guiInventory, i, event) -> {
            foldersChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("folders", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getFolders().stream().map(namespacedKey -> "<grey> - </grey><yellow>" + namespacedKey + "</yellow>").toList()))))).register();
        btnBld.action(GROUPS.getKey()).state(state -> state.icon(Material.BOOKSHELF).action((cache, guiHandler, player, guiInventory, i, event) -> {
            groupsChatEditor.send(player);
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("groups", String.join("<newline>", guiHandler.getCustomCache().getRecipeBookEditorCache().getCategorySetting().getGroups().stream().map(group -> "<grey> - </grey><yellow>" + group + "</yellow>").toList()))))).register();
    }
}
