package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RecipeHandler {

    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final List<Recipe> allRecipes = new ArrayList<>();

    private final TreeMap<NamespacedKey, ICustomRecipe> customRecipes = new TreeMap<>();

    private final ArrayList<String> disabledRecipes = new ArrayList<>();

    private final List<Particles> particlesList;
    private final List<ParticleEffects> particleEffectsList;

    private final ConfigAPI configAPI;
    private final WolfyUtilities api;
    private final ObjectMapper objectMapper;

    public RecipeHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.getAPI(customCrafting);
        this.configAPI = api.getConfigAPI();
        this.customCrafting = customCrafting;
        this.particlesList = new ArrayList<>();
        this.particleEffectsList = new ArrayList<>();
        this.categories = customCrafting.getConfigHandler().getRecipeBookConfig().getCategories();
        this.objectMapper = JacksonUtil.getObjectMapper();
    }

    public void load() {
        if (CustomCrafting.hasDataBaseHandler()) {
            loadDataBase();
        } else {
            loadConfigs();
        }
        //TEST Recipes. Used when no creator is available!
        //System.out.println("Test Recipe: ");
    }

    private void loadConfigs() {
        if (!customCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            disabledRecipes.addAll(customCrafting.getConfigHandler().getConfig().getDisabledRecipes());
        }
        api.sendConsoleMessage("$msg.startup.recipes.title$");
        File recipesFolder = new File(customCrafting.getDataFolder() + File.separator + "recipes");
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.items$");
            for (File dir : dirs) {
                api.sendConsoleMessage("- " + dir.getName());
                loadConfig(dir.getName(), "items");
            }
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.recipes$");
            for (File dir : dirs) {
                api.sendConsoleMessage("- " + dir.getName());
                loadConfig(dir.getName(), "workbench");
                loadConfig(dir.getName(), "furnace");
                loadConfig(dir.getName(), "anvil");
                loadConfig(dir.getName(), "cauldron");
                loadConfig(dir.getName(), "blast_furnace");
                loadConfig(dir.getName(), "smoker");
                loadConfig(dir.getName(), "campfire");
                loadConfig(dir.getName(), "stonecutter");
                loadConfig(dir.getName(), "grindstone");
                loadConfig(dir.getName(), "brewing");
                loadConfig(dir.getName(), "elite_workbench");
            }
            api.sendConsoleMessage("");
            api.sendConsoleMessage("$msg.startup.recipes.particles$");
            for (File dir : dirs) {
                api.sendConsoleMessage("- " + dir.getName());
                loadConfig(dir.getName(), "particles");
            }
        }
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(customCrafting.getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
        workbench.listFiles(file -> {
            String name = file.getName();
            if (name.contains(".")) {
                String key = name.substring(0, name.lastIndexOf("."));
                String fileType = name.substring(name.lastIndexOf(".") + 1);
                if (fileType.equalsIgnoreCase("json")) {
                    try {
                        NamespacedKey namespacedKey = new NamespacedKey(subfolder, key);
                        JsonNode node = objectMapper.readTree(file);
                        switch (type) {
                            case "items":
                                CustomItems.addCustomItem(namespacedKey, objectMapper.convertValue(node, CustomItem.class));
                                break;
                            case "particles":
                                Particles particles = new Particles(customCrafting, subfolder, File.separator + "recipes");
                                particles.load();
                                particlesList.add(particles);
                                ParticleEffects particleEffects = new ParticleEffects(customCrafting, subfolder, File.separator + "recipes");
                                particleEffects.load();
                                particleEffectsList.add(particleEffects);
                                break;
                            case "workbench":
                                if (node.path("shapeless").asBoolean()) {
                                    registerRecipe(new ShapelessCraftRecipe(namespacedKey, node));
                                } else {
                                    registerRecipe(new ShapedCraftRecipe(namespacedKey, node));
                                }
                                break;
                            case "elite_workbench":
                                if (node.path("shapeless").asBoolean()) {
                                    registerRecipe(new ShapelessEliteCraftRecipe(namespacedKey, node));
                                } else {
                                    registerRecipe(new ShapedEliteCraftRecipe(namespacedKey, node));
                                }
                                break;
                            case "furnace":
                                registerRecipe(new CustomFurnaceRecipe(namespacedKey, node));
                                break;
                            case "anvil":
                                registerRecipe(new CustomAnvilRecipe(namespacedKey, node));
                                break;
                            case "blast_furnace":
                                registerRecipe(new CustomBlastRecipe(namespacedKey, node));
                                break;
                            case "smoker":
                                registerRecipe(new CustomSmokerRecipe(namespacedKey, node));
                                break;
                            case "campfire":
                                registerRecipe(new CustomCampfireRecipe(namespacedKey, node));
                                break;
                            case "stonecutter":
                                registerRecipe(new CustomStonecutterRecipe(namespacedKey, node));
                                break;
                            case "cauldron":
                                registerRecipe(new CauldronRecipe(namespacedKey, node));
                                break;
                            case "grindstone":
                                registerRecipe(new GrindstoneRecipe(namespacedKey, node));
                                break;
                            case "brewing":
                                registerRecipe(new BrewingRecipe(namespacedKey, node));
                        }
                    } catch (Exception ex) {
                        ChatUtils.sendRecipeItemLoadingError(subfolder, name, type, ex);
                    }
                } else {
                    api.sendConsoleMessage("$msg.startup.recipes.incompatible$", new String[]{"%namespace%", subfolder}, new String[]{"%key%", key}, new String[]{"%file_type%", fileType});
                }
            }
            return true;
        });
    }

    public void onSave() {
        customCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        customCrafting.getConfigHandler().getConfig().save();
        try {
            for (Particles particles : particlesList) {
                particles.save();
            }
            for (ParticleEffects particleEffects : particleEffectsList) {
                particleEffects.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        getRecipes().values().forEach(dataBaseHandler::updateRecipe);
        api.sendConsoleMessage("Exported configs to database successfully.");
    }

    public void registerRecipe(ICustomRecipe recipe) {
        if (recipe instanceof ICustomVanillaRecipe) {
            api.sendDebugMessage("  - add to Bukkit");
            Bukkit.addRecipe(((ICustomVanillaRecipe<?>) recipe).getVanillaRecipe());
        }
        api.sendDebugMessage("  - cache custom recipe");
        customRecipes.put(recipe.getNamespacedKey(), recipe);
    }

    public void injectRecipe(ICustomRecipe recipe) {
        api.sendDebugMessage("[Inject Recipe]");
        api.sendDebugMessage("  - unregister old recipe");
        unregisterRecipe(recipe);
        registerRecipe(recipe);
        api.sendDebugMessage("[- - Done - -]");
    }

    public void unregisterVanillaRecipe(NamespacedKey namespacedKey) {
        if (WolfyUtilities.hasBuzzyBeesUpdate()) {
            api.sendDebugMessage("      -> using new API method");
            Bukkit.removeRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()));
        } else {
            api.sendDebugMessage("      -> using old method");
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            boolean inject = false;
            while (recipeIterator.hasNext()) {
                Recipe recipe = recipeIterator.next();
                if (((Keyed) recipe).getKey().toString().equals(namespacedKey.toString())) {
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
    }

    public void unregisterRecipe(ICustomRecipe customRecipe) {
        customRecipes.remove(customRecipe.getNamespacedKey());
        if (customRecipe instanceof ICustomVanillaRecipe) {
            unregisterVanillaRecipe(customRecipe.getNamespacedKey());
        }
    }

    /*
        Get all the Recipes from this group
     */
    public List<ICustomRecipe> getRecipeGroup(String group) {
        List<ICustomRecipe> groupRecipes = new ArrayList<>();
        for (NamespacedKey id : customRecipes.keySet()) {
            if (customRecipes.get(id).getGroup().equals(group))
                groupRecipes.add(customRecipes.get(id));
        }
        return groupRecipes;
    }

    public List<String> getNamespaces() {
        return customRecipes.keySet().stream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
    }

    public List<ICustomRecipe> getRecipesByNamespace(String namespace) {
        return customRecipes.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<CraftingRecipe> getSimilarRecipes(List<List<ItemStack>> items, boolean elite, boolean advanced) {
        List<CraftingRecipe> recipes = new ArrayList<>();
        AtomicInteger size = new AtomicInteger();

        items.forEach(itemStacks -> size.addAndGet((int) itemStacks.stream().filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count()));

        List<CraftingRecipe> craftingRecipes = new ArrayList<>();
        if (elite) {
            craftingRecipes.addAll(getEliteCraftingRecipes());
        }
        if (advanced) {
            craftingRecipes.addAll(getAdvancedCraftingRecipes());
        }
        craftingRecipes.stream().filter(customRecipe -> customRecipe.getIngredients().keySet().size() == size.get()).forEach(customRecipe -> {
            if (customRecipe instanceof IShapedCraftingRecipe) {
                IShapedCraftingRecipe recipe = ((IShapedCraftingRecipe) customRecipe);
                boolean sizeCheck = items.size() > 0 && recipe.getShape().length > 0;
                if (sizeCheck) {
                    boolean sizeSimilarity = items.size() == recipe.getShape().length;
                    boolean rowSize = items.get(0).size() == recipe.getShape()[0].length();

                    if (sizeSimilarity && rowSize) {
                        recipes.add(customRecipe);
                    }
                }
            } else {
                recipes.add(customRecipe);
            }
        });
        return recipes;
    }

    public ICustomRecipe getRecipe(NamespacedKey namespacedKey) {
        return customRecipes.get(namespacedKey);
    }

    @Deprecated
    public ICustomRecipe getRecipe(String key) {
        return customRecipes.get(new NamespacedKey(key.split(":")[0], key.split(":")[1]));
    }

    public List<ICustomRecipe> getRecipes(String type) {
        List<ICustomRecipe> customRecipes = new ArrayList<>();
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

    public List<ICustomRecipe> getRecipes(CustomItem result) {
        return customRecipes.values().stream().filter(recipe -> recipe.getCustomResults().contains(result)).collect(Collectors.toList());
    }

    public List<ICustomRecipe> getAvailableRecipes(CustomItem result, Player player) {
        return getAvailableRecipes(ICustomRecipe.class, player).stream().filter(recipe -> recipe.getCustomResults().contains(result)).collect(Collectors.toList());
    }

    public List<ICustomRecipe> getAvailableRecipesBySimilarResult(ItemStack result, Player player) {
        return getAvailableRecipes(ICustomRecipe.class, player).stream().filter(recipe -> recipe.getCustomResults().stream().anyMatch(customItem -> customItem.create().isSimilar(result))).collect(Collectors.toList());
    }

    //CRAFTING RECIPES
    public CraftingRecipe getAdvancedCraftingRecipe(String key) {
        ICustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof CraftingRecipe ? (CraftingRecipe) customRecipe : null;
    }

    public List<ICustomRecipe> getRecipes(RecipeType type) {
        return customRecipes.values().stream().filter(recipe -> recipe.getRecipeType().equals(type)).collect(Collectors.toList());
    }

    public <T extends ICustomRecipe> List<T> getRecipes(Class<T> type) {
        return customRecipes.values().stream().filter(type::isInstance).map(recipe -> (T) recipe).collect(Collectors.toList());
    }

    public <T extends ICustomRecipe> List<T> getAvailableRecipes(Class<T> type) {
        List<T> recipes = getRecipes(type);
        recipes.removeIf(recipe -> recipe.isHidden() || customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString()));
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return recipes;
    }

    public <T extends ICustomRecipe> List<T> getAvailableRecipes(Class<T> type, Player player) {
        List<T> recipes = getRecipes(type);
        recipes.removeIf(recipe -> recipe.isHidden() || customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString()));
        if (player != null) {
            recipes.removeIf(recipe -> !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        }
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return recipes;
    }

    public List<CraftingRecipe> getAdvancedCraftingRecipes() {
        return getRecipes(CraftingRecipe.class);
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
    public List<CraftingRecipe> getAvailableAdvancedCraftingRecipes(Player player) {
        return getAvailableRecipes(CraftingRecipe.class, player);
    }

    public List<EliteCraftingRecipe> getAvailableEliteCraftingRecipes(Player player) {
        return getAvailableRecipes(EliteCraftingRecipe.class, player);
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
        return getAvailableRecipes(CustomAnvilRecipe.class, player);
    }

    public List<CauldronRecipe> getAvailableCauldronRecipes() {
        return getAvailableRecipes(CauldronRecipe.class);
    }

    public List<GrindstoneRecipe> getAvailableGrindstoneRecipes(Player player) {
        return getAvailableRecipes(GrindstoneRecipe.class, player);
    }

    public List<BrewingRecipe> getAvailableBrewingRecipes(Player player) {
        return getAvailableRecipes(BrewingRecipe.class, player);
    }

    public TreeMap<NamespacedKey, ICustomRecipe> getRecipes() {
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
                if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof CookingRecipe) {
                    if (((Keyed) recipe).getKey().toString().startsWith("minecraft")) {
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
            if (!iterator.next().stream().allMatch(Objects::isNull)) break;
            iterator.remove();
        }
        iterator = items.listIterator(items.size());
        while (iterator.hasPrevious()) {
            if (!iterator.previous().stream().allMatch(Objects::isNull)) break;
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

    public boolean loadRecipeIntoCache(ICustomRecipe recipe, GuiHandler<?> guiHandler) {
        TestCache cache = (TestCache) guiHandler.getCustomCache();
        if (cache.getRecipeType().equals(recipe.getRecipeType())) {
            cache.setCustomRecipe(recipe.clone());
            return true;
        }
        return false;
    }

    public Categories getCategories() {
        return categories;
    }
}
