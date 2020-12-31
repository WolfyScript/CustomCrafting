package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.*;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeHandler {

    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final List<Recipe> allRecipes = new ArrayList<>();

    private final TreeMap<NamespacedKey, ICustomRecipe<?>> customRecipes = new TreeMap<>();

    private final ArrayList<String> disabledRecipes = new ArrayList<>();

    private final ConfigAPI configAPI;
    private final WolfyUtilities api;
    private final Chat chat;
    private final ObjectMapper objectMapper;

    public RecipeHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.chat = api.getChat();
        this.configAPI = api.getConfigAPI();
        this.customCrafting = customCrafting;
        this.categories = customCrafting.getConfigHandler().getRecipeBook().getCategories();
        this.objectMapper = JacksonUtil.getObjectMapper();
    }

    public void load() {
        if (CustomCrafting.hasDataBaseHandler()) {
            loadDataBase();
        } else {
            loadConfigs();
        }
    }

    private void loadConfigs() {
        if (!customCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            disabledRecipes.addAll(customCrafting.getConfigHandler().getConfig().getDisabledRecipes());
        }
        chat.sendConsoleMessage("$msg.startup.recipes.title$");
        File recipesFolder = new File(customCrafting.getDataFolder() + File.separator + "recipes");
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            chat.sendConsoleMessage("");
            chat.sendConsoleMessage("$msg.startup.recipes.items$");
            for (File dir : dirs) {
                chat.sendConsoleMessage("- " + dir.getName());
                loadConfig(dir.getName(), "items");
            }
            chat.sendConsoleMessage("");
            chat.sendConsoleMessage("$msg.startup.recipes.recipes$");
            for (File dir : dirs) {
                chat.sendConsoleMessage("- " + dir.getName());
                loadConfig(dir.getName(), "workbench");
                loadConfig(dir.getName(), "furnace");
                loadConfig(dir.getName(), "anvil");
                loadConfig(dir.getName(), "cauldron");
                loadConfig(dir.getName(), "blast_furnace");
                loadConfig(dir.getName(), "smoker");
                loadConfig(dir.getName(), "campfire");
                loadConfig(dir.getName(), "stonecutter");
                loadConfig(dir.getName(), "grindstone");
                loadConfig(dir.getName(), "brewing_stand");
                loadConfig(dir.getName(), "elite_workbench");
                loadConfig(dir.getName(), "smithing");
            }
            chat.sendConsoleMessage("");
            chat.sendConsoleMessage("$msg.startup.recipes.particles$");
            for (File dir : dirs) {
                chat.sendConsoleMessage("- " + dir.getName());
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
                                Registry.CUSTOM_ITEMS.register(namespacedKey, objectMapper.convertValue(node, CustomItem.class));
                                break;
                            case "particles":
                                //TODO: Load particles
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
                            case "brewing_stand":
                                registerRecipe(new BrewingRecipe(namespacedKey, node));
                                break;
                            case "smithing":
                                registerRecipe(new CustomSmithingRecipe(namespacedKey, node));
                        }
                    } catch (Exception ex) {
                        ChatUtils.sendRecipeItemLoadingError(subfolder, name, type, ex);
                    }
                } else {
                    api.getChat().sendConsoleMessage("$msg.startup.recipes.incompatible$", new String[]{"%namespace%", subfolder}, new String[]{"%key%", key}, new String[]{"%file_type%", fileType});
                }
            }
            return true;
        });
    }

    public void onSave() {
        customCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        customCrafting.getConfigHandler().getConfig().save();
    }

    private void loadDataBase() {
        DataBaseHandler dataBaseHandler = CustomCrafting.getDataBaseHandler();
        try {
            chat.sendConsoleMessage("$msg.startup.recipes.title$");
            dataBaseHandler.loadItems();
            dataBaseHandler.loadRecipes(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void migrateConfigsToDB(DataBaseHandler dataBaseHandler) {
        chat.sendConsoleMessage("Exporting configs to database...");
        getRecipes().values().forEach(dataBaseHandler::updateRecipe);
        chat.sendConsoleMessage("Exported configs to database successfully.");
    }

    public void registerRecipe(ICustomRecipe recipe) {
        if (recipe instanceof ICustomVanillaRecipe) {
            chat.sendDebugMessage("  - add to Bukkit");
            Bukkit.addRecipe(((ICustomVanillaRecipe<?>) recipe).getVanillaRecipe());
        }
        chat.sendDebugMessage("  - cache custom recipe");
        customRecipes.put(recipe.getNamespacedKey(), recipe);
    }

    public void injectRecipe(ICustomRecipe recipe) {
        chat.sendDebugMessage("[Inject Recipe]");
        chat.sendDebugMessage("  - unregister old recipe");
        unregisterRecipe(recipe);
        registerRecipe(recipe);
        chat.sendDebugMessage("[- - Done - -]");
    }

    public void unregisterVanillaRecipe(NamespacedKey namespacedKey) {
        if (WolfyUtilities.hasBuzzyBeesUpdate()) {
            chat.sendDebugMessage("      -> using new API method");
            Bukkit.removeRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()));
        } else {
            chat.sendDebugMessage("      -> using old method");
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
    public List<ICustomRecipe<?>> getRecipeGroup(String group) {
        return customRecipes.values().stream().filter(r -> r.getGroup().equals(group)).collect(Collectors.toList());
    }

    public List<String> getNamespaces() {
        return customRecipes.keySet().stream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
    }

    public List<ICustomRecipe<?>> getRecipesByNamespace(String namespace) {
        return customRecipes.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Stream<CraftingRecipe<?>> getSimilarRecipesStream(List<List<ItemStack>> items, boolean elite, boolean advanced) {
        AtomicInteger size = new AtomicInteger();
        items.forEach(stacks -> size.addAndGet((int) stacks.stream().filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count()));
        List<CraftingRecipe<?>> craftingRecipes = new ArrayList<>();
        if (elite) craftingRecipes.addAll(getRecipes(RecipeType.ELITE_WORKBENCH));
        if (advanced) craftingRecipes.addAll(getRecipes(RecipeType.WORKBENCH));
        return craftingRecipes.stream().filter(r -> r.getIngredients().keySet().size() == size.get()).filter(customRecipe -> {
            if (customRecipe instanceof IShapedCraftingRecipe) {
                IShapedCraftingRecipe recipe = ((IShapedCraftingRecipe) customRecipe);
                return items.size() > 0 && recipe.getShape().length > 0 && items.size() == recipe.getShape().length && items.get(0).size() == recipe.getShape()[0].length();
            }
            return true;
        });
    }

    public List<CraftingRecipe<?>> getSimilarRecipes(List<List<ItemStack>> items, boolean elite, boolean advanced) {
        return getSimilarRecipesStream(items, elite, advanced).collect(Collectors.toList());
    }

    public ICustomRecipe<?> getRecipe(NamespacedKey namespacedKey) {
        return customRecipes.get(namespacedKey);
    }

    @Deprecated
    public ICustomRecipe<?> getRecipe(String key) {
        return customRecipes.get(new NamespacedKey(key.split(":")[0], key.split(":")[1]));
    }

    public List<ICustomRecipe<?>> getRecipes(String type) {
        return new ArrayList<>(getRecipes(RecipeType.valueOf(type)));
    }

    /**
     * This method returns all the recipes that are cached.
     *
     * @param result
     * @return Recipes without the indicated Type
     */
    public List<ICustomRecipe<?>> getRecipes(CustomItem result) {
        return customRecipes.values().stream().filter(recipe -> recipe.getResults().contains(result)).collect(Collectors.toList());
    }

    //CRAFTING RECIPES

    public AdvancedCraftingRecipe getAdvancedCraftingRecipe(String key) {
        ICustomRecipe<?> customRecipe = getRecipe(key);
        return customRecipe instanceof AdvancedCraftingRecipe ? (AdvancedCraftingRecipe) customRecipe : null;
    }

    public <T extends ICustomRecipe<?>> List<T> getRecipes(Class<T> type) {
        return customRecipes.values().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public <T extends ICustomRecipe<?>> List<T> getRecipes(RecipeType<T> type) {
        return getRecipes(type.getClazz());
    }

    public TreeMap<NamespacedKey, ICustomRecipe<?>> getRecipes() {
        return customRecipes;
    }


    /*
        Get the available recipes only.
        Disabled and hidden recipes are removed!
        For the crafting recipes you also need permissions to view them.
         */


    /**
     * Get all the recipes that are available.
     * Recipes that are hidden or disabled are not included.
     *
     * @return
     */
    public List<ICustomRecipe<?>> getAvailableRecipes() {
        List<ICustomRecipe<?>> recipes = new ArrayList<>(getRecipes().values());
        recipes.removeIf(recipe -> recipe.isHidden() || customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString()));
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return recipes;
    }

    /**
     * Similar to {@link #getAvailableRecipes()} only includes the visible and enabled recipes, but also takes the player into account.
     * Recipes that the player has no permission to view are not included.
     *
     * @param player
     * @return
     */
    public List<ICustomRecipe<?>> getAvailableRecipes(Player player) {
        List<ICustomRecipe<?>> recipes = getAvailableRecipes();
        if (player != null) {
            recipes.removeIf(recipe -> recipe.getConditions().getByID("permission") != null && !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        }
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return recipes;
    }

    /**
     * The same as {@link #getAvailableRecipes(Player)}, but only includes the recipes that contain the CustomItem in the result List.
     *
     * @param result
     * @param player
     * @return
     */
    public List<ICustomRecipe<?>> getAvailableRecipes(CustomItem result, Player player) {
        return getAvailableRecipes(player).stream().filter(recipe -> recipe.getResults().contains(result)).collect(Collectors.toList());
    }

    /**
     * The same as {@link #getAvailableRecipes(Player)}, but only includes the recipes that contain the similar ItemStack in the result List.
     *
     * @param result
     * @param player
     * @return
     */
    public List<ICustomRecipe<?>> getAvailableRecipesBySimilarResult(ItemStack result, Player player) {
        return getAvailableRecipes(player).stream().filter(recipe -> recipe.getResults().stream().anyMatch(customItem -> customItem.create().isSimilar(result))).collect(Collectors.toList());
    }

    /**
     * @param type
     * @param <T>
     * @return
     */
    public <T extends ICustomRecipe<?>> List<T> getAvailableRecipes(Class<T> type) {
        List<T> recipes = getRecipes(type);
        recipes.removeIf(recipe -> recipe.isHidden() || customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString()));
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return new ArrayList<>(recipes);
    }

    /**
     * @param type
     * @param player
     * @param <T>
     * @return
     */
    public <T extends ICustomRecipe<?>> List<T> getAvailableRecipes(Class<T> type, Player player) {
        List<T> recipes = getAvailableRecipes(type);
        if (player != null) {
            recipes.removeIf(recipe -> recipe.getConditions().getByID("permission") != null && !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        }
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return recipes;
    }

    /**
     * @param type
     * @param <T>
     * @return
     */
    public <T extends ICustomRecipe<?>> List<T> getAvailableRecipes(RecipeType<T> type) {
        return getAvailableRecipes(type.getClazz());
    }

    /**
     * @param type
     * @param player
     * @param <T>
     * @return
     */
    public <T extends ICustomRecipe<?>> List<T> getAvailableRecipes(RecipeType<T> type, Player player) {
        return getAvailableRecipes(type.getClazz(), player);
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
                if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof CookingRecipe || (WolfyUtilities.hasNetherUpdate() && recipe instanceof SmithingRecipe)) {
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

    public boolean loadRecipeIntoCache(ICustomRecipe<?> recipe, GuiHandler<?> guiHandler) {
        CCCache cache = (CCCache) guiHandler.getCustomCache();
        if (cache.getRecipeType().equals(recipe.getRecipeType())) {
            ICustomRecipe<?> recipeCopy = recipe.clone();
            recipeCopy.setNamespacedKey(recipe.getNamespacedKey());
            cache.setCustomRecipe(recipeCopy);
            return true;
        }
        return false;
    }

    public Categories getCategories() {
        return categories;
    }
}
