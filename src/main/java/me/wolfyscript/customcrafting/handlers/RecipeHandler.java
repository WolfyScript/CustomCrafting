package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ShapedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingConfig;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
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
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneConfig;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
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
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ItemConfig;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class RecipeHandler {

    private List<Recipe> allRecipes = new ArrayList<>();

    private TreeMap<String, CustomRecipe> customRecipes = new TreeMap<>();

    private ArrayList<String> disabledRecipes = new ArrayList<>();

    private List<Particles> particlesList;
    private List<ParticleEffects> particleEffectsList;

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
        this.particlesList = new ArrayList<>();
        this.particleEffectsList = new ArrayList<>();
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
                    loadConfig(folder.getName(), "grindstone");
                    loadConfig(folder.getName(), "brewing");
                    loadConfig(folder.getName(), "elite_workbench");
                }
            }
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                api.sendConsoleMessage("");
                api.sendConsoleMessage("$msg.startup.recipes.particles$");
                for (File folder : subFolders) {
                    api.sendConsoleMessage("- " + folder.getName());
                    loadConfig(folder.getName(), "particles");
                }
            }
        }
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
        LinkedList<String> recipes = new LinkedList<>();
        workbench.listFiles((dir, name) -> {
            if (name.contains(".")) {
                String key = name.substring(0, name.lastIndexOf("."));
                String fileType = name.substring(name.lastIndexOf(".") + 1);
                if (fileType.equalsIgnoreCase("json")) {
                    recipes.add(key);
                } else {
                    api.sendConsoleMessage("$msg.startup.recipes.incompatible$", new String[]{"%namespace%", subfolder}, new String[]{"%key%", key}, new String[]{"%file_type%", fileType});
                }
            }
            return true;
        });
        if (!recipes.isEmpty()) {
            for (String name : recipes) {
                try {
                    switch (type) {
                        case "items":
                            ItemConfig itemConfig = new ItemConfig(subfolder, name, false, configAPI);
                            CustomItems.setCustomItem(itemConfig);
                            break;
                        case "particles":
                            Particles particles = new Particles(configAPI, subfolder, CustomCrafting.getInst().getDataFolder().getAbsolutePath() + File.separator + "recipes");
                            particles.loadParticles();
                            particlesList.add(particles);
                            ParticleEffects particleEffects = new ParticleEffects(configAPI, subfolder, CustomCrafting.getInst().getDataFolder().getAbsolutePath() + File.separator + "recipes");
                            particleEffects.loadEffects();
                            particleEffectsList.add(particleEffects);
                            break;
                        case "workbench":
                            AdvancedCraftConfig config = new AdvancedCraftConfig(configAPI, subfolder, name);
                            if (config.isShapeless()) {
                                registerRecipe(new ShapelessCraftRecipe(config));
                            } else {
                                registerRecipe(new ShapedCraftRecipe(config));
                            }
                            break;
                        case "elite_workbench":
                            EliteCraftConfig eliteCraftConfig = new EliteCraftConfig(configAPI, subfolder, name);
                            if (eliteCraftConfig.isShapeless()) {
                                registerRecipe(new ShapelessEliteCraftRecipe(eliteCraftConfig));
                            } else {
                                registerRecipe(new ShapedEliteCraftRecipe(eliteCraftConfig));
                            }
                            break;
                        case "furnace":
                            registerRecipe(new CustomFurnaceRecipe(new FurnaceConfig(configAPI, subfolder, name)));
                            break;
                        case "anvil":
                            registerRecipe(new CustomAnvilRecipe(new AnvilConfig(configAPI, subfolder, name)));
                            break;
                        case "blast_furnace":
                            registerRecipe(new CustomBlastRecipe(new BlastingConfig(configAPI, subfolder, name)));
                            break;
                        case "smoker":
                            registerRecipe(new CustomSmokerRecipe(new SmokerConfig(configAPI, subfolder, name)));
                            break;
                        case "campfire":
                            registerRecipe(new CustomCampfireRecipe(new CampfireConfig(configAPI, subfolder, name)));
                            break;
                        case "stonecutter":
                            registerRecipe(new CustomStonecutterRecipe(new StonecutterConfig(configAPI, subfolder, name)));
                            break;
                        case "cauldron":
                            registerRecipe(new CauldronRecipe(new CauldronConfig(configAPI, subfolder, name)));
                            break;
                        case "grindstone":
                            registerRecipe(new GrindstoneRecipe(new GrindstoneConfig(configAPI, subfolder, name)));
                            break;
                        case "brewing":
                            registerRecipe(new BrewingRecipe(new BrewingConfig(configAPI, subfolder, name)));
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
        for (Particles particles : particlesList) {
            particles.setParticles();
            particles.save();
        }
        for (ParticleEffects particleEffects : particleEffectsList) {
            particleEffects.setEffects();
            particleEffects.save();
        }
    }

    private void loadDataBase() {
        DataBaseHandler dataBaseHandler = CustomCrafting.getDataBaseHandler();
        try {
            api.sendConsoleMessage("$msg.startup.recipes.title$");
            dataBaseHandler.loadItems();
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
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "grindstone");
                    migrateConfigToDB(dataBaseHandler, folder.getName(), "brewing");
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
                                dataBaseHandler.updateItem(new ItemConfig(subfolder, name, "json", configAPI));
                                break;
                            case "stonecutter":
                                dataBaseHandler.updateRecipe(new StonecutterConfig(configAPI, key, name));
                                break;
                            case "cauldron":
                                dataBaseHandler.updateRecipe(new CauldronConfig(configAPI, key, name));
                                break;
                            case "brewing":
                                dataBaseHandler.updateRecipe(new BrewingConfig(configAPI, key, name));
                        }
                    } catch (Exception ex) {
                        ChatUtils.sendRecipeItemLoadingError(key, name, type, ex);
                    }
                }
            }
        }
    }

    public void registerRecipe(CustomRecipe recipe) {
        if (recipe instanceof Recipe) {
            api.sendDebugMessage("  add to Bukkit...");
            Bukkit.addRecipe((Recipe) recipe);
        }
        api.sendDebugMessage("  add to cache...");
        customRecipes.put(recipe.getId(), recipe);
    }

    public void injectRecipe(CustomRecipe recipe) {
        api.sendDebugMessage("Inject Recipe:");
        if (recipe instanceof Recipe) {
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

    public List<String> getNamespaces() {
        List<String> namespaces = new ArrayList<>();
        for (String namespace : customRecipes.keySet()) {

            if (!namespaces.contains(namespace.split(":")[0])) {
                namespaces.add(namespace.split(":")[0]);
            }
        }
        return namespaces;
    }

    public List<CustomRecipe> getRecipesByNamespace(String namespace) {
        List<CustomRecipe> namespaceRecipes = new ArrayList<>();
        for (Map.Entry<String, CustomRecipe> recipeEntry : customRecipes.entrySet()) {
            if (recipeEntry.getKey().split(":")[0].equalsIgnoreCase(namespace)) {
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
                    ShapedCraftingRecipe recipe = ((ShapedCraftingRecipe) customRecipe);
                    if (items.size() > 0 && recipe.getShape().length > 0 && items.size() == recipe.getShape().length && items.get(0).size() == recipe.getShape()[0].length()) {
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
                return new ArrayList<>(getEliteCraftingRecipes());
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
            case "grindstone":
                return new ArrayList<>(getGrindstoneRecipes());
            case "brewing":
                return new ArrayList<>(getBrewingRecipes());

        }
        return customRecipes;
    }

    public List<CustomRecipe> getRecipes(CustomItem result) {
        List<CustomRecipe> recipeList = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe.getCustomResults().contains(result)) {
                recipeList.add(recipe);
            }
        }
        return recipeList;
    }

    //CRAFTING RECIPES
    public AdvancedCraftingRecipe getAdvancedCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof AdvancedCraftingRecipe ? (AdvancedCraftingRecipe) customRecipe : null;
    }

    public <T extends CustomRecipe> List<T> getRecipes(Class<T> type) {
        List<T> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (type.isInstance(recipe)) {
                recipes.add((T) recipe);
            }
        }
        return recipes;
    }

    public <T extends CustomRecipe> List<T> getAvailableRecipes(Class<T> type) {
        List<T> recipes = getRecipes(type);
        Iterator<T> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            T recipe = iterator.next();
            if (recipe.isHidden() || CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getId())) {
                iterator.remove();
            }
        }
        recipes.sort(Comparator.comparing(CustomRecipe::getPriority));
        return recipes;
    }

    public List<AdvancedCraftingRecipe> getAdvancedCraftingRecipes() {
        return getRecipes(AdvancedCraftingRecipe.class);
    }

    public List<EliteCraftingRecipe> getEliteCraftingRecipes() {
        return getRecipes(EliteCraftingRecipe.class);
    }

    public List<CustomFurnaceRecipe> getFurnaceRecipes() {
        return getRecipes(CustomFurnaceRecipe.class);
    }

    public List<CustomSmokerRecipe> getSmokerRecipes() {
        return getRecipes(CustomSmokerRecipe.class);
    }

    public List<CustomBlastRecipe> getBlastRecipes() {
        return getRecipes(CustomBlastRecipe.class);
    }

    public List<CustomCampfireRecipe> getCampfireRecipes() {
        return getRecipes(CustomCampfireRecipe.class);
    }

    public List<CustomStonecutterRecipe> getStonecutterRecipes() {
        return getRecipes(CustomStonecutterRecipe.class);
    }

    public List<CustomAnvilRecipe> getAnvilRecipes() {
        return getRecipes(CustomAnvilRecipe.class);
    }

    public List<CauldronRecipe> getCauldronRecipes() {
        return getRecipes(CauldronRecipe.class);
    }

    public List<GrindstoneRecipe> getGrindstoneRecipes() {
        return getRecipes(GrindstoneRecipe.class);
    }

    public List<BrewingRecipe> getBrewingRecipes() {
        return getRecipes(BrewingRecipe.class);
    }

    /*
    Get the available recipes only.
    Disabled and hidden recipes are removed!
    For the crafting recipes you also need permissions to view them.
     */
    public List<AdvancedCraftingRecipe> getAvailableAdvancedCraftingRecipes(Player player) {
        List<AdvancedCraftingRecipe> recipes = getAvailableRecipes(AdvancedCraftingRecipe.class);
        Iterator<AdvancedCraftingRecipe> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            AdvancedCraftingRecipe recipe = iterator.next();
            if (!recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                iterator.remove();
            }
        }
        return recipes;
    }

    public List<EliteCraftingRecipe> getAvailableEliteCraftingRecipes(Player player) {
        List<EliteCraftingRecipe> recipes = getAvailableRecipes(EliteCraftingRecipe.class);
        Iterator<EliteCraftingRecipe> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            EliteCraftingRecipe recipe = iterator.next();
            if (!recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                iterator.remove();
            }
        }
        return recipes;
    }

    public List<CustomFurnaceRecipe> getAvailableFurnaceRecipes() {
        return getAvailableRecipes(CustomFurnaceRecipe.class);
    }

    public List<CustomSmokerRecipe> getAvailableSmokerRecipes() {
        return getAvailableRecipes(CustomSmokerRecipe.class);
    }

    public List<CustomBlastRecipe> getAvailableBlastRecipes() {
        return getAvailableRecipes(CustomBlastRecipe.class);
    }

    public List<CustomCampfireRecipe> getAvailableCampfireRecipes() {
        return getAvailableRecipes(CustomCampfireRecipe.class);
    }

    public List<CustomStonecutterRecipe> getAvailableStonecutterRecipes() {
        return getAvailableRecipes(CustomStonecutterRecipe.class);
    }

    public List<CustomAnvilRecipe> getAvailableAnvilRecipes(Player player) {
        List<CustomAnvilRecipe> recipes = getAvailableRecipes(CustomAnvilRecipe.class);
        Iterator<CustomAnvilRecipe> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            CustomAnvilRecipe recipe = iterator.next();
            if (!recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                iterator.remove();
            }
        }
        return recipes;
    }

    public List<CauldronRecipe> getAvailableCauldronRecipes() {
        return getAvailableRecipes(CauldronRecipe.class);
    }

    public List<GrindstoneRecipe> getAvailableGrindstoneRecipes(Player player) {
        List<GrindstoneRecipe> recipes = getAvailableRecipes(GrindstoneRecipe.class);
        Iterator<GrindstoneRecipe> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            GrindstoneRecipe recipe = iterator.next();
            if (!recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                iterator.remove();
            }
        }
        return recipes;
    }

    public List<BrewingRecipe> getAvailableBrewingRecipes(Player player) {
        List<BrewingRecipe> recipes = getAvailableRecipes(BrewingRecipe.class);
        Iterator<BrewingRecipe> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            BrewingRecipe recipe = iterator.next();
            if (!recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))) {
                iterator.remove();
            }
        }
        return recipes;
    }

    public TreeMap<String, CustomRecipe> getRecipes() {
        return customRecipes;
    }

    //DISABLED RECIPES AND GET ALL RECIPES
    public ArrayList<String> getDisabledRecipes() {
        return disabledRecipes;
    }

    public List<Recipe> getVanillaRecipes() {
        if (allRecipes.isEmpty()) {
            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe recipe = iterator.next();
                if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || (WolfyUtilities.hasVillagePillageUpdate() && recipe instanceof CookingRecipe) || recipe instanceof FurnaceRecipe) {
                    if (recipe instanceof Keyed && ((Keyed) recipe).getKey().toString().startsWith("minecraft")) {
                        allRecipes.add(recipe);
                    }
                }
            }
            allRecipes.sort(Comparator.comparing(o -> ((Keyed) o).getKey().toString()));
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

    public boolean loadRecipeIntoCache(CustomRecipe recipe, GuiHandler guiHandler) {
        TestCache cache = (TestCache) guiHandler.getCustomCache();
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
                if (recipe instanceof CauldronRecipe) {
                    cache.setCauldronConfig(((CauldronRecipe) recipe).getConfig());
                    return true;
                }
                return false;
            case BREWING_STAND:
                if (recipe instanceof BrewingRecipe) {
                    cache.setBrewingConfig(((BrewingRecipe) recipe).getConfig());
                    return true;
                }

        }
        return false;
    }


}
