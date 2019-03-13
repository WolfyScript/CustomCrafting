package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Furnace;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeEditor extends ExtendedGuiWindow {

    public RecipeEditor(InventoryAPI inventoryAPI) {
        super("recipe_editor", inventoryAPI, 54);
    }

    @Override
    public void onInit() {

    }

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
        String action = guiAction.getAction();
        if(action.equals("back")){
            guiAction.getGuiHandler().openLastInv();
        }else{
            //TODO: Functions for different recipe types
            Player player = guiAction.getPlayer();
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            switch (action){
                case "recipe_list":
                    guiAction.getGuiHandler().changeToInv("recipe_list");
                    break;
                case "create_recipe":
                    guiAction.getGuiHandler().changeToInv("recipe_creator");
                    break;
                case "edit_recipe":
                    runChat(0, "&3Type in the name of the folder and item! &6e.g. example your_recipe", guiAction.getGuiHandler());
                    break;
                case "delete_recipe":
                    runChat(1, "&3Type in the name of the folder and item! &6e.g. example your_recipe", guiAction.getGuiHandler());
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
        Workbench workbench = cache.getWorkbench();
        Furnace furnace = cache.getFurnace();
        if(args.length > 1){
            CustomRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(args[0]+":"+args[1]);
            if(recipe != null){
                if(id == 0){
                    switch (cache.getSetting()){
                        case CRAFT_RECIPE:
                            if (recipe instanceof CraftingRecipe){
                                workbench.setResult(recipe.getCustomResult());
                                HashMap<Character, List<CustomItem>> ingredients = ((CraftingRecipe) recipe).getIngredients();
                                workbench.setIngredients(Arrays.asList(new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR)), new CustomItem(new ItemStack(Material.AIR))));

                                System.out.println(ingredients);
                                for(String row : ((CraftingRecipe) recipe).getConfig().getShape()){
                                    for(char key : row.toCharArray()){
                                        if(key != ' '){
                                            workbench.setIngredients(key, ingredients.get((char)key));
                                        }
                                    }
                                }
                                System.out.println("Ingrd: "+workbench.getIngredients());
                                workbench.setResult(recipe.getCustomResult());
                                workbench.setExtend(recipe.getExtends());
                                workbench.setShapeless(((CraftingRecipe) recipe).isShapeless());
                                workbench.setAdvWorkbench(((CraftingRecipe) recipe).needsAdvancedWorkbench());
                                workbench.setPermissions(((CraftingRecipe) recipe).needsPermission());
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("recipe_creator"), 1);
                                return false;
                            }
                            api.sendPlayerMessage(player, "This recipe is not a Craft Recipe!");
                            return true;
                        case FURNACE_RECIPE:
                            if(recipe instanceof FurnaceCRecipe){
                                furnace.setAdvFurnace(((FurnaceCRecipe) recipe).needsAdvancedFurnace());
                                furnace.setSource(((FurnaceCRecipe) recipe).getSource());
                                furnace.setResult(recipe.getCustomResult());
                                furnace.setExperience(((FurnaceCRecipe) recipe).getExperience());
                                furnace.setCookingTime(((FurnaceCRecipe) recipe).getCookingTime());
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("recipe_creator"), 1);
                                return false;
                            }
                            api.sendPlayerMessage(player, "This recipe is not a Furnace Recipe!");
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
