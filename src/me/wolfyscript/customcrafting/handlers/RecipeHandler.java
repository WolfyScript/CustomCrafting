package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.*;

public class RecipeHandler {

    private List<Recipe> allRecipes = new ArrayList<>();

    private List<CustomRecipe> customRecipes = new ArrayList<>();
    private List<CustomItem> customItems = new ArrayList<>();

    private ArrayList<String> disabledRecipes = new ArrayList<>();

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);

        File[] files = workbench.listFiles((dir, name) -> (name.split("\\.").length > 1 && name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml")));
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
                            registerRecipe(new CustomFurnaceRecipe(new FurnaceConfig(configAPI, key, name)));
                            break;
                        case "blast_furnace":
                            registerRecipe(new CustomBlastRecipe(new BlastingConfig(configAPI, key, name)));
                            break;
                        case "smoker":
                            registerRecipe(new CustomSmokerRecipe(new SmokerConfig(configAPI, key, name)));
                            break;
                        case "campfire":
                            registerRecipe(new CustomCampfireRecipe(new CampfireConfig(configAPI, key, name)));
                            break;
                        case "items":
                            customItems.add(new CustomItem(new ItemConfig(configAPI, key, name)));
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

    public void onSave() {
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

    public void injectRecipe(CustomRecipe recipe) {
        unregisterRecipe(recipe);
        Bukkit.addRecipe(recipe);
        customRecipes.add(recipe);
        getRecipes().sort((o1, o2) -> Integer.compare(o2.getPriority().getOrder(), o1.getPriority().getOrder()));
    }

    public void unregisterRecipe(String key) {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (((Keyed) recipe).getKey().toString().equals(key)) {
                recipeIterator.remove();
            }
        }
    }

    public void unregisterRecipe(CustomRecipe customRecipe) {
        customRecipes.removeIf(customRecipe1 -> customRecipe1.getId().equals(customRecipe.getId()));
        unregisterRecipe(customRecipe.getId());
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
        List<CustomRecipe> groupRecipes = new ArrayList<>(getRecipeGroup(recipe.getId()));
        groupRecipes.remove(recipe);
        return groupRecipes;
    }

    public List<CraftingRecipe> getSimilarRecipes(List<List<ItemStack>> items) {
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (CraftingRecipe customRecipe : getCraftingRecipes()) {
            if (customRecipe instanceof ShapedCraftRecipe) {
                if (items.size() == ((ShapedCraftRecipe) customRecipe).getShape().length && items.get(0).size() == ((ShapedCraftRecipe) customRecipe).getShape()[0].length()) {
                    recipes.add(customRecipe);
                }
            } else {
                int i = 0;
                for (List<ItemStack> row : items) {
                    for (ItemStack c : row) {
                        if (c != null) {
                            i++;
                        }
                    }
                }
                if (customRecipe.getIngredients().keySet().size() == i) {
                    recipes.add(customRecipe);
                }
            }
        }
        return recipes;
    }

    public CustomRecipe getRecipe(String key) {
        for (CustomRecipe craftingRecipe : customRecipes) {
            if (craftingRecipe.getId().equals(key)) {
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
        for (CustomRecipe customRecipe : getRecipes()) {
            if (customRecipe instanceof CraftingRecipe) {
                recipes.add((CraftingRecipe) customRecipe);
            }
        }
        return recipes;
    }

    //FURNACE RECIPES
    public List<CustomFurnaceRecipe> getFurnaceRecipes() {
        List<CustomFurnaceRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes) {
            if (recipe instanceof CustomFurnaceRecipe) {
                recipes.add((CustomFurnaceRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomRecipe> getRecipes() {
        return customRecipes;
    }

    public CustomFurnaceRecipe getFurnaceRecipe(String key) {
        for (CustomFurnaceRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getId().equals(key)) {
                return recipe;
            }
        }
        return null;
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

    public List<Recipe> getAllRecipes() {
        allRecipes.clear();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (!(recipe instanceof FurnaceRecipe)) {
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
    public List<List<ItemStack>> getIngredients(ItemStack[] ingredients) {
        api.sendDebugMessage("-----[getting Ingredients]-----");
        List<List<ItemStack>> items = new ArrayList<>();
        int j = 0;
        int r = 0;
        List<String> empty = new ArrayList<>();
        List<ItemStack> row = new ArrayList<>();
        int rowLength = ingredients.length == 9 ? 3 : 2;
        for (ItemStack item : ingredients) {
            row.add(item);
            if (++j / rowLength > 0) {
                boolean blocked = false;
                for(int i = 0; i < rowLength; i++){
                    if(row.get(i) != null){
                       blocked = true;
                    }
                }
                if (!blocked) {
                    empty.add("r" + r);
                }
                items.add(new ArrayList<>(row));
                row.clear();
                j = 0;
                r++;
            }
        }
        api.sendDebugMessage("items: " + items);
        for (int i = 0; i < items.get(0).size(); i++) {
            boolean blocked = false;
            for (int e = 0; e < items.size(); e++) {
                if (items.get(e).get(i) != null) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                empty.add("c" + i);
            }
        }
        api.sendDebugMessage("Empty: " + empty);
        ListIterator<List<ItemStack>> iterator = items.listIterator();
        int index = 0;
        while (iterator.hasNext()) {
            List<ItemStack> list = iterator.next();
            api.sendDebugMessage("row: " + index);
            if (empty.contains("r" + index)) {
                if (index == 1) {
                    if (rowLength == 2 || (empty.contains("r0") || empty.contains("r2"))) {
                        iterator.remove();
                        api.sendDebugMessage("  -> remove");
                    }
                } else {
                    api.sendDebugMessage("  -> remove");
                    iterator.remove();
                }
            } else {
                Iterator<ItemStack> rowIterator = list.iterator();
                int cIndex = 0;
                while (rowIterator.hasNext()) {
                    ItemStack c = rowIterator.next();
                    api.sendDebugMessage("column: " + cIndex);
                    if (empty.contains("c" + cIndex)) {
                        if (cIndex == 1) {
                            if (rowLength == 2 || (empty.contains("c0") || empty.contains("c2"))) {
                                rowIterator.remove();
                                api.sendDebugMessage("  -> remove");
                            }
                        } else {
                            rowIterator.remove();
                            api.sendDebugMessage("  -> remove");
                        }
                    }
                    cIndex++;
                }
            }
            index++;
        }
        api.sendDebugMessage("Result:");
        for (List<ItemStack> itemStacks : items) {
            for (ItemStack itemStack : itemStacks) {
                api.sendDebugMessage("- " + itemStack);
            }
        }
        api.sendDebugMessage("------------------------------");
        return items;
    }
}
