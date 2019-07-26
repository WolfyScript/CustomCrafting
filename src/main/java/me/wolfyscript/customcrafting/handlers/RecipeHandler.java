package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Anvil;
import me.wolfyscript.customcrafting.data.cache.CookingData;
import me.wolfyscript.customcrafting.data.cache.Stonecutter;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.*;

public class RecipeHandler {

    private List<Recipe> allRecipes = new ArrayList<>();

    private HashMap<String, CustomRecipe> customRecipes = new HashMap<>();
    private HashMap<String, CustomItem> customItems = new HashMap<>();

    private ArrayList<String> disabledRecipes = new ArrayList<>();

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);

        File[] files = workbench.listFiles((dir, name) -> (name.split("\\.").length > 1));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String key = file.getParentFile().getParentFile().getName().toLowerCase();
                String name = fileName.substring(0, file.getName().lastIndexOf("."));
                String fileType = fileName.substring(file.getName().lastIndexOf(".") + 1);
                try {
                    switch (type) {
                        case "workbench":
                            CraftConfig config = new CraftConfig(configAPI, key, name, fileType);
                            if (config.isShapeless()) {
                                registerRecipe(new ShapelessCraftRecipe(config));
                            } else {
                                registerRecipe(new ShapedCraftRecipe(config));
                            }
                            break;
                        case "furnace":
                            registerRecipe(new CustomFurnaceRecipe(new FurnaceConfig(configAPI, key, name, fileType)));
                            break;
                        case "anvil":
                            registerRecipe(new CustomAnvilRecipe(new AnvilConfig(configAPI, key, name, fileType)));
                            break;
                        case "blast_furnace":
                            registerRecipe(new CustomBlastRecipe(new BlastingConfig(configAPI, key, name, fileType)));
                            break;
                        case "smoker":
                            registerRecipe(new CustomSmokerRecipe(new SmokerConfig(configAPI, key, name, fileType)));
                            break;
                        case "campfire":
                            registerRecipe(new CustomCampfireRecipe(new CampfireConfig(configAPI, key, name, fileType)));
                            break;
                        case "items":
                            ItemConfig itemConfig = new ItemConfig(configAPI, key, name, fileType);
                            customItems.put(itemConfig.getId(), new CustomItem(itemConfig));
                            break;
                        case "stonecutter":
                            registerRecipe(new CustomStonecutterRecipe(new StonecutterConfig(configAPI, key, name, fileType)));
                            break;
                    }
                } catch (Exception ex) {
                    api.sendConsoleMessage("-------------------------------------------------");
                    api.sendConsoleMessage("Error loading Contents for: " + key + ":" + name);
                    api.sendConsoleMessage("    Type: " + type);
                    api.sendConsoleMessage("    Message: " + ex.getMessage());
                    if (ex.getCause() != null) {
                        api.sendConsoleMessage("    Cause: " + ex.getCause().getMessage());
                    }
                    api.sendConsoleMessage("You should check the config for empty settings ");
                    api.sendConsoleMessage("e.g. No set Result or Source Item!");
                    api.sendConsoleMessage("------------------[StackTrace]-------------------");
                    ex.printStackTrace();
                    if (ex.getCause() != null) {
                        api.sendConsoleMessage("Caused StackTrace: ");
                        ex.getCause().printStackTrace();
                    }
                    api.sendConsoleMessage("------------------[StackTrace]-------------------");
                }
            }
        }
    }

    public void onSave() {
        CustomCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        CustomCrafting.getConfigHandler().getConfig().save();
    }

    public void loadDataBase() {

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
                loadConfig(folder.getName(), "anvil");

                if (WolfyUtilities.hasVillagePillageUpdate()) {
                    loadConfig(folder.getName(), "blast_furnace");
                    loadConfig(folder.getName(), "smoker");
                    loadConfig(folder.getName(), "campfire");
                    loadConfig(folder.getName(), "stonecutter");
                }
            }
        }
    }

    public void registerRecipe(CustomRecipe recipe) {
        if (!(recipe instanceof CustomAnvilRecipe)) {
            api.sendDebugMessage("  add to Bukkit...");
            Bukkit.addRecipe(recipe);
        }
        api.sendDebugMessage("  add to cache...");
        customRecipes.put(recipe.getId(), recipe);
    }

    public void injectRecipe(CustomRecipe recipe) {
        api.sendDebugMessage("Inject Recipe:");
        api.sendDebugMessage("  unregister old recipe:");
        unregisterRecipe(recipe);
        registerRecipe(recipe);
    }

    public void unregisterRecipe(String key) {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        boolean inject = false;
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (((Keyed) recipe).getKey().toString().equals(key)) {
                if (!inject) {
                    inject = true;
                }
                recipeIterator.remove();
            }
        }
        if (inject) {
            Bukkit.resetRecipes();
            while (recipeIterator.hasNext()) {
                Bukkit.addRecipe(recipeIterator.next());
            }
        }
    }

    public void unregisterRecipe(CustomRecipe customRecipe) {
        customRecipes.remove(customRecipe.getId());
        if (!(customRecipe instanceof CustomAnvilRecipe)) {
            unregisterRecipe(customRecipe.getId());
        }
    }

    /*
        Get all the ShapedRecipes from this group
     */
    public List<CustomRecipe> getRecipeGroup(String group) {
        List<CustomRecipe> groupRecipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            if (customRecipes.get(id).getGroup().equals(group))
                groupRecipes.add(customRecipes.get(id));
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
        return customRecipes.get(key);
    }

    public List<CustomRecipe> getRecipes(String type) {
        List<CustomRecipe> customRecipes = new ArrayList<>();
        switch (type) {
            case "workbench":
                customRecipes.addAll(getCraftingRecipes());
                break;
            case "furnace":
                customRecipes.addAll(getFurnaceRecipes());
                break;
            case "anvil":
                customRecipes.addAll(getAnvilRecipes());
                break;
            case "blast_furnace":
                customRecipes.addAll(getBlastRecipes());
                break;
            case "smoker":
                customRecipes.addAll(getSmokerRecipes());
                break;
            case "campfire":
                customRecipes.addAll(getCampfireRecipes());
                break;
            case "stonecutter":
                customRecipes.addAll(getStonecutterRecipes());
        }
        return customRecipes;
    }

    public List<CustomRecipe> getRecipes(Setting setting) {
        return getRecipes(setting.toString().toLowerCase(Locale.ROOT));
    }

    //CRAFTING RECIPES
    public CraftingRecipe getCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof CraftingRecipe ? (CraftingRecipe) customRecipe : null;
    }

    public List<CraftingRecipe> getCraftingRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe customRecipe = customRecipes.get(id);
            if (customRecipe instanceof CraftingRecipe) {
                recipes.add((CraftingRecipe) customRecipe);
            }
        }
        return recipes;
    }

    //FURNACE RECIPES
    public List<CustomFurnaceRecipe> getFurnaceRecipes() {
        List<CustomFurnaceRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe recipe = customRecipes.get(id);
            if (recipe instanceof CustomFurnaceRecipe) {
                recipes.add((CustomFurnaceRecipe) recipe);
            }
        }
        return recipes;
    }

    //SMOKER RECIPES
    public List<CustomSmokerRecipe> getSmokerRecipes() {
        List<CustomSmokerRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe recipe = customRecipes.get(id);
            if (recipe instanceof CustomSmokerRecipe) {
                recipes.add((CustomSmokerRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomBlastRecipe> getBlastRecipes() {
        List<CustomBlastRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe recipe = customRecipes.get(id);
            if (recipe instanceof CustomBlastRecipe) {
                recipes.add((CustomBlastRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomCampfireRecipe> getCampfireRecipes() {
        List<CustomCampfireRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe recipe = customRecipes.get(id);
            if (recipe instanceof CustomCampfireRecipe) {
                recipes.add((CustomCampfireRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomStonecutterRecipe> getStonecutterRecipes() {
        List<CustomStonecutterRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe recipe = customRecipes.get(id);
            if (recipe instanceof CustomStonecutterRecipe) {
                recipes.add((CustomStonecutterRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomAnvilRecipe> getAnvilRecipes() {
        List<CustomAnvilRecipe> recipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            CustomRecipe recipe = customRecipes.get(id);
            if (recipe instanceof CustomAnvilRecipe) {
                recipes.add((CustomAnvilRecipe) recipe);
            }
        }
        return recipes;
    }

    public CustomFurnaceRecipe getFurnaceRecipe(String key) {
        for (CustomFurnaceRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getId().equals(key)) {
                return recipe;
            }
        }
        return null;
    }

    public HashMap<String, CustomRecipe> getRecipes() {
        return customRecipes;
    }

    //CUSTOM ITEMS
    public List<CustomItem> getCustomItems() {
        return new ArrayList<>(customItems.values());
    }

    public CustomItem getCustomItem(String key) {
        return getCustomItem(key, true);
    }

    public CustomItem getCustomItem(String key, boolean replace) {
        if (customItems.containsKey(key) && customItems.get(key) != null) {
            if (replace)
                return customItems.get(key).getRealItem();
            return customItems.get(key).clone();
        }
        return null;
    }

    public void addCustomItem(CustomItem item) {
        customItems.put(item.getId(), item);
    }

    public void removeCustomItem(String id) {
        customItems.remove(id);
    }

    public void removeCustomItem(CustomItem item) {
        customItems.remove(item.getId());
    }

    public CustomItem getCustomItem(String key, String name) {
        return getCustomItem(key + ":" + name);
    }

    public CustomItem getCustomItem(String key, String name, boolean replace) {
        return getCustomItem(key + ":" + name, replace);
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
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                if (!(recipe instanceof CookingRecipe)) {
                    allRecipes.add(recipe);
                }

            } else if (!(recipe instanceof FurnaceRecipe)) {
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
                for (int i = 0; i < rowLength; i++) {
                    if (row.get(i) != null) {
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
        for (int i = 0; i < items.get(0).size(); i++) {
            boolean blocked = false;
            for (List<ItemStack> item : items) {
                if (item.get(i) != null) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                empty.add("c" + i);
            }
        }
        ListIterator<List<ItemStack>> iterator = items.listIterator();
        int index = 0;
        while (iterator.hasNext()) {
            List<ItemStack> list = iterator.next();
            if (empty.contains("r" + index)) {
                if (index == 1) {
                    if (rowLength == 2 || (empty.contains("r0") || empty.contains("r2"))) {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            } else {
                Iterator<ItemStack> rowIterator = list.iterator();
                int cIndex = 0;
                while (rowIterator.hasNext()) {
                    rowIterator.next();
                    if (empty.contains("c" + cIndex)) {
                        if (cIndex == 1) {
                            if (rowLength == 2 || (empty.contains("c0") || empty.contains("c2"))) {
                                rowIterator.remove();
                            }
                        } else {
                            rowIterator.remove();
                        }
                    }
                    cIndex++;
                }
            }
            index++;
        }
        return items;
    }


    public boolean loadRecipeIntoCache(CustomRecipe recipe, Player player) {
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        switch (cache.getSetting()) {
            case WORKBENCH:
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
                    return true;
                }
                return false;
            case ANVIL:
                if (recipe instanceof CustomAnvilRecipe) {
                    cache.resetAnvil();
                    Anvil anvil = cache.getAnvil();
                    anvil.setResult(recipe.getCustomResult());
                    anvil.setInputLeft(((CustomAnvilRecipe) recipe).getInputLeft());
                    anvil.setInputRight(((CustomAnvilRecipe) recipe).getInputRight());
                    anvil.setDurability(((CustomAnvilRecipe) recipe).getDurability());
                    anvil.setRepairCost(((CustomAnvilRecipe) recipe).getRepairCost());
                    anvil.setMode(((CustomAnvilRecipe) recipe).getMode());
                    anvil.setBlockRename(((CustomAnvilRecipe) recipe).isBlockRename());
                    anvil.setBlockEnchant(((CustomAnvilRecipe) recipe).isBlockEnchant());
                    anvil.setBlockRepair(((CustomAnvilRecipe) recipe).isBlockRepair());
                    anvil.setExactMeta(recipe.isExactMeta());
                    anvil.setPermissions(((CustomAnvilRecipe) recipe).isPermission());
                    anvil.setPriority(recipe.getPriority());
                    anvil.setMenu(Anvil.Menu.MAINMENU);
                    return true;
                }
                return false;
            case STONECUTTER:
                if (recipe instanceof CustomStonecutterRecipe) {
                    cache.resetStonecutter();
                    Stonecutter stonecutter = cache.getStonecutter();
                    stonecutter.setResult(recipe.getCustomResult());
                    stonecutter.setSource(((CustomStonecutterRecipe) recipe).getSource());
                    stonecutter.setExactMeta(recipe.isExactMeta());
                    stonecutter.setPriority(recipe.getPriority());
                    return true;
                }
                return false;
            case CAMPFIRE:
            case BLAST_FURNACE:
            case SMOKER:
            case FURNACE:
                if (recipe instanceof CustomCookingRecipe) {
                    cache.resetCookingData();
                    CookingData furnace = cache.getFurnace();
                    //furnace.setAdvFurnace(((CustomFurnaceRecipe) recipe).needsAdvancedFurnace());
                    furnace.setSource(((CustomCookingRecipe) recipe).getSource());
                    furnace.setResult(recipe.getCustomResult());
                    furnace.setExperience(((CustomCookingRecipe) recipe).getConfig().getXP());
                    furnace.setCookingTime(((CustomCookingRecipe) recipe).getConfig().getCookingTime());
                    furnace.setExactMeta(recipe.isExactMeta());
                    return true;
                }
                return false;
        }
        return false;
    }
}
