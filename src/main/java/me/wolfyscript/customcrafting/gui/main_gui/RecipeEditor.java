package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class RecipeEditor extends CCWindow {

    public RecipeEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "recipe_editor", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>("create_recipe", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            changeToCreator(guiHandler);
            return true;
        }));
        registerButton(new ActionButton<>("edit_recipe", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            customCrafting.getChatUtils().sendRecipeListExpanded(player);
            guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    ICustomRecipe<?> recipe = customCrafting.getRecipeHandler().getRecipe(new NamespacedKey(args[0], args[1]));
                    if (recipe == null) {
                        api.getChat().sendKey(player, getNamespacedKey(), "not_existing", new Pair<>("%recipe%", args[0] + ":" + args[1]));
                        return true;
                    }
                    if (customCrafting.getRecipeHandler().loadRecipeIntoCache(recipe, guiHandler1)) {
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> changeToCreator(guiHandler), 1);
                        return false;
                    } else {
                        api.getChat().sendKey(player1, getNamespacedKey(), "invalid_recipe", new Pair<>("%recipe_type%", guiHandler.getCustomCache().getRecipeType().name()));
                        return true;
                    }
                }
                return false;
            });
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }));
        registerButton(new ActionButton<>("delete_recipe", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            customCrafting.getChatUtils().sendRecipeListExpanded(player);
            guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    ICustomRecipe<?> recipe = customCrafting.getRecipeHandler().getRecipe(new NamespacedKey(args[0], args[1]));
                    if (recipe == null) {
                        api.getChat().sendKey(player, getNamespacedKey(), "not_existing", new Pair<>("%recipe%", args[0] + ":" + args[1]));
                        return true;
                    }
                    api.getChat().sendKey(player1, getNamespacedKey(), "delete.confirm", new Pair<>("%recipe%", recipe.getNamespacedKey().toString()));
                    api.getChat().sendActionMessage(player1, new ClickData("$inventories.none.recipe_editor.messages.delete.confirmed$", (wolfyUtilities, player2) -> {
                        guiHandler1.openCluster();
                        Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> recipe.delete(player2));
                    }), new ClickData("$inventories.none.recipe_editor.messages.delete.declined$", (wolfyUtilities, player2) -> guiHandler1.openCluster()));
                    guiHandler1.cancelChatInputAction();
                    return true;
                }
                return false;
            });
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        event.setButton(21, "create_recipe");
        event.setButton(23, "none", "recipe_list");
    }

    private void changeToCreator(GuiHandler<CCCache> guiHandler) {
        guiHandler.getCustomCache().setSetting(Setting.RECIPE_CREATOR);
        guiHandler.openWindow(new NamespacedKey("recipe_creator", guiHandler.getCustomCache().getRecipeType().getCreatorID()));
    }
}
