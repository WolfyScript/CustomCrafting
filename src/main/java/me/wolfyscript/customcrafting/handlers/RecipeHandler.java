package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
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
import me.wolfyscript.utilities.api.utils.NamespacedKey;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RecipeHandler {

    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final List<Recipe> allRecipes = new ArrayList<>();

    private final TreeMap<NamespacedKey, CustomRecipe> customRecipes = new TreeMap<>();

    private final ArrayList<String> disabledRecipes = new ArrayList<>();

    private final List<Particles> particlesList;
    private final List<ParticleEffects> particleEffectsList;

    private final ConfigAPI configAPI;
    private final WolfyUtilities api;

    public RecipeHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.getAPI(customCrafting);
        this.configAPI = api.getConfigAPI();
        this.customCrafting = customCrafting;
        this.particlesList = new ArrayList<>();
        this.particleEffectsList = new ArrayList<>();
        this.categories = customCrafting.getConfigHandler().getRecipeBookConfig().getCategories();
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
        if (!customCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            disabledRecipes.addAll(customCrafting.getConfigHandler().getConfig().getDisabledRecipes());
        }
        api.sendConsoleMessage("$msg.startup.recipes.title$");
        File recipesFolder = new File(customCrafting.getDataFolder() + File.separator + "recipes");
        List<File> subFolders = null;
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            subFolders = new ArrayList<>(Arrays.asList(dirs));
        }
        if (subFolders != null) {
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.items$");
            subFolders.forEach(folder -> {
                api.sendConsoleMessage("- " + folder.getName());
                loadConfig(folder.getName(), "items");
            });
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.recipes$");
            subFolders.forEach(folder -> {
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

            });
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                api.sendConsoleMessage("");
                api.sendConsoleMessage("$msg.startup.recipes.particles$");
                subFolders.forEach(folder -> {
                    api.sendConsoleMessage("- " + folder.getName());
                    loadConfig(folder.getName(), "particles");
                });
            }
        }
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(customCrafting.getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
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
        recipes.forEach(name -> {
            try {
                switch (type) {
                    case "items":
                        ItemConfig itemConfig = new ItemConfig(subfolder, name, false, configAPI);
                        CustomItems.setCustomItem(itemConfig);
                        break;
                    case "particles":
                        Particles particles = new Particles(configAPI, subfolder, customCrafting.getDataFolder().getAbsolutePath() + File.separator + "recipes");
                        particles.loadParticles();
                        particlesList.add(particles);
                        ParticleEffects particleEffects = new ParticleEffects(configAPI, subfolder, customCrafting.getDataFolder().getAbsolutePath() + File.separator + "recipes");
                        particleEffects.loadEffects();
                        particleEffectsList.add(particleEffects);
                        break;
                    case "workbench":
                        AdvancedCraftConfig config = new AdvancedCraftConfig(customCrafting, subfolder, name);
                        if (config.isShapeless()) {
                            registerRecipe(new ShapelessCraftRecipe(config));
                        } else {
                            registerRecipe(new ShapedCraftRecipe(config));
                        }
                        break;
                    case "elite_workbench":
                        EliteCraftConfig eliteCraftConfig = new EliteCraftConfig(customCrafting, subfolder, name);
                        if (eliteCraftConfig.isShapeless()) {
                            registerRecipe(new ShapelessEliteCraftRecipe(eliteCraftConfig));
                        } else {
                            registerRecipe(new ShapedEliteCraftRecipe(eliteCraftConfig));
                        }
                        break;
                    case "furnace":
                        registerRecipe(new CustomFurnaceRecipe(new FurnaceConfig(customCrafting, subfolder, name)));
                        break;
                    case "anvil":
                        registerRecipe(new CustomAnvilRecipe(new AnvilConfig(customCrafting, subfolder, name)));
                        break;
                    case "blast_furnace":
                        registerRecipe(new CustomBlastRecipe(new BlastingConfig(customCrafting, subfolder, name)));
                        break;
                    case "smoker":
                        registerRecipe(new CustomSmokerRecipe(new SmokerConfig(customCrafting, subfolder, name)));
                        break;
                    case "campfire":
                        registerRecipe(new CustomCampfireRecipe(new CampfireConfig(customCrafting, subfolder, name)));
                        break;
                    case "stonecutter":
                        registerRecipe(new CustomStonecutterRecipe(new StonecutterConfig(customCrafting, subfolder, name)));
                        break;
                    case "cauldron":
                        registerRecipe(new CauldronRecipe(new CauldronConfig(customCrafting, subfolder, name)));
                        break;
                    case "grindstone":
                        registerRecipe(new GrindstoneRecipe(new GrindstoneConfig(customCrafting, subfolder, name)));
                        break;
                    case "brewing":
                        registerRecipe(new BrewingRecipe(new BrewingConfig(customCrafting, subfolder, name)));
                }
            } catch (Exception ex) {
                ChatUtils.sendRecipeItemLoadingError(subfolder, name, type, ex);
            }
        });
    }

    public void onSave() {
        customCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        customCrafting.getConfigHandler().getConfig().save();
        particlesList.forEach(particles -> {
            particles.setParticles();
            particles.save();
        });
        particleEffectsList.forEach(particleEffects -> {
            particleEffects.setEffects();
            particleEffects.save();
        });
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
        File recipesFolder = new File(customCrafting.getDataFolder() + File.separator + "recipes");
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
        File workbench = new File(customCrafting.getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
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
                                dataBaseHandler.updateRecipe(new AdvancedCraftConfig(customCrafting, key, name));
                                break;
                            case "elite_workbench":
                                dataBaseHandler.updateRecipe(new EliteCraftConfig(customCrafting, key, name));
                                break;
                            case "furnace":
                                dataBaseHandler.updateRecipe(new FurnaceConfig(customCrafting, key, name));
                                break;
                            case "anvil":
                                dataBaseHandler.updateRecipe(new AnvilConfig(customCrafting, key, name));
                                break;
                            case "blast_furnace":
                                dataBaseHandler.updateRecipe(new BlastingConfig(customCrafting, key, name));
                                break;
                            case "smoker":
                                dataBaseHandler.updateRecipe(new SmokerConfig(customCrafting, key, name));
                                break;
                            case "campfire":
                                dataBaseHandler.updateRecipe(new CampfireConfig(customCrafting, key, name));
                                break;
                            case "items":
                                dataBaseHandler.updateItem(new ItemConfig(subfolder, name, configAPI));
                                break;
                            case "stonecutter":
                                dataBaseHandler.updateRecipe(new StonecutterConfig(customCrafting, key, name));
                                break;
                            case "cauldron":
                                dataBaseHandler.updateRecipe(new CauldronConfig(customCrafting, key, name));
                                break;
                            case "brewing":
                                dataBaseHandler.updateRecipe(new BrewingConfig(customCrafting, key, name));
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
        customRecipes.put(recipe.getNamespacedKey(), recipe);
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
        customRecipes.remove(customRecipe.getNamespacedKey().toString());
        if (customRecipes.containsKey(customRecipe.getNamespacedKey().toString())) {
            unregisterRecipe(customRecipe.getNamespacedKey().toString());
        }
    }

    /*
        Get all the Recipes from this group
     */
    public List<CustomRecipe> getRecipeGroup(String group) {
        List<CustomRecipe> groupRecipes = new ArrayList<>();
        for (NamespacedKey id : customRecipes.keySet()) {
            if (customRecipes.get(id).getGroup().equals(group))
                groupRecipes.add(customRecipes.get(id));
        }
        return groupRecipes;
    }

    public List<String> getNamespaces() {
        return customRecipes.keySet().stream().map(namespacedKey -> namespacedKey.toString()).collect(Collectors.toList());
    }

    public List<CustomRecipe> getRecipesByNamespace(String namespace) {
        return customRecipes.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(namespace)).map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public List<CraftingRecipe> getSimilarRecipes(List<List<ItemStack>> items, boolean elite, boolean advanced) {
        List<CraftingRecipe> recipes = new ArrayList<>();
        AtomicInteger size = new AtomicInteger();
        items.forEach(itemStacks -> size.addAndGet((int) itemStacks.stream().filter(itemStack -> itemStack != null && !itemStack.getType().equals(Material.AIR)).count()));
        List<CraftingRecipe> craftingRecipes = new ArrayList<>();
        if (elite) {
            craftingRecipes.addAll(getEliteCraftingRecipes());
        }
        if (advanced) {
            craftingRecipes.addAll(getAdvancedCraftingRecipes());
        }
        craftingRecipes.stream().filter(customRecipe -> customRecipe.getIngredients().keySet().size() == size.get()).forEach(customRecipe -> {
            if (customRecipe instanceof ShapedCraftingRecipe) {
                ShapedCraftingRecipe recipe = ((ShapedCraftingRecipe) customRecipe);
                if (items.size() > 0 && recipe.getShape().length > 0 && items.size() == recipe.getShape().length && items.get(0).size() == recipe.getShape()[0].length()) {
                    recipes.add(customRecipe);
                }
            } else {
                recipes.add(customRecipe);
            }
        });
        return recipes;
    }

    public CustomRecipe getRecipe(NamespacedKey namespacedKey) {
        return customRecipes.get(namespacedKey);
    }


    @Deprecated
    public CustomRecipe getRecipe(String key) {
        return customRecipes.get(new NamespacedKey(key.split(":")[0], key.split(":")[1]));
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
        return customRecipes.values().stream().filter(recipe -> recipe.getCustomResults().contains(result)).collect(Collectors.toList());
    }

    //CRAFTING RECIPES
    public AdvancedCraftingRecipe getAdvancedCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof AdvancedCraftingRecipe ? (AdvancedCraftingRecipe) customRecipe : null;
    }

    public <T extends CustomRecipe> List<T> getRecipes(Class<T> type) {
        return customRecipes.values().stream().filter(recipe -> type.isInstance(recipe)).map(recipe -> (T) recipe).collect(Collectors.toList());
    }

    public <T extends CustomRecipe> List<T> getAvailableRecipes(Class<T> type) {
        List<T> recipes = getRecipes(type);
        recipes.removeIf(recipe -> recipe.isHidden() || customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString()));
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
        recipes.removeIf(recipe -> !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        return recipes;
    }

    public List<EliteCraftingRecipe> getAvailableEliteCraftingRecipes(Player player) {
        List<EliteCraftingRecipe> recipes = getAvailableRecipes(EliteCraftingRecipe.class);
        recipes.removeIf(recipe -> !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
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
        recipes.removeIf(recipe -> !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        return recipes;
    }

    public List<CauldronRecipe> getAvailableCauldronRecipes() {
        return getAvailableRecipes(CauldronRecipe.class);
    }

    public List<GrindstoneRecipe> getAvailableGrindstoneRecipes(Player player) {
        List<GrindstoneRecipe> recipes = getAvailableRecipes(GrindstoneRecipe.class);
        recipes.removeIf(recipe -> !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        return recipes;
    }

    public List<BrewingRecipe> getAvailableBrewingRecipes(Player player) {
        List<BrewingRecipe> recipes = getAvailableRecipes(BrewingRecipe.class);
        recipes.removeIf(recipe -> !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        return recipes;
    }

    public TreeMap<NamespacedKey, CustomRecipe> getRecipes() {
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
        ListIterator<List<ItemStack>> iterator = items.listIterator();
        while (iterator.hasNext()) {
            if (!iterator.next().parallelStream().allMatch(Objects::isNull)) break;
            iterator.remove();
        }
        iterator = items.listIterator(items.size());
        while (iterator.hasPrevious()) {
            if (!iterator.previous().parallelStream().allMatch(Objects::isNull)) break;
            iterator.remove();
        }
        if (!items.isEmpty()) {
            while (true) {
                if (checkColumn(items, 0)) {
                    break;
                }
            }
            boolean columnBlocked = false;
            for (int i = items.get(0).size() - 1; !columnBlocked && i > 0; i--) {
                if (checkColumn(items, i)) {
                    columnBlocked = true;
                }
            }
        }
        return items;
    }

    private boolean checkColumn(List<List<ItemStack>> items, int column) {
        if (items.stream().anyMatch(item -> item.get(column) != null)) return true;
        items.forEach(item -> item.remove(column));
        return false;
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

    public Categories getCategories() {
        return categories;
    }
}
