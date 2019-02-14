package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.*;

public class RecipeHandler {

    private List<CustomRecipe> customRecipes = new ArrayList<>();
    private List<CustomItem> customItems = new ArrayList<>();

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
        File[] files = workbench.listFiles((dir, name) -> name.split("\\.").length > 1 && name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml") && !name.split("\\.")[0].equals("example"));
        if (files != null) {
            api.sendConsoleMessage("    "+type+":");
            for (File file : files) {
                String key = file.getParentFile().getParentFile().getName().toLowerCase();
                String name = file.getName().split("\\.")[0].toLowerCase();
                try {
                    switch (type) {
                        case "workbench":
                            CraftConfig config = new CraftConfig(configAPI, key, name);
                            if (config.isShapeless()) {
                                ShapelessCraftRecipe recipe = new ShapelessCraftRecipe(config);
                                recipe.load();
                                registerRecipe(recipe);
                            } else {
                                ShapedCraftRecipe recipe = new ShapedCraftRecipe(config);
                                recipe.load();
                                registerRecipe(recipe);
                            }
                            break;
                        case "furnace":
                            FurnaceCRecipe furnaceCRecipe = new FurnaceCRecipe(new FurnaceConfig(configAPI, key, name));
                            registerRecipe(furnaceCRecipe);
                            break;
                        case "items":
                            customItems.add(new CustomItem(new ItemConfig(configAPI, key, name)));
                            break;
                        case "fuel":

                            break;
                    }
                } catch (Exception ex) {
                    api.sendConsoleMessage("-------------------------------------------------");
                    api.sendConsoleMessage("Error loading Contents for: " + key+":"+name);
                    api.sendConsoleMessage("    Type: " + type);
                    api.sendConsoleMessage("    Message: " + ex.getMessage());
                    api.sendConsoleMessage("    Cause: " + ex.getCause());
                    api.sendConsoleMessage("You should check the config for empty settings " +
                            "\ne.g. No set Result or Source Item!");
                    api.sendConsoleMessage("-------------------------------------------------");
                }
                api.sendConsoleMessage("      - "+name);
            }
        }
    }

    public void loadConfigs() {

        if(!CustomCrafting.getConfigHandler().getConfig().getVanillaRecipes().isEmpty()){
            api.sendConsoleMessage("      - [Remove Vanilla Recipes] -");
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while(recipeIterator.hasNext()){
                Recipe recipe = recipeIterator.next();
                if(CustomCrafting.getConfigHandler().getConfig().getVanillaRecipes().contains(recipe.getResult().getType())){
                    recipeIterator.remove();
                }
            }
        }
        api.sendConsoleMessage("________[Loading Recipes/Items]________");
        File recipesFolder = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes");
        List<File> subFolders = null;
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            subFolders = new ArrayList<>(Arrays.asList(dirs));
        }
        if (subFolders != null) {
            for (File folder : subFolders) {
                api.sendConsoleMessage("loading - " + folder.getName()+" -");
                loadConfig(folder.getName(), "items");
                loadConfig(folder.getName(), "workbench");
                loadConfig(folder.getName(), "furnace");
            }
        }
    }

    private void registerRecipe(CustomRecipe recipe) {
        Bukkit.addRecipe(recipe);
        customRecipes.add(recipe);
    }

    /*
        Get all the ShapedRecipes from this group
     */
    public List<CustomRecipe> getRecipeGroup(String group) {
        List<CustomRecipe> groupRecipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes) {
            if (recipe.getGroup().equals(group))
                groupRecipes.add(recipe);
        }
        return groupRecipes;
    }

    public List<CustomRecipe> getRecipeGroup(CraftingRecipe recipe) {
        List<CustomRecipe> groupRecipes = new ArrayList<>(getRecipeGroup(recipe.getID()));
        groupRecipes.remove(recipe);
        return groupRecipes;
    }

    public CraftingRecipe getRecipe(Recipe recipe) {
        for (CustomRecipe craftingRecipe : customRecipes) {
            if (craftingRecipe.getID().equals(recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getKey().toString() : recipe instanceof ShapelessRecipe ? ((ShapelessRecipe) recipe).getKey().toString() : "")) {
                return (CraftingRecipe) craftingRecipe;
            }
        }
        return null;
    }

    public List<FurnaceCRecipe> getFurnaceRecipes(ItemStack source) {
        List<FurnaceCRecipe> recipes = new ArrayList<>();
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getSource().getType() == source.getType()) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public List<FurnaceCRecipe> getFurnaceRecipes() {
        List<FurnaceCRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes) {
            if (recipe instanceof FurnaceCRecipe) {
                recipes.add((FurnaceCRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomRecipe> getRecipes() {
        return customRecipes;
    }

    public FurnaceCRecipe getFurnaceRecipe(ItemStack source) {
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getSource().getType() == source.getType()) {
                return recipe;
            }
        }
        return null;
    }

    public List<CustomItem> getCustomItems() {
        return customItems;
    }

    public CustomItem getCustomItem(String key){
        for(CustomItem customItem : customItems){
            if(customItem.getId().equals(key)){
                return customItem;
            }
        }
        return null;
    }

    public void addCustomItem(CustomItem item){
        customItems.add(item);
    }

    public void removeCustomItem(String id){
        customItems.remove(getCustomItem(id));
    }

    public void removeCustomItem(CustomItem item){
        customItems.remove(item);
    }

    public CustomItem getCustomItem(String key, String name){
        return getCustomItem(key+":"+name);
    }
}
