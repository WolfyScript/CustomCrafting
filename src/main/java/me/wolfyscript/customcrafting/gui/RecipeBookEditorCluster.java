package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook_editor.*;
import me.wolfyscript.customcrafting.gui.recipebook_editor.buttons.SaveCategoryButton;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
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
import java.util.stream.Collectors;

public class RecipeBookEditorCluster extends CCCluster {

    public static final String KEY = "recipe_book_editor";

    public static final NamespacedKey BACK = new NamespacedKey(KEY, "back");
    public static final NamespacedKey SAVE = new NamespacedKey(KEY, "save");
    public static final NamespacedKey SAVE_AS = new NamespacedKey(KEY, "save_as");
    public static final NamespacedKey ICON = new NamespacedKey(KEY, "icon");
    public static final NamespacedKey NAME = new NamespacedKey(KEY, "name");
    public static final NamespacedKey DESCRIPTION_ADD = new NamespacedKey(KEY, "description.add");
    public static final NamespacedKey DESCRIPTION_REMOVE = new NamespacedKey(KEY, "description.remove");
    public static final NamespacedKey RECIPES = new NamespacedKey(KEY, "recipes");
    public static final NamespacedKey NAMESPACES = new NamespacedKey(KEY, "namespaces");
    public static final NamespacedKey GROUPS = new NamespacedKey(KEY, "groups");

    public RecipeBookEditorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new EditorMain(this, customCrafting));
        registerGuiWindow(new OverviewCategories(this, customCrafting));
        registerGuiWindow(new OverviewFilters(this, customCrafting));
        registerGuiWindow(new EditCategory(this, customCrafting));
        registerGuiWindow(new EditFilter(this, customCrafting));

        registerButton(new ActionButton<>(BACK.getKey(), new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().setFilter(null);
            cache.getRecipeBookEditor().setCategory(null);
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new SaveCategoryButton(false, customCrafting));
        registerButton(new SaveCategoryButton(true, customCrafting));
        registerButton(new ItemInputButton<>(ICON.getKey(), Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getRecipeBookEditor().getCategorySetting().setIcon(inventory.getItem(slot).getType());
                } else {
                    cache.getRecipeBookEditor().getCategorySetting().setIcon(Material.AIR);
                }
            });
            return false;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
            if (recipeBookEditor.getCategorySetting() != null && recipeBookEditor.getCategorySetting().getIcon() != null) {
                return new ItemStack(guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getIcon());
            }
            return new ItemStack(Material.AIR);
        }));
        registerButton(new ChatInputButton<>(NAME.getKey(), Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%name%", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getName());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().setName(s);
            return false;
        }));
        registerButton(new ChatInputButton<>(DESCRIPTION_ADD.getKey(), Material.WRITABLE_BOOK, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%description%", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getDescription());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getDescription().add(s.equals("&empty") ? "" : ChatColor.convert(s));
            return false;
        }));
        registerButton(new ActionButton<>(DESCRIPTION_REMOVE.getKey(), Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            ChatUtils.sendCategoryDescription(player);
            guiHandler.close();
            return true;
        }));
        registerButton(new ActionButton<>(RECIPES.getKey(), Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            if (event instanceof InventoryClickEvent) {
                boolean remove = ((InventoryClickEvent) event).isRightClick();
                List<String> recipeKeys = Registry.RECIPES.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toList());
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    StringUtil.copyPartialMatches(args[0], recipeKeys, results);
                    return results;
                });
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        var namespacedKey = new NamespacedKey(args[0], args[1]);
                        ICustomRecipe<?, ?> recipe = Registry.RECIPES.get(namespacedKey);
                        if (recipe == null) {
                            wolfyUtilities.getChat().sendKey(player, new NamespacedKey("none", "recipe_editor"), "not_existing", new Pair<>("%recipe%", args[0] + ":" + args[1]));
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
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%recipes%", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getRecipes().stream().map(recipe -> "&7 - " + recipe.toString()).collect(Collectors.toList()));
            return itemStack;
        }));

        registerButton(new ActionButton<>(NAMESPACES.getKey(), Material.ENDER_CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            if (event instanceof InventoryClickEvent) {
                boolean remove = ((InventoryClickEvent) event).isRightClick();
                List<String> namespaces = Registry.RECIPES.namespaces();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    StringUtil.copyPartialMatches(args[0], namespaces, results);
                    return results;
                });
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 0) {
                        String namespace = args[0];
                        if (namespace == null || namespace.isEmpty()) {
                            return true;
                        }
                        if (remove) {
                            cache.getRecipeBookEditor().getCategorySetting().getNamespaces().remove(namespace);
                        } else {
                            cache.getRecipeBookEditor().getCategorySetting().getNamespaces().add(namespace);
                        }
                    }
                    return false;
                });
                Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%namespaces%", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getNamespaces().stream().map(namespacedKey -> "&7 - " + namespacedKey).collect(Collectors.toList()));
            return itemStack;
        }));

        registerButton(new ActionButton<>(GROUPS.getKey(), Material.BOOKSHELF, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            if (event instanceof InventoryClickEvent) {
                boolean remove = ((InventoryClickEvent) event).isRightClick();
                List<String> groups = Registry.RECIPES.groups();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    StringUtil.copyPartialMatches(args[0], groups, results);
                    return results;
                });
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
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%groups%", guiHandler.getCustomCache().getRecipeBookEditor().getCategorySetting().getGroups().stream().map(group -> "&7 - " + group).collect(Collectors.toList()));
            return itemStack;
        }));

    }
}
