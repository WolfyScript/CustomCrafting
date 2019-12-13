package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ShapedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronConfig;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ItemConfig;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class RecipeHandler {

    private List<Recipe> allRecipes = new ArrayList<>();

    private HashMap<String, CustomRecipe> customRecipes = new HashMap<>();

    private ArrayList<String> disabledRecipes = new ArrayList<>();

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
    }

    public void load() {
        if (CustomCrafting.hasDataBaseHandler()) {
            loadDataBase();
        } else {
            loadConfigs();
        }
        //TEST Recipes. Used when no creator is available!
    }

    private void loadConfigs() {
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
                loadConfig(folder.getName(), "cauldron");

                if (WolfyUtilities.hasVillagePillageUpdate()) {
                    loadConfig(folder.getName(), "blast_furnace");
                    loadConfig(folder.getName(), "smoker");
                    loadConfig(folder.getName(), "campfire");
                    loadConfig(folder.getName(), "stonecutter");
                    loadConfig(folder.getName(), "elite_workbench");
                }
            }
        }
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
        HashMap<String, String> recipes = new HashMap<>();
        workbench.listFiles((dir, name) -> {
            String key = name.substring(0, name.lastIndexOf("."));
            String fileType = name.substring(name.lastIndexOf(".") + 1);
            if (recipes.containsKey(key)) {
                if (recipes.get(key).equals("yml")) {
                    if (fileType.equals("json")) {
                        recipes.put(key, fileType);
                    }
                } else {
                    api.sendConsoleMessage("$msg.startup.recipes.duplicate$", new String[]{"%namespace%", subfolder}, new String[]{"%key%", key}, new String[]{"%file_type%", fileType});
                }
            } else {
                recipes.put(key, fileType);
            }
            return true;
        });
        if (!recipes.isEmpty()) {
            for (Map.Entry<String, String> recipe : recipes.entrySet()) {
                String name = recipe.getKey();
                String fileType = recipe.getValue();
                try {
                    switch (type) {
                        case "workbench":
                            AdvancedCraftConfig config = new AdvancedCraftConfig(configAPI, subfolder, name, fileType);
                            if (config.isShapeless()) {
                                registerRecipe(new ShapelessCraftRecipe(config));
                            } else {
                                registerRecipe(new ShapedCraftRecipe(config));
                            }
                            break;
                        case "elite_workbench":
                            EliteCraftConfig eliteCraftConfig = new EliteCraftConfig(configAPI, subfolder, name, fileType);
                            if (eliteCraftConfig.isShapeless()) {
                                registerRecipe(new ShapelessEliteCraftRecipe(eliteCraftConfig));
                            } else {
                                registerRecipe(new ShapedEliteCraftRecipe(eliteCraftConfig));
                            }
                            break;
                        case "furnace":
                            registerRecipe(new CustomFurnaceRecipe(new FurnaceConfig(configAPI, subfolder, name, fileType)));
                            break;
                        case "anvil":
                            registerRecipe(new CustomAnvilRecipe(new AnvilConfig(configAPI, subfolder, name, fileType)));
                            break;
                        case "blast_furnace":
                            registerRecipe(new CustomBlastRecipe(new BlastingConfig(configAPI, subfolder, name, fileType)));
                            break;
                        case "smoker":
                            registerRecipe(new CustomSmokerRecipe(new SmokerConfig(configAPI, subfolder, name, fileType)));
                            break;
                        case "campfire":
                            registerRecipe(new CustomCampfireRecipe(new CampfireConfig(configAPI, subfolder, name, fileType)));
                            break;
                        case "items":
                            ItemConfig itemConfig = new ItemConfig(subfolder, name, fileType, configAPI);
                            CustomItems.setCustomItem(itemConfig);
                            break;
                        case "stonecutter":
                            registerRecipe(new CustomStonecutterRecipe(new StonecutterConfig(configAPI, subfolder, name, fileType)));
                            break;
                        case "cauldron":
                            registerRecipe(new CauldronRecipe(new CauldronConfig(configAPI, subfolder, name, fileType)));


                    }
                } catch (Exception ex) {
                    ChatUtils.sendRecipeItemLoadingError(subfolder, name, type, ex);
                }
            }
        }
    }

    public void onSave() {
        CustomCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        CustomCrafting.getConfigHandler().getConfig().save();
    }

    private void loadDataBase() {
        DataBaseHandler dataBaseHandler = CustomCrafting.getDataBaseHandler();
        try {
            api.sendConsoleMessage("$msg.startup.recipes.title$");
            dataBaseHandler.loadItems(this);
            dataBaseHandler.loadRecipes(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void migrateConfigsToDB(DataBaseHandler dataBaseHandler) {
        api.sendConsoleMessage("Exporting configs to database...");
        File recipesFolder = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes");
        List<File> subFolders = null;
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            subFolders = new ArrayList<>(Arrays.asList(dirs));
        }
        if (subFolders != null) {
            for (File folder : subFolders) {
                api.sendConsoleMessage("- " + folder.getName());
                migrateConfigToDB(dataBaseHandler, folder.getName(), "items");
            }
            for (File folder : subFolders) {
                api.sendConsoleMessage("- " + folder.getName());
                migrateConfigToDB(dataBaseHandler, folder.getName(), "workbench");
                migrateConfigToDB(dataBaseHandler, folder.getName(), "furnace");
                migrateConfigToDB(dataBaseHandler, folder.getName(), "anvil");
                migrateConfigToDB(dataBaseHandler, folder.getName(), "cauldron");
                if (WolfyUtilities.hasVillagePillageUpdate()) {
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "blast_furnace");
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "smoker");
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "campfire");
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "stonecutter");
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "elite_workbench");
                }
            }
        }
        api.sendConsoleMessage("Exported configs to database successfully.");
    }

    private void migrateConfigToDB(DataBaseHandler dataBaseHandler, String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
        File[] files = workbench.listFiles((dir, name) -> (name.split("\\.").length > 1));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String key = file.getParentFile().getParentFile().getName().toLowerCase();
                String name = fileName.substring(0, file.getName().lastIndexOf("."));
                String fileType = fileName.substring(file.getName().lastIndexOf(".") + 1);
                if (fileType.equals("json")) {
                    try {
                        switch (type) {
                            case "workbench":
                                dataBaseHandler.updateRecipe(new AdvancedCraftConfig(configAPI, key, name));
                                break;
                            case "elite_workbench":
                                dataBaseHandler.updateRecipe(new EliteCraftConfig(configAPI, key, name));
                                break;
                            case "furnace":
                                dataBaseHandler.updateRecipe(new FurnaceConfig(configAPI, key, name));
                                break;
                            case "anvil":
                                dataBaseHandler.updateRecipe(new AnvilConfig(configAPI, key, name));
                                break;
                            case "blast_furnace":
                                dataBaseHandler.updateRecipe(new BlastingConfig(configAPI, key, name));
                                break;
                            case "smoker":
                                dataBaseHandler.updateRecipe(new SmokerConfig(configAPI, key, name));
                                break;
                            case "campfire":
                                dataBaseHandler.updateRecipe(new CampfireConfig(configAPI, key, name));
                                break;
                            case "items":
                                dataBaseHandler.updateItem(new ItemConfig(configAPI, key, name));
                                break;
                            case "stonecutter":
                                dataBaseHandler.updateRecipe(new StonecutterConfig(configAPI, key, name));
                                break;
                            case "cauldron":
                                dataBaseHandler.updateRecipe(new CauldronConfig(configAPI, key, name));
                        }
                    } catch (Exception ex) {
                        ChatUtils.sendRecipeItemLoadingError(key, name, type, ex);
                    }
                }
            }
        }
    }

    public void registerRecipe(CustomRecipe recipe) {
        if (!(recipe instanceof CraftingRecipe) && !(recipe instanceof CustomAnvilRecipe) && !(recipe instanceof CauldronRecipe)) {
            api.sendDebugMessage("  add to Bukkit...");
            Bukkit.addRecipe(recipe);
        }
        api.sendDebugMessage("  add to cache...");
        customRecipes.put(recipe.getId(), recipe);
    }

    public void injectRecipe(CustomRecipe recipe) {
        api.sendDebugMessage("Inject Recipe:");
        if (!(recipe instanceof CraftingRecipe) && !(recipe instanceof CustomAnvilRecipe) && !(recipe instanceof CauldronRecipe)) {
            api.sendDebugMessage("  unregister old recipe:");
            unregisterRecipe(recipe);
        }
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
        if (customRecipes.containsKey(customRecipe.getId())) {
            unregisterRecipe(customRecipe.getId());
        }
    }

    /*
        Get all the Recipes from this group
     */
    public List<CustomRecipe> getRecipeGroup(String group) {
        List<CustomRecipe> groupRecipes = new ArrayList<>();
        for (String id : customRecipes.keySet()) {
            if (customRecipes.get(id).getGroup().equals(group))
                groupRecipes.add(customRecipes.get(id));
        }
        return groupRecipes;
    }

    public List<String> getNamespaces(){
        List<String> namespaces = new ArrayList<>();
        for(String namespace : customRecipes.keySet()){

            if(!namespaces.contains(namespace.split(":")[0])){
                namespaces.add(namespace.split(":")[0]);
            }
        }
        return namespaces;
    }

    public List<CustomRecipe> getRecipesByNamespace(String namespace) {
        List<CustomRecipe> namespaceRecipes = new ArrayList<>();
        for(Map.Entry<String, CustomRecipe> recipeEntry : customRecipes.entrySet()){
            if(recipeEntry.getKey().split(":")[0].equalsIgnoreCase(namespace)){
                namespaceRecipes.add(recipeEntry.getValue());
            }
        }

        return namespaceRecipes;
    }

    public List<CraftingRecipe> getSimilarRecipes(List<List<ItemStack>> items, boolean elite, boolean advanced) {
        List<CraftingRecipe> recipes = new ArrayList<>();
        int size = 0;
        for (List<ItemStack> itemStacks : items) {
            size += (int) itemStacks.stream().filter(itemStack -> itemStack != null && !itemStack.getType().equals(Material.AIR)).count();
        }
        List<CraftingRecipe> craftingRecipes = new ArrayList<>();
        if (elite) {
            craftingRecipes.addAll(getEliteCraftingRecipes());
        }
        if (advanced) {
            craftingRecipes.addAll(getAdvancedCraftingRecipes());
        }
        for (CraftingRecipe customRecipe : craftingRecipes) {
            if (customRecipe.getIngredients().keySet().size() == size) {
                if (customRecipe instanceof ShapedCraftingRecipe) {
                    if (items.size() == ((ShapedCraftingRecipe) customRecipe).getShape().length && items.get(0).size() == ((ShapedCraftingRecipe) customRecipe).getShape()[0].length()) {
                        recipes.add(customRecipe);
                    }
                } else {
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
                return new ArrayList<>(getAdvancedCraftingRecipes());
            case "elite_workbench":
                return new ArrayList<>();
            case "furnace":
                return new ArrayList<>(getFurnaceRecipes());
            case "anvil":
                return new ArrayList<>(getAnvilRecipes());
            case "blast_furnace":
                return new ArrayList<>(getBlastRecipes());
            case "smoker":
                return new ArrayList<>(getSmokerRecipes());
            case "campfire":
                return new ArrayList<>(getCampfireRecipes());
            case "stonecutter":
                return new ArrayList<>(getStonecutterRecipes());
            case "cauldron":
                return new ArrayList<>(getCauldronRecipes());
        }
        return customRecipes;
    }

    public List<CustomRecipe> getRecipes(Setting setting) {
        return getRecipes(setting.toString().toLowerCase(Locale.ROOT));
    }

    //CRAFTING RECIPES
    public AdvancedCraftingRecipe getAdvancedCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof AdvancedCraftingRecipe ? (AdvancedCraftingRecipe) customRecipe : null;
    }

    public List<AdvancedCraftingRecipe> getAdvancedCraftingRecipes() {
        List<AdvancedCraftingRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof AdvancedCraftingRecipe) {
                recipes.add((AdvancedCraftingRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<AdvancedCraftingRecipe> getAvailableAdvancedCraftingRecipes(Player player){
        List<AdvancedCraftingRecipe> recipes = new ArrayList<>();
        for (AdvancedCraftingRecipe recipe : CustomCrafting.getRecipeHandler().getAdvancedCraftingRecipes()) {
            if (recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                if (!CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getId())) {
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }

    public List<EliteCraftingRecipe> getEliteCraftingRecipes() {
        List<EliteCraftingRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof EliteCraftingRecipe) {
                recipes.add((EliteCraftingRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<EliteCraftingRecipe> getAvailableEliteCraftingRecipes(Player player){
        List<EliteCraftingRecipe> recipes = new ArrayList<>();
        for (EliteCraftingRecipe recipe : CustomCrafting.getRecipeHandler().getEliteCraftingRecipes()) {
            if (recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                if (!CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getId())) {
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }

    public CraftingRecipe getCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof CraftingRecipe ? (CraftingRecipe) customRecipe : null;
    }

    public List<CraftingRecipe> getCraftingRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CraftingRecipe) {
                recipes.add((CraftingRecipe) recipe);
            }
        }
        return recipes;
    }

    //FURNACE RECIPES
    public List<CustomFurnaceRecipe> getFurnaceRecipes() {
        List<CustomFurnaceRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CustomFurnaceRecipe) {
                recipes.add((CustomFurnaceRecipe) recipe);
            }
        }
        return recipes;
    }

    //SMOKER RECIPES
    public List<CustomSmokerRecipe> getSmokerRecipes() {
        List<CustomSmokerRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CustomSmokerRecipe) {
                recipes.add((CustomSmokerRecipe) recipe);
            }
        }
        return recipes;
    }

    //Blasting Recipes
    public List<CustomBlastRecipe> getBlastRecipes() {
        List<CustomBlastRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CustomBlastRecipe) {
                recipes.add((CustomBlastRecipe) recipe);
            }
        }
        return recipes;
    }

    //Campfire Recipes
    public List<CustomCampfireRecipe> getCampfireRecipes() {
        List<CustomCampfireRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CustomCampfireRecipe) {
                recipes.add((CustomCampfireRecipe) recipe);
            }
        }
        return recipes;
    }

    //Stonecutter Recipes
    public List<CustomStonecutterRecipe> getStonecutterRecipes() {
        List<CustomStonecutterRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CustomStonecutterRecipe) {
                recipes.add((CustomStonecutterRecipe) recipe);
            }
        }
        return recipes;
    }


    public List<CustomAnvilRecipe> getAnvilRecipes() {
        List<CustomAnvilRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
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

    public List<CauldronRecipe> getCauldronRecipes() {
        List<CauldronRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe instanceof CauldronRecipe) {
                recipes.add((CauldronRecipe) recipe);
            }
        }
        return recipes;
    }

    public HashMap<String, CustomRecipe> getRecipes() {
        return customRecipes;
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

    public List<List<ItemStack>> getIngredients(ItemStack[] ingredients) {
        List<List<ItemStack>> items = new ArrayList<>();
        int gridSize = NumberConversions.toInt(Math.sqrt(ingredients.length));
        for (int y = 0; y < gridSize; y++) {
            items.add(new ArrayList<>(Arrays.asList(ingredients).subList(y * gridSize, gridSize + y * gridSize)));
        }
        ListIterator<List<ItemStack>> listIterator = items.listIterator();
        boolean rowBlocked = false, columnBlocked = false;
        while (!rowBlocked && listIterator.hasNext()) {
            List<ItemStack> row = listIterator.next();
            if (row.parallelStream().allMatch(Objects::isNull)) {
                listIterator.remove();
            } else {
                rowBlocked = true;
            }
        }
        while (listIterator.hasNext()) {
            listIterator.next();
        }
        rowBlocked = false;
        while (!rowBlocked && listIterator.hasPrevious()) {
            List<ItemStack> row = listIterator.previous();
            if (row.parallelStream().allMatch(Objects::isNull)) {
                listIterator.remove();
            } else {
                rowBlocked = true;
            }
        }
        if (!items.isEmpty()) {
            while (!columnBlocked) {
                if (checkColumn(items, 0)) {
                    columnBlocked = true;
                }
            }

            columnBlocked = false;
            int column = items.get(0).size() - 1;
            while (!columnBlocked) {
                if (checkColumn(items, column)) {
                    columnBlocked = true;
                } else {
                    column--;
                }
            }
        }
        return items;
    }

    private boolean checkColumn(List<List<ItemStack>> items, int column) {
        boolean blocked = false;
        for (List<ItemStack> item : items) {
            if (item.get(column) != null) {
                blocked = true;
            }
        }
        if (!blocked) {
            for (List<ItemStack> item : items) {
                item.remove(column);
            }
        }
        return blocked;
    }

    public boolean loadRecipeIntoCache(CustomRecipe recipe, Player player) {
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        switch (cache.getSetting()) {
            case WORKBENCH:
                if (recipe instanceof AdvancedCraftingRecipe) {
                    cache.setAdvancedCraftConfig(((AdvancedCraftingRecipe) recipe).getConfig());
                    return true;
                }
                return false;
            case ELITE_WORKBENCH:
                if (recipe instanceof EliteCraftingRecipe) {
                    cache.setEliteCraftConfig(((EliteCraftingRecipe) recipe).getConfig());
                    return true;
                }
                return false;
            case ANVIL:
                if (recipe instanceof CustomAnvilRecipe) {
                    cache.setAnvilConfig(((CustomAnvilRecipe) recipe).getConfig());
                    return true;
                }
                return false;
            case STONECUTTER:
                if (recipe instanceof CustomStonecutterRecipe) {
                    cache.setStonecutterConfig(((CustomStonecutterRecipe) recipe).getConfig());
                    return true;
                }
                return false;
            case CAMPFIRE:
            case BLAST_FURNACE:
            case SMOKER:
            case FURNACE:
                if (recipe instanceof CustomCookingRecipe) {
                    cache.setCookingConfig(((CustomCookingRecipe) recipe).getConfig());
                    return true;
                }
                return false;
            case CAULDRON:
                if(recipe instanceof CauldronRecipe){
                    cache.setCauldronConfig(((CauldronRecipe) recipe).getConfig());
                    return true;
                }
        }
        return false;
    }
}
