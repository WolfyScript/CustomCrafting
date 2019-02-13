package me.wolfyscript.customcrafting.gui.list;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RecipeList extends GuiWindow {

    private HashMap<String, List<CustomRecipe>> cachedRecipes = new HashMap<>();

    public RecipeList(InventoryAPI inventoryAPI) {
        super("recipe_list", inventoryAPI, 54);
    }

    @Override
    public void onInit() {


    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            RecipeHandler recHand = CustomCrafting.getRecipeHandler();
            List<ItemStack> listItems = new ArrayList<>();
            if (cachedRecipes.isEmpty()) {
                for (CustomRecipe recipe : recHand.getRecipes()) {
                    String key = recipe.getID().split(":")[0];
                    if (cachedRecipes.get(key) != null && cachedRecipes.get(key).isEmpty()) {
                        cachedRecipes.put(key, new ArrayList<>(Collections.singleton(recipe)));
                    } else {
                        List<CustomRecipe> update = cachedRecipes.get(key) == null ? new ArrayList<>() : cachedRecipes.get(key);
                        update.add(recipe);
                        cachedRecipes.put(key, update);
                    }
                }
            }
            String listSetting = CustomCrafting.getPlayerCache(event.getPlayer()).getRecipeListSetting();
            if (listSetting.isEmpty()) {
                for (String key : cachedRecipes.keySet()) {
                    ItemStack itemStack = new ItemStack(Material.CHEST);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§7" + key);
                    itemStack.setItemMeta(itemMeta);
                    listItems.add(itemStack);
                }
            } else if (!listSetting.contains(":")) {
                if (cachedRecipes.get(listSetting) != null) {
                    for (CustomRecipe recipe : cachedRecipes.get(listSetting)) {
                        ItemStack itemStack = new ItemStack(Material.STONE);
                        if(recipe instanceof FurnaceCRecipe){
                            itemStack = new ItemStack(Material.FURNACE);
                        }else if(recipe instanceof CraftingRecipe){
                            itemStack = new ItemStack(Material.CRAFTING_TABLE);
                        }
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName("§6" + recipe.getID().split(":")[1]);
                        itemStack.setItemMeta(itemMeta);
                        listItems.add(itemStack);
                    }
                }
            }
            listItems.sort(Comparator.comparing(o -> o.getItemMeta().getDisplayName()));
            int slot = 10;
            for (ItemStack itemStack : listItems) {
                event.setItem(slot, itemStack);
                slot += 2;
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        if(action.equals("back")){
            guiAction.getGuiHandler().openLastInv();
        }else{

        }
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        if (guiClick.getClickedInventory().equals(getInventory(guiClick.getGuiHandler()))) {
            ItemStack itemStack = guiClick.getCurrentItem();
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                String name = itemStack.getItemMeta().getDisplayName().substring(2);
                String setting = CustomCrafting.getPlayerCache(guiClick.getPlayer()).getRecipeListSetting();
                if (setting.isEmpty()) {
                    CustomCrafting.getPlayerCache(guiClick.getPlayer()).setRecipeListSetting(name);
                    guiClick.getGuiHandler().reloadInv("recipe_list");
                } else if(!setting.contains(":")){
                    CustomCrafting.getPlayerCache(guiClick.getPlayer()).setRecipeListSetting(setting + ":" + name);
                    guiClick.getGuiHandler().reloadInv("recipe_list");
                }else{
                    guiClick.getGuiHandler().changeToInv("");
                }
            }
        }
        return true;
    }


}
