package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.CookingData;
import me.wolfyscript.customcrafting.data.cache.Stonecutter;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.inventory.*;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeEditor extends ExtendedGuiWindow {

    public RecipeEditor(InventoryAPI inventoryAPI) {
        super("recipe_editor", inventoryAPI, 45);
    }

    @Override
    public void onInit() { }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setItem(20, "main_menu", "create_recipe");
            event.setItem(22, "main_menu", "edit_recipe");
            event.setItem(24, "main_menu", "delete_recipe");
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if (!super.onAction(guiAction)) {
            String action = guiAction.getAction();
            if (action.equals("back")) {
                guiAction.getGuiHandler().openLastInv();
            } else {
                PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
                switch (action) {
                    case "recipe_list":
                        guiAction.getGuiHandler().changeToInv("recipe_list");
                        break;
                    case "create_recipe":
                        guiAction.getGuiHandler().changeToInv("recipe_creator");
                        break;
                    case "edit_recipe":
                        cache.getChatLists().setCurrentPageRecipes(1);
                        api.sendActionMessage(guiAction.getPlayer(), new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> ChatUtils.sendRecipeListExpanded(player1), true), new ClickData(" Recipe List", null));
                        runChat(0, "$msg.gui.recipe_editor.input$", guiAction.getGuiHandler());
                        break;
                    case "delete_recipe":
                        cache.getChatLists().setCurrentPageRecipes(1);
                        api.sendActionMessage(guiAction.getPlayer(), new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> ChatUtils.sendRecipeListExpanded(player1), true), new ClickData(" Recipe List", null));
                        runChat(1, "$msg.gui.recipe_editor.input$", guiAction.getGuiHandler());
                }
            }
        }
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        return true;
    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        String[] args = message.split(" ");
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(guiHandler.getPlayer());

        if (args.length > 1) {
            CustomRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(args[0] + ":" + args[1]);
            if (recipe != null) {
                if (id == 0) {
                    switch (cache.getSetting()) {
                        case CRAFT_RECIPE:
                            if (recipe instanceof CraftingRecipe) {
                                cache.resetWorkbench();
                                Workbench workbench = cache.getWorkbench();
                                workbench.setResult(recipe.getCustomResult());
                                HashMap<Character, ArrayList<CustomItem>> ingredients = ((CraftingRecipe) recipe).getIngredients();
                                workbench.setIngredients(Arrays.asList(new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR))));
                                for (String row : ((CraftingRecipe) recipe).getConfig().getShape()) {
                                    for (char key : row.toCharArray()) {
                                        if (key != ' ') {
                                            workbench.setIngredients(key, ingredients.get((char) key));
                                        }
                                    }
                                }
                                workbench.setResult(recipe.getCustomResult());
                                workbench.setShapeless(((CraftingRecipe) recipe).isShapeless());
                                workbench.setAdvWorkbench(((CraftingRecipe) recipe).needsAdvancedWorkbench());
                                workbench.setPermissions(((CraftingRecipe) recipe).needsPermission());
                                workbench.setPriority(recipe.getPriority());
                                workbench.setExactMeta(recipe.isExactMeta());
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("recipe_creator"), 1);
                                return false;
                            }
                            api.sendPlayerMessage(player, "$msg.gui.recipe_editor.invalid_recipe$", new String[]{"%RECIPE_TYPE%", cache.getSetting().name()});
                            return true;
                        case ANVIL:

                            return true;
                        case STONECUTTER:
                            if (recipe instanceof CustomStonecutterRecipe) {
                                cache.resetStonecutter();
                                Stonecutter stonecutter = cache.getStonecutter();
                                stonecutter.setResult(recipe.getCustomResult());
                                stonecutter.setSource(((CustomStonecutterRecipe) recipe).getSource());
                                stonecutter.setExactMeta(recipe.isExactMeta());
                                stonecutter.setPriority(recipe.getPriority());
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("recipe_creator"), 1);
                                return false;
                            }
                            api.sendPlayerMessage(player, "$msg.gui.recipe_editor.invalid_recipe$", new String[]{"%RECIPE_TYPE%", cache.getSetting().name()});
                            return false;
                        case CAMPFIRE:
                        case BLAST_FURNACE:
                        case SMOKER:
                        case FURNACE_RECIPE:
                            if (recipe instanceof CustomCookingRecipe) {
                                cache.resetCookingData();
                                CookingData furnace = cache.getFurnace();
                                //furnace.setAdvFurnace(((CustomFurnaceRecipe) recipe).needsAdvancedFurnace());
                                furnace.setSource(((CustomCookingRecipe) recipe).getSource());
                                furnace.setResult(recipe.getCustomResult());
                                furnace.setExperience(((CustomCookingRecipe) recipe).getConfig().getXP());
                                furnace.setCookingTime(((CustomCookingRecipe) recipe).getConfig().getCookingTime());
                                furnace.setExactMeta(recipe.isExactMeta());
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("recipe_creator"), 1);
                                return false;
                            }
                            api.sendPlayerMessage(player, "$msg.gui.recipe_editor.invalid_recipe$", new String[]{"%RECIPE_TYPE%", cache.getSetting().name()});
                            return false;
                    }
                } else if (id == 1) {
                    api.sendPlayerMessage(player, "$msg.gui.recipe_editor.delete.confirm$", new String[]{"%RECIPE%", recipe.getId()});
                    api.sendActionMessage(player, new ClickData("$msg.gui.recipe_editor.delete.confirmed$", (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        CustomCrafting.getRecipeHandler().unregisterRecipe(recipe);
                        if (recipe.getConfig().getConfigFile().delete()) {
                            player1.sendMessage("§aRecipe deleted!");
                        } else {
                            player1.sendMessage("§cCould not delete recipe!");
                        }
                        guiHandler.openLastInv();
                    })), new ClickData("$msg.gui.recipe_editor.delete.declined$", (wolfyUtilities, player1) -> {
                        guiHandler.openLastInv();
                    }));
                    guiHandler.cancelChatEvent();
                    return true;
                }
            } else {
                api.sendPlayerMessage(player, "$msg.gui.recipe_editor.not_existing$", new String[]{"%RECIPE%", args[0] + ":" + args[1]});
            }
        }
        return false;
    }
}
