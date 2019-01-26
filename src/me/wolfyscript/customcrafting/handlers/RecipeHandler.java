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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeHandler {

    private List<CraftConfig> cachedConfigs = new ArrayList<>();
    private List<FurnaceConfig> cachedFurnaceConfigs = new ArrayList<>();

    private List<ShapedCraftRecipe> shapedRecipes = new ArrayList<>();
    private List<ShapelessCraftRecipe> shapelessRecipes = new ArrayList<>();
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
                        api.sendConsoleMessage("        -> " + file.getParentFile().getName() + ":" + file.getName());
                        cachedConfigs.add(new CraftConfig(configAPI, file.getParentFile().getParentFile().getName().toLowerCase(), file.getName().split("\\.")[0].toLowerCase()));
                    }
                }
                api.sendConsoleMessage("    loading Furnace configs...");
                File furnace = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + folder.getName() + File.separator+ "furnace");
                files = furnace.listFiles((dir, name) -> name.split("\\.").length > 1 && name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml") && !name.split("\\.")[0].equals("example"));
                if (files != null) {
                    for (File file : files) {
                        api.sendConsoleMessage("        -> " + file.getParentFile().getName() + ":" + file.getName());
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

    public List<ShapedCraftRecipe> getShapedRecipeGroup(String group){
        List<ShapedCraftRecipe> recipes = new ArrayList<>();
        for (ShapedCraftRecipe recipe : shapedRecipes) {
            if (recipe.getGroup().equals(group))
                recipes.add(recipe);
        }
        return recipes;
    }

    public List<ShapedCraftRecipe> getShapedRecipeGroup(ShapedCraftRecipe recipe){
        List<ShapedCraftRecipe> group = getShapedRecipeGroup(recipe.getGroup());
        group.remove(recipe);
        return group;

    }

    public List<ShapelessCraftRecipe> getShapelessRecipeGroup(String group){
        List<ShapelessCraftRecipe> recipes = new ArrayList<>();
        for (ShapelessCraftRecipe recipe : shapelessRecipes) {
            if (recipe.getGroup().equals(group))
                recipes.add(recipe);
        }
        return recipes;
    }

    public List<CraftingRecipe> getRecipeGroup(String group){
        List<CraftingRecipe> recipes = new ArrayList<>();
        recipes.addAll(getShapedRecipeGroup(group));
        recipes.addAll(getShapelessRecipeGroup(group));
        return recipes;
    }

    public void registerCraftRecipe(CraftingRecipe recipe) {
        Bukkit.addRecipe(recipe);
        if (recipe instanceof ShapelessCraftRecipe) {
            if (!shapelessRecipes.contains(recipe)) {
                shapelessRecipes.add((ShapelessCraftRecipe) recipe);
            }
        } else if (recipe instanceof ShapedCraftRecipe) {
            if (!shapedRecipes.contains(recipe)) {
                shapedRecipes.add((ShapedCraftRecipe) recipe);
            }
        }
    }

    public ShapedCraftRecipe getShapedRecipe(String id) {
        for (ShapedCraftRecipe recipe : shapedRecipes) {
            if (recipe.getId().equals(id))
                return recipe;
        }
        return null;
    }

    public ShapelessCraftRecipe getShapelessRecipe(String id) {
        for (ShapelessCraftRecipe recipe : shapelessRecipes) {
            if (recipe.getId().equals(id))
                return recipe;
        }
        return null;
    }


}
