package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import me.wolfyscript.customcrafting.recipes.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.*;

public class RecipeHandler {

    private List<CraftConfig> cachedConfigs = new ArrayList<>();
    private List<FurnaceConfig> cachedFurnaceConfigs = new ArrayList<>();

    private List<CraftingRecipe> recipes = new ArrayList<>();
    private List<FurnaceCRecipe> furnaceRecipes = new ArrayList<>();

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
    }


    public void loadConfigs() {
        api.sendConsoleMessage("loading Recipe configs...");
        File recipesFolder = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes");
        List<File> subFolders = null;
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            subFolders = new ArrayList<>(Arrays.asList(dirs));
        }

        if (subFolders != null) {
            for (File folder : subFolders) {
                api.sendConsoleMessage("    loading Workbench configs...");
                File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + folder.getName() + File.separator+ "workbench");
                File[] files = workbench.listFiles((dir, name) -> name.split("\\.").length > 1 && name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml") && !name.split("\\.")[0].equals("example"));
                if (files != null) {
                    for (File file : files) {
                        api.sendConsoleMessage("        -> " + file.getParentFile().getParentFile().getName() + ":" + file.getName().split("\\.")[0]);
                        cachedConfigs.add(new CraftConfig(configAPI, file.getParentFile().getParentFile().getName().toLowerCase(), file.getName().split("\\.")[0].toLowerCase()));
                    }
                }
                api.sendConsoleMessage("    loading Furnace configs...");
                File furnace = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + folder.getName() + File.separator+ "furnace");
                files = furnace.listFiles((dir, name) -> name.split("\\.").length > 1 && name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml") && !name.split("\\.")[0].equals("example"));
                if (files != null) {
                    for (File file : files) {
                        api.sendConsoleMessage("        -> " + file.getParentFile().getParentFile().getName() + ":" + file.getName().split("\\.")[0]);
                        cachedFurnaceConfigs.add(new FurnaceConfig(configAPI, file.getParentFile().getParentFile().getName().toLowerCase(), file.getName().split("\\.")[0].toLowerCase()));
                    }
                }

            }
        }
    }

    public void loadRecipes() {
        api.sendConsoleMessage("loading Recipes...");
        api.sendConsoleMessage("    loading Workbench recipes...");
        for (CraftConfig craftConfig : cachedConfigs) {
            api.sendConsoleMessage("        -> " + craftConfig.getId());
            if(craftConfig.isShapeless()){
                ShapelessCraftRecipe recipe = new ShapelessCraftRecipe(craftConfig);
                recipe.load();
                registerCraftRecipe(recipe);
            }else{
                ShapedCraftRecipe recipe = new ShapedCraftRecipe(craftConfig);
                recipe.load();
                registerCraftRecipe(recipe);
            }
        }
        api.sendConsoleMessage("    loading Furnace recipes...");
        for(FurnaceConfig furnaceConfig : cachedFurnaceConfigs){
            api.sendConsoleMessage("        -> " + furnaceConfig.getId());
            FurnaceCRecipe furnaceCRecipe = new FurnaceCRecipe(furnaceConfig);
            furnaceRecipes.add(furnaceCRecipe);
            Bukkit.addRecipe(furnaceCRecipe);
        }

    }

    private void registerCraftRecipe(CraftingRecipe recipe) {
        Bukkit.addRecipe(recipe);
        recipes.add(recipe);
    }

    /*
        Get all the ShapedRecipes from this group
     */
    public List<CraftingRecipe> getRecipeGroup(String group){
        List<CraftingRecipe> groupRecipes = new ArrayList<>();
        for (CraftingRecipe recipe : recipes) {
            if (recipe.getGroup().equals(group))
                groupRecipes.add(recipe);
        }
        return groupRecipes;
    }

    public List<CraftingRecipe> getRecipeGroup(CraftingRecipe recipe){
        List<CraftingRecipe> groupRecipes = new ArrayList<>(getRecipeGroup(recipe.getID()));
        groupRecipes.remove(recipe);
        return groupRecipes;
    }

    public CraftingRecipe getRecipe(Recipe recipe){
        for(CraftingRecipe craftingRecipe : recipes){
            if(craftingRecipe.getID().equals(recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getKey().toString() : recipe instanceof ShapelessRecipe ? ((ShapelessRecipe) recipe).getKey().toString() : "")){
                return craftingRecipe;
            }
        }
        return null;
    }

    public List<FurnaceCRecipe> getFurnaceRecipes(ItemStack source){
        List<FurnaceCRecipe> recipes = new ArrayList<>();
        for(FurnaceCRecipe recipe : furnaceRecipes){
            if(recipe.getSource().getType() == source.getType()){
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public List<FurnaceCRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public List<CraftingRecipe> getRecipes() {
        return recipes;
    }

    public FurnaceCRecipe getFurnaceRecipe(ItemStack source){
        List<FurnaceCRecipe> recipes = new ArrayList<>();
        for(FurnaceCRecipe recipe : furnaceRecipes){
            if(recipe.getSource().getType() == source.getType()){
                return recipe;
            }
        }
        return null;
    }

    public HashMap<String, List<String>> getCraftRecipes(){
        HashMap<String, List<String>> recipes = new HashMap<>();
        for(CraftingRecipe recipe : getRecipes()){
            String folder = recipe.getID().split(":")[0];
            String recipeNmn = recipe.getID().split(":")[1];

            if(!recipes.containsKey(folder)){
                recipes.put(folder, new ArrayList<>(Collections.singleton(recipeNmn)));
            }else{
                List<String> list = recipes.get(folder);
                list.add(recipeNmn);
                recipes.put(folder, list);
            }
        }
        return recipes;
    }




}
