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
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.io.File;
import java.security.Key;
import java.util.*;

public class RecipeHandler {

    private List<Recipe> allRecipes = new ArrayList<>();

    private List<CustomRecipe> customRecipes = new ArrayList<>();
    private List<CustomItem> customItems = new ArrayList<>();

    private ArrayList<String> disabledRecipes = new ArrayList<>();
    private HashMap<String, List<String>> overrideRecipes = new HashMap<>();

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
            api.sendConsoleMessage("    " + type + ":");
            for (File file : files) {
                String key = file.getParentFile().getParentFile().getName().toLowerCase();
                String name = file.getName().split("\\.")[0].toLowerCase();
                try {
                    switch (type) {
                        case "workbench":
                            CraftConfig config = new CraftConfig(configAPI, key, name);
                            CraftingRecipe craftingRecipe;
                            if (config.isShapeless()) {
                                craftingRecipe = new ShapelessCraftRecipe(config);
                            } else {
                                craftingRecipe = new ShapedCraftRecipe(config);
                            }
                            craftingRecipe.load();
                            registerRecipe(craftingRecipe);
                            break;
                        case "furnace":
                            registerRecipe(new FurnaceCRecipe(new FurnaceConfig(configAPI, key, name)));
                            break;
                        case "items":
                            customItems.add(new CustomItem(new ItemConfig(configAPI, key, name)));
                            break;
                        case "fuel":

                            break;
                    }
                } catch (Exception ex) {
                    api.sendConsoleMessage("-------------------------------------------------");
                    api.sendConsoleMessage("Error loading Contents for: " + key + ":" + name);
                    api.sendConsoleMessage("    Type: " + type);
                    api.sendConsoleMessage("    Message: " + ex.getMessage());
                    api.sendConsoleMessage("    Cause: " + ex.getCause());
                    api.sendConsoleMessage("You should check the config for empty settings ");
                    api.sendConsoleMessage("e.g. No set Result or Source Item!");
                    api.sendConsoleMessage("-------------------------------------------------");
                    ex.printStackTrace();
                }
                api.sendConsoleMessage("      - " + name);
            }
        }
    }

    public void onSave(){
        CustomCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        CustomCrafting.getConfigHandler().getConfig().save();
    }

    public void loadConfigs() {
        if (!CustomCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            disabledRecipes.addAll(CustomCrafting.getConfigHandler().getConfig().getDisabledRecipes());
        }
        api.sendConsoleMessage("$msg.startup.recipes.title$");
        File recipesFolder = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes");
        List<File> subFolders = null;
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            subFolders = new ArrayList<>(Arrays.asList(dirs));
        }
        if (subFolders != null) {
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.items$");
            for (File folder : subFolders) {
                api.sendConsoleMessage("- " + folder.getName());
                loadConfig(folder.getName(), "items");
            }
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.recipes$");
            for (File folder : subFolders) {
                api.sendConsoleMessage("- " + folder.getName());
                loadConfig(folder.getName(), "workbench");
                loadConfig(folder.getName(), "furnace");
            }
            getRecipes().sort((o1, o2) -> Integer.compare(o2.getPriority().getOrder(), o1.getPriority().getOrder()));
        }
    }

    public void registerRecipe(CustomRecipe recipe) {
        Bukkit.addRecipe(recipe);
        customRecipes.add(recipe);
    }

    public void injectRecipe(CustomRecipe recipe){
        unregisterRecipe(recipe);
        Bukkit.addRecipe(recipe);
        customRecipes.add(recipe);
        getRecipes().sort((o1, o2) -> Integer.compare(o2.getPriority().getOrder(), o1.getPriority().getOrder()));
    }

    public void unregisterRecipe(String key){
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        List<Recipe> recipes = new ArrayList<>();
        while(recipeIterator.hasNext()){
            Recipe recipe = recipeIterator.next();
            if(!((Keyed) recipe).getKey().toString().equals(key)){
                recipes.add(recipe);
            }
        }
        Bukkit.clearRecipes();
        Collections.reverse(recipes);
        for(Recipe recipe : recipes){
            Bukkit.addRecipe(recipe);
        }
    }

    public void unregisterRecipe(CustomRecipe customRecipe) {
        customRecipes.remove(customRecipe);
        unregisterRecipe(customRecipe.getID());
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

    public CustomRecipe getRecipe(Recipe recipe) {
        for (CustomRecipe craftingRecipe : customRecipes) {
            if (recipe instanceof Keyed) {
                if (craftingRecipe.getID().equals(((Keyed) recipe).getKey().toString())) {
                    return craftingRecipe;
                }
            }
        }
        return null;
    }

    public List<CraftingRecipe> getSimilarRecipes(List<List<ItemStack>> items){
        List<CraftingRecipe> recipes = new ArrayList<>();
        for(CraftingRecipe customRecipe : getCraftingRecipes()){
            if(customRecipe instanceof ShapedCraftRecipe){
                if(items.size() == ((ShapedCraftRecipe) customRecipe).getShape().length && items.get(0).size() == ((ShapedCraftRecipe) customRecipe).getShape()[0].length()){
                    recipes.add(customRecipe);
                }
            }else{
                int i = 0;
                for(List<ItemStack> row : items){
                    for(ItemStack c : row){
                        if(c != null){
                            i++;
                        }
                    }
                }
                if(customRecipe.getIngredients().keySet().size() == i){
                    recipes.add(customRecipe);
                }
            }
        }
        return recipes;
    }

    public CustomRecipe getRecipe(String key) {
        for (CustomRecipe craftingRecipe : customRecipes) {
            if (craftingRecipe.getID().equals(key)) {
                return craftingRecipe;
            }
        }
        return null;
    }

    //CRAFTING RECIPES

    public CraftingRecipe getCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof CraftingRecipe ? (CraftingRecipe) customRecipe : null;
    }

    public List<CraftingRecipe> getCraftingRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        for(CustomRecipe customRecipe : getRecipes()){
            if(customRecipe instanceof CraftingRecipe){
                recipes.add((CraftingRecipe) customRecipe);
            }
        }
        return recipes;
    }

    public List<ShapedCraftRecipe> getShapedCraftRecipes() {
        List<ShapedCraftRecipe> recipes = new ArrayList<>();
        for(CustomRecipe customRecipe : getRecipes()){
            if(customRecipe instanceof ShapedCraftRecipe){
                recipes.add((ShapedCraftRecipe) customRecipe);
            }
        }
        return recipes;
    }

    public List<ShapelessCraftRecipe> getShapelessCraftRecipes() {
        List<ShapelessCraftRecipe> recipes = new ArrayList<>();
        for(CustomRecipe customRecipe : getRecipes()){
            if(customRecipe instanceof ShapelessCraftRecipe){
                recipes.add((ShapelessCraftRecipe) customRecipe);
            }
        }
        return recipes;
    }

    public List<CraftingRecipe> getSimilarRecipes(CraftingRecipe craftingRecipe){
        List<CraftingRecipe> similar = new ArrayList<>();
        for(CraftingRecipe recipe : getCraftingRecipes()){
            if(recipe.isSimilar(craftingRecipe)){

            }
        }
        return similar;
    }



    //FURNACE RECIPES

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



    public FurnaceCRecipe getFurnaceRecipe(String key) {
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getID().equals(key)) {
                return recipe;
            }
        }
        return null;
    }

    public FurnaceCRecipe getFurnaceRecipe(ItemStack source) {
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getSource().getType() == source.getType()) {
                return recipe;
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

    //CUSTOM ITEMS

    public List<CustomItem> getCustomItems() {
        return customItems;
    }

    public CustomItem getCustomItem(String key) {
        for (CustomItem customItem : customItems) {
            if (customItem.getId().equals(key)) {
                return customItem;
            }
        }
        return null;
    }

    public void addCustomItem(CustomItem item) {
        customItems.add(item);
    }

    public void removeCustomItem(String id) {
        customItems.remove(getCustomItem(id));
    }

    public void removeCustomItem(CustomItem item) {
        customItems.remove(item);
    }

    public CustomItem getCustomItem(String key, String name) {
        return getCustomItem(key + ":" + name);
    }


    //DISABLED RECIPES AND GET ALL RECIPES

    public ArrayList<String> getDisabledRecipes() {
        return disabledRecipes;
    }

    public HashMap<String, List<String>> getOverrideRecipes() {
        return overrideRecipes;
    }

    public void setOverrideRecipe(String original, List<String> overrides) {
        overrideRecipes.put(original, overrides);
    }

    public void addOverrideRecipe(String original, String override){
        List<String> overrides = overrideRecipes.getOrDefault(original, new ArrayList<>());
        overrides.add(override);
        setOverrideRecipe(original, overrides);
    }

    public void removeOverrideRecipe(String override){

    }

    public List<Recipe> getAllRecipes() {
        allRecipes.clear();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while(iterator.hasNext()){
            Recipe recipe = iterator.next();
            if(!(recipe instanceof FurnaceRecipe)){
                allRecipes.add(recipe);
            }
        }
        return allRecipes;
    }

    /*

    "D  "     "D"  |  "D  "    "D  "
    "D  "  -> "D"  |  "   " -> "   "
    "   "          |  "  D"    "  D"
                   |
    "D  "    "D "  |  " D "    "D"
    " D " -> " D"  |  " D " -> "D"
    "   "          |  " D "    "D"

     */
    public List<List<ItemStack>> getIngredients(ItemStack[] ingredients){
        List<List<ItemStack>> items = new ArrayList<>();
        int j = 0;
        int r = 0;
        List<String> empty = new ArrayList<>();
        List<ItemStack> row = new ArrayList<>();
        for(ItemStack item : ingredients){
            row.add(item);
            if(++j / 3 > 0){
                if(row.get(0) == null && row.get(1) == null && row.get(2) == null){
                    empty.add("r"+r);
                }
                items.add(new ArrayList<>(row));
                row.clear();
                j = 0;
                r++;
            }
        }
        for(int i = 0; i < 3; i++){
            if(i < items.get(0).size()){
                if(items.get(0).get(i) == null && items.get(1).get(i) == null && items.get(2).get(i) == null){
                    empty.add("c"+i);
                }
            }
        }
        ListIterator<List<ItemStack>> iterator = items.listIterator();
        while(iterator.hasNext()){
            int index = iterator.nextIndex();
            List<ItemStack> list = iterator.next();
            if(empty.contains("r"+index)){
                if(index == 1){
                    if(empty.contains("r0") || empty.contains("r2")){
                        iterator.remove();
                    }
                }else{
                    iterator.remove();
                }
            }else{
                Iterator<ItemStack> rowIterator = list.iterator();
                int cIndex = 0;
                while (rowIterator.hasNext()){
                    ItemStack c = rowIterator.next();
                    if(empty.contains("c"+cIndex)){
                        if(cIndex == 1){
                            if(empty.contains("c0") || empty.contains("c2")){
                                rowIterator.remove();
                            }
                        }else{
                            rowIterator.remove();
                        }
                    }
                    cIndex++;
                }
                //iterator.set(list);
            }
        }
        api.sendDebugMessage("Result:");
        for(List<ItemStack> itemStacks : items){
            api.sendDebugMessage(" - ");
            for(ItemStack itemStack : itemStacks){
                api.sendDebugMessage(""+itemStack);
            }
        }
        return items;
    }

}
