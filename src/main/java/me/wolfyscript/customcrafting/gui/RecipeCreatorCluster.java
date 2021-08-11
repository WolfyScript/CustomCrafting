package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.*;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.HiddenButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.*;
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

public class RecipeCreatorCluster extends CCCluster {

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
    public static final String SHAPELESS = "workbench.shapeless";
    public static final String MIRROR_VERTICAL = "workbench.mirror_vertical";
    public static final String MIRROR_HORIZONTAL = "workbench.mirror_horizontal";
    public static final String MIRROR_ROTATION = "workbench.mirror_rotation";
    //Language Keys
    private static final String ENABLED = ".enabled";
    public static final NamespacedKey EXACT_META_ENABLED = enabledKey(EXACT_META.getKey());
    public static final NamespacedKey HIDDEN_ENABLED = enabledKey(HIDDEN.getKey());
    public static final NamespacedKey SHAPELESS_ENABLED = enabledKey(SHAPELESS);
    public static final NamespacedKey MIRROR_VERTICAL_ENABLED = enabledKey(MIRROR_VERTICAL);
    public static final NamespacedKey MIRROR_HORIZONTAL_ENABLED = enabledKey(MIRROR_HORIZONTAL);
    public static final NamespacedKey MIRROR_ROTATION_ENABLED = enabledKey(MIRROR_ROTATION);
    private static final String DISABLED = ".disabled";
    public static final NamespacedKey EXACT_META_DISABLED = disabledKey(EXACT_META.getKey());
    public static final NamespacedKey HIDDEN_DISABLED = disabledKey(HIDDEN.getKey());
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

    public RecipeCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new WorkbenchCreator(this, customCrafting));
        registerGuiWindow(new CookingCreator(this, customCrafting));
        registerGuiWindow(new AnvilCreator(this, customCrafting));
        registerGuiWindow(new CauldronCreator(this, customCrafting));
        registerGuiWindow(new StonecutterCreator(this, customCrafting));
        registerGuiWindow(new GrindstoneCreator(this, customCrafting));
        registerGuiWindow(new EliteWorkbenchCreator(this, customCrafting));
        registerGuiWindow(new EliteWorkbenchCreatorSettings(this, customCrafting));
        registerGuiWindow(new BrewingCreator(this, customCrafting));
        registerGuiWindow(new SmithingCreator(this, customCrafting));
        //Other Menus
        registerGuiWindow(new ConditionsMenu(this, customCrafting));
        registerGuiWindow(new ConditionsAddMenu(this, customCrafting));
        registerGuiWindow(new ResultMenu(this, customCrafting));
        registerGuiWindow(new IngredientMenu(this, customCrafting));
        //Tags
        registerGuiWindow(new TagSettings(this, customCrafting));
        registerGuiWindow(new TagChooseList(this, customCrafting));
        registerGuiWindow(new ItemEditor(this, customCrafting));

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
            StringUtil.copyPartialMatches(args[0], CCRegistry.RECIPES.groups(), results);
            return results;
        }));
        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());
        registerButton(new HiddenButton());
        registerSaveButtons();
    }

    private void registerSaveButtons() {
        registerButton(new ActionButton<>(SAVE.getKey(), Material.WRITABLE_BOOK, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            if (guiHandler.getWindow() instanceof RecipeCreator) {
                if (cache.getRecipeCreatorCache().getRecipeCache().save(customCrafting, player, guiHandler)) {
                    guiHandler.getApi().getChat().sendKey(player, KEY, "save.empty");
                    return false;
                }
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
                            StringUtil.copyPartialMatches(args[0], CCRegistry.RECIPES.namespaces(), results);
                        } else if (args.length == 2) {
                            results.add("<key>");
                            StringUtil.copyPartialMatches(args[1], CCRegistry.RECIPES.get(args[0]).stream().filter(recipe -> cache.getRecipeCreatorCache().getRecipeType().isInstance(recipe)).map(recipe -> recipe.getNamespacedKey().getKey()).toList(), results);
                        }
                    }
                    Collections.sort(results);
                    return results;
                });
                recipeCreator.openChat(guiHandler.getInvAPI().getGuiCluster(KEY), "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    var namespacedKey = ChatUtils.getInternalNamespacedKey(player1, s, args);
                    if (namespacedKey != null) {
                        cache.getRecipeCreatorCache().getRecipeCache().setKey(namespacedKey);
                        if (cache.getRecipeCreatorCache().getRecipeCache().save(customCrafting, player, guiHandler)) {
                            guiHandler.getApi().getChat().sendKey(player, KEY, "save.empty");
                            return false;
                        }
                    }
                    return true;
                });
            }
            return true;
        }));
    }
}
