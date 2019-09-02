package me.wolfyscript.customcrafting.gui.main_gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class RecipeEditor extends ExtendedGuiWindow {

    public RecipeEditor(InventoryAPI inventoryAPI) {
        super("recipe_editor", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("create_recipe", new ButtonState("create_recipe", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("recipe_creator");
            return true;
        })));
        registerButton(new ActionButton("edit_recipe", new ButtonState("edit_recipe", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getChatLists().setCurrentPageRecipes(1);
            api.sendActionMessage(player, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> ChatUtils.sendRecipeListExpanded(player1), true), new ClickData(" Recipe List", null));

            openChat(guiHandler, "$msg.gui.recipe_editor.input$", (guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    CustomRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(args[0] + ":" + args[1]);
                    if (CustomCrafting.getRecipeHandler().loadRecipeIntoCache(recipe, player1)) {
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler1.changeToInv("recipe_creator"), 1);
                        return false;
                    } else {
                        api.sendPlayerMessage(player1, "$msg.gui.recipe_editor.invalid_recipe$", new String[]{"%RECIPE_TYPE%", CustomCrafting.getPlayerCache(player1).getSetting().name()});
                        return true;
                    }
                }
                return false;
            });
            return true;
        })));
        registerButton(new ActionButton("delete_recipe", new ButtonState("delete_recipe", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getChatLists().setCurrentPageRecipes(1);
            api.sendActionMessage(player, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> ChatUtils.sendRecipeListExpanded(player1), true), new ClickData(" Recipe List", null));
            openChat(guiHandler, "$msg.gui.recipe_editor.input$", (guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    CustomRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(args[0] + ":" + args[1]);
                    api.sendPlayerMessage(player1, "$msg.gui.recipe_editor.delete.confirm$", new String[]{"%RECIPE%", recipe.getId()});
                    api.sendActionMessage(player1, new ClickData("$msg.gui.recipe_editor.delete.confirmed$", (wolfyUtilities, player2) -> Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        CustomCrafting.getRecipeHandler().unregisterRecipe(recipe);
                        if (CustomCrafting.hasDataBaseHandler()) {
                            CustomCrafting.getDataBaseHandler().removeRecipe(recipe.getConfig().getFolder(), recipe.getConfig().getName());
                            player1.sendMessage("§aRecipe deleted!");
                        } else {
                            if (recipe.getConfig().getConfigFile().delete()) {
                                player1.sendMessage("§aRecipe deleted!");
                            } else {
                                recipe.getConfig().getConfigFile().deleteOnExit();
                                player1.sendMessage("§cCould not delete recipe!");
                            }
                        }
                        guiHandler1.openCluster();
                    })), new ClickData("$msg.gui.recipe_editor.delete.declined$", (wolfyUtilities, player2) -> {
                        guiHandler1.openCluster();
                    }));
                    guiHandler1.cancelChatEvent();
                    return true;
                }
                return false;
            });
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            event.setButton(20, "create_recipe");
            event.setButton(22, "edit_recipe");
            event.setButton(24, "delete_recipe");
        }
    }
}
