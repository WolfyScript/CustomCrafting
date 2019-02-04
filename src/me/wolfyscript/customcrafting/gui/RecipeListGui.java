package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RecipeListGui extends GuiWindow {

    private HashMap<String, List<CraftingRecipe>> cachedRecipes = new HashMap<>();

    public RecipeListGui(InventoryAPI inventoryAPI) {
        super("recipe_list", inventoryAPI, 54);
    }

    @Override
    public void onInit() {



    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event){
        if(event.verify(this)){
            Setting setting = CustomCrafting.getPlayerSettings(event.getPlayer()).getSetting();
            RecipeHandler recHand = CustomCrafting.getRecipeHandler();
            List<ItemStack> listItems = new ArrayList<>();

            if(setting.equals(Setting.CRAFT_RECIPE)){
                if(cachedRecipes.isEmpty()){
                    for(CraftingRecipe recipe : recHand.getRecipes()){
                        String key = recipe.getID().split(":")[0];
                        if(cachedRecipes.get(key) != null && cachedRecipes.get(key).isEmpty()){
                            cachedRecipes.put(key, new ArrayList<>(Collections.singleton(recipe)));
                        }else{
                            List<CraftingRecipe> update = cachedRecipes.get(key) == null ? new ArrayList<>() : cachedRecipes.get(key);
                            update.add(recipe);
                            cachedRecipes.put(key, update);
                        }
                    }
                }
                String listSetting = CustomCrafting.getPlayerSettings(event.getPlayer()).getRecipeListSetting();
                if(listSetting.isEmpty()){
                    for(String key : cachedRecipes.keySet()){
                        ItemStack itemStack = new ItemStack(Material.CHEST);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName("§7"+key);
                        itemStack.setItemMeta(itemMeta);
                        listItems.add(itemStack);
                    }
                }else if(!listSetting.contains(":")){
                    if(cachedRecipes.get(listSetting) != null){
                        for(CraftingRecipe recipe : cachedRecipes.get(listSetting)){
                            ItemStack itemStack = new ItemStack(Material.BOOK);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§6"+recipe.getID().split(":")[1]);
                            itemStack.setItemMeta(itemMeta);
                            listItems.add(itemStack);
                        }
                    }
                }else{

                }

                listItems.sort(Comparator.comparing(o -> o.getItemMeta().getDisplayName()));
                int slot = 10;
                for(ItemStack itemStack : listItems){
                    event.setItem(slot, itemStack);
                    slot += 2 ;
                }
            }



        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        if(guiClick.getClickedInventory().equals(getInventory(guiClick.getGuiHandler()))){
            ItemStack itemStack = guiClick.getCurrentItem();
            if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()){
                String name = itemStack.getItemMeta().getDisplayName().substring(2);
                String setting = CustomCrafting.getPlayerSettings(guiClick.getPlayer()).getRecipeListSetting();
                if(setting.isEmpty()){
                    CustomCrafting.getPlayerSettings(guiClick.getPlayer()).setRecipeListSetting(name);
                }else{
                    CustomCrafting.getPlayerSettings(guiClick.getPlayer()).setRecipeListSetting(setting+":"+name);
                }
                guiClick.getGuiHandler().reloadInv("recipe_list");
            }
        }
        return true;
    }



}
