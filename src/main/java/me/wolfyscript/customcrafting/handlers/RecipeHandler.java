package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeHandler {

    public static final File DATA_FOLDER = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "data");
    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final List<Recipe> allRecipes = new ArrayList<>();

    private final Map<NamespacedKey, ICustomRecipe<?>> customRecipes = new TreeMap<>();

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

    public void load() throws IOException {
        if (CustomCrafting.hasDataBaseHandler()) {
            loadDataBase();
        } else {
            loadConfigs();
        }
    }

    private void loadConfigs() throws IOException {
        if (!customCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            disabledRecipes.addAll(customCrafting.getConfigHandler().getConfig().getDisabledRecipes());
        }
        if (!DATA_FOLDER.exists()) { //Check for the old recipes folder and rename it to the new data folder.
            File old = new File(customCrafting.getDataFolder() + File.separator + "recipes");
            if (!old.renameTo(DATA_FOLDER)) {
                customCrafting.getLogger().severe("Couldn't rename folder to the new required names!");
            }
        }
        String[] dirs = DATA_FOLDER.list();
        if (dirs != null) {
            chat.sendConsoleMessage("$msg.startup.recipes.items$");
            for (String dir : dirs) {
                loadItems(dir);
            }
            chat.sendConsoleMessage("");
            chat.sendConsoleMessage("$msg.startup.recipes.recipes$");
            for (String dir : dirs) {
                chat.sendConsoleMessage("- " + dir);
                for (RecipeType<? extends ICustomRecipe<?>> type : Types.values()) {
                    loadRecipe(dir, type);
                }
            }
            chat.sendConsoleMessage("");
            chat.sendConsoleMessage("$msg.startup.recipes.particles$");
            /*
            for (String dir : dirs) {
                chat.sendConsoleMessage("- " + dir);
                loadConfig(dir, "particles");
            }

             */
        }
    }

    private File[] getFiles(String subFolder, String type) {
        File data = new File(DATA_FOLDER, subFolder + File.separator + type);
        if (!data.exists()) return new File[0];
        return data.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
    }

    private void loadItems(String subFolder) throws IOException {
        chat.sendConsoleMessage("- " + subFolder);
        for (File file : getFiles(subFolder, "items")) {
            String name = file.getName();
            Registry.CUSTOM_ITEMS.register(new NamespacedKey(subFolder, name.substring(0, name.lastIndexOf("."))), objectMapper.readValue(file, CustomItem.class));
        }
    }

    private void loadRecipe(String subFolder, RecipeType<? extends ICustomRecipe<?>> type) throws IOException {
        for (File file : getFiles(subFolder, type.getType().toString().toLowerCase(Locale.ROOT))) {
            String name = file.getName();
            registerRecipe(type.getInstance(new NamespacedKey(subFolder, name.substring(0, name.lastIndexOf("."))), objectMapper.readTree(file)));
        }
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

    public void registerRecipe(ICustomRecipe<?> recipe) {
        if (recipe instanceof ICustomVanillaRecipe) {
            chat.sendDebugMessage("  - add to Bukkit");
            Bukkit.addRecipe(((ICustomVanillaRecipe<?>) recipe).getVanillaRecipe());
        }
        chat.sendDebugMessage("  - cache custom recipe");
        customRecipes.put(recipe.getNamespacedKey(), recipe);
    }

    public void injectRecipe(ICustomRecipe<?> recipe) {
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

    public void unregisterRecipe(ICustomRecipe<?> customRecipe) {
        customRecipes.remove(customRecipe.getNamespacedKey());
        if (customRecipe instanceof ICustomVanillaRecipe) {
            unregisterVanillaRecipe(customRecipe.getNamespacedKey());
        }
    }

    /*
        Get all the Recipes from this group
     */
    public List<ICustomRecipe<?>> getRecipeGroup(String group) {
        return customRecipes.values().parallelStream().filter(r -> r.getGroup().equals(group)).collect(Collectors.toList());
    }

    public List<String> getNamespaces() {
        return customRecipes.keySet().parallelStream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
    }

    public List<ICustomRecipe<?>> getRecipesByNamespace(String namespace) {
        return customRecipes.entrySet().parallelStream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Stream<CraftingRecipe<?>> getSimilarRecipesStream(List<List<ItemStack>> items, boolean elite, boolean advanced) {
        int size = 0;
        for (List<ItemStack> stacks : items) {
            size += stacks.parallelStream().filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count();
        }
        List<CraftingRecipe<?>> craftingRecipes = new ArrayList<>();
        if (elite) craftingRecipes.addAll(getRecipes(Types.ELITE_WORKBENCH));
        if (advanced) craftingRecipes.addAll(getRecipes(Types.WORKBENCH));
        final int totalSize = size;
        final int itemsSize = items.size();
        final int items0Size = itemsSize > 0 ? items.get(0).size() : 0;
        return craftingRecipes.parallelStream().filter(r -> r.getIngredients().keySet().size() == totalSize).filter(recipe -> {
            if (recipe instanceof IShapedCraftingRecipe) {
                IShapedCraftingRecipe shapedRecipe = ((IShapedCraftingRecipe) recipe);
                return itemsSize > 0 && shapedRecipe.getShape().length > 0 && itemsSize == shapedRecipe.getShape().length && items0Size == shapedRecipe.getShape()[0].length();
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

    /**
     * This method returns all the recipes that are cached.
     *
     * @param result
     * @return Recipes without the indicated Type
     */
    public List<ICustomRecipe<?>> getRecipes(CustomItem result) {
        return customRecipes.values().parallelStream().filter(recipe -> recipe.getResults().contains(result)).collect(Collectors.toList());
    }

    //CRAFTING RECIPES

    public AdvancedCraftingRecipe getAdvancedCraftingRecipe(NamespacedKey recipeKey) {
        ICustomRecipe<?> customRecipe = getRecipe(recipeKey);
        return customRecipe instanceof AdvancedCraftingRecipe ? (AdvancedCraftingRecipe) customRecipe : null;
    }

    public <T extends ICustomRecipe<?>> List<T> getRecipes(Class<T> type) {
        return customRecipes.values().parallelStream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public <T extends ICustomRecipe<?>> List<T> getRecipes(RecipeType<T> type) {
        return getRecipes(type.getClazz());
    }

    public Map<NamespacedKey, ICustomRecipe<?>> getRecipes() {
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
        return getAvailable(getAvailableRecipes(), player);
    }

    /**
     * The same as {@link #getAvailableRecipes(Player)}, but only includes the recipes that contain the CustomItem in the result List.
     *
     * @param result
     * @param player
     * @return
     */
    public List<ICustomRecipe<?>> getAvailableRecipes(CustomItem result, Player player) {
        return getAvailableRecipes(player).parallelStream().filter(recipe -> recipe.getResults().contains(result)).collect(Collectors.toList());
    }

    /**
     * The same as {@link #getAvailableRecipes(Player)}, but only includes the recipes that contain the similar ItemStack in the result List.
     *
     * @param result
     * @param player
     * @return
     */
    public List<ICustomRecipe<?>> getAvailableRecipesBySimilarResult(ItemStack result, Player player) {
        return getAvailableRecipes(player).parallelStream().filter(recipe -> recipe.getResults().stream().anyMatch(customItem -> customItem.create().isSimilar(result))).collect(Collectors.toList());
    }

    /**
     * @param type
     * @param <T>
     * @return
     */
    public <T extends ICustomRecipe<?>> List<T> getAvailableRecipes(RecipeType<T> type) {
        List<T> recipes = getRecipes(type.getClazz());
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
    public <T extends ICustomRecipe<?>> List<T> getAvailableRecipes(RecipeType<T> type, Player player) {
        return getAvailable(getAvailableRecipes(type), player);
    }

    private <T extends ICustomRecipe<?>> List<T> getAvailable(List<T> recipes, Player player){
        if (player != null) {
            recipes.removeIf(recipe -> recipe.getConditions().getByID("permission") != null && !recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null)));
        }
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        return recipes;
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
                if (recipe instanceof ComplexRecipe || recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof CookingRecipe || (WolfyUtilities.hasNetherUpdate() && recipe instanceof SmithingRecipe)) {
                    if (((Keyed) recipe).getKey().getNamespace().equals("minecraft")) {
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
        if (items.parallelStream().anyMatch(item -> item.get(column) != null)) return true;
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
