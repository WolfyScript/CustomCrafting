package me.wolfyscript.customcrafting.handlers;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DataHandler {

    public static final File DATA_FOLDER = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "data");
    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final Set<NamespacedKey> disabledRecipes = new HashSet<>();
    private List<Recipe> minecraftRecipes = new ArrayList<>();

    private final Map<Category, Map<Category, List<ICustomRecipe<?,?>>>> indexedCategoryRecipes;

    private final MainConfig mainConfig;
    private final WolfyUtilities api;
    private final Chat chat;
    private final ObjectMapper objectMapper;

    public DataHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.chat = api.getChat();
        this.mainConfig = customCrafting.getConfigHandler().getConfig();
        this.customCrafting = customCrafting;
        this.categories = customCrafting.getConfigHandler().getRecipeBook().getCategories();
        this.objectMapper = JacksonUtil.getObjectMapper();
        this.indexedCategoryRecipes = new HashMap<>();
    }

    public void load(boolean update) {
        chat.sendConsoleMessage("$msg.startup.recipes.title$");
        if (CustomCrafting.hasDataBaseHandler()) {
            if (mainConfig.isLocalStorageEnabled()) {
                if (mainConfig.isLocalStorageBeforeDatabase()) {
                    loadConfigs();
                    loadDataBase();
                } else {
                    loadDataBase();
                    loadConfigs();
                }
            } else {
                loadDataBase();
            }
        } else {
            loadConfigs();
        }
        if (update) {
            int lastBukkitVersion = mainConfig.getInt("data.bukkit_version");
            int lastVersion = mainConfig.getInt("data.version");
            if (lastBukkitVersion < CustomCrafting.BUKKIT_VERSION || lastVersion < CustomCrafting.CONFIG_VERSION) {
                chat.sendConsoleMessage("[ Converting Items & Recipes to the latest Bukkit and Config format ]");
                saveData();
                chat.sendConsoleMessage("Loading Items & Recipes from updated configs...");
                load(false);
                chat.sendConsoleMessage("[ Conversion of Item & Recipes complete! ]");
                mainConfig.set("data.version", CustomCrafting.CONFIG_VERSION);
                mainConfig.set("data.bukkit_version", CustomCrafting.BUKKIT_VERSION);
                mainConfig.reload();
            }
        }
    }

    private void loadDataBase() {
        DataBaseHandler dataBaseHandler = CustomCrafting.getDataBaseHandler();
        chat.sendConsoleMessage("- - - - [Database Storage] - - - -");
        try {
            dataBaseHandler.loadItems();
            chat.sendConsoleMessage("");
            dataBaseHandler.loadRecipes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadConfigs() {
        chat.sendConsoleMessage("- - - - [Local Storage] - - - -");
        String[] dirs = DATA_FOLDER.list();
        if (dirs != null) {
            for (String dir : dirs) {
                chat.sendConsoleMessage("> " + dir);
                loadItems(dir);
            }
            for (String dir : dirs) {
                for (RecipeType<? extends ICustomRecipe<?,?>> type : Types.values()) {
                    loadRecipe(dir, type);
                }
            }
            chat.sendConsoleMessage("");
        }
    }

    public void saveData() {
        chat.sendConsoleMessage("Saving Items & Recipes");
        Registry.CUSTOM_ITEMS.entrySet().forEach(entry -> ItemLoader.saveItem(entry.getKey(), entry.getValue()));
        me.wolfyscript.customcrafting.Registry.RECIPES.values().forEach(ICustomRecipe::save);
    }

    private File[] getFiles(String subFolder, String type) {
        File data = new File(DATA_FOLDER, subFolder + File.separator + type);
        if (!data.exists()) return new File[0];
        return data.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
    }

    private void loadItems(String subFolder) {
        for (File file : getFiles(subFolder, "items")) {
            String name = file.getName();
            NamespacedKey namespacedKey = new NamespacedKey(customCrafting, subFolder + "/" + name.substring(0, name.lastIndexOf(".")));
            try {
                Registry.CUSTOM_ITEMS.register(namespacedKey, objectMapper.readValue(file, CustomItem.class));
            } catch (IOException e) {
                customCrafting.getLogger().severe(String.format("Could not load item '%s': %s", namespacedKey.toString(), e.getMessage()));
            }
        }
    }

    private void loadRecipe(String subFolder, RecipeType<? extends ICustomRecipe<?,?>> type) {
        for (File file : getFiles(subFolder, type.getType().toString().toLowerCase(Locale.ROOT))) {
            String name = file.getName();
            NamespacedKey namespacedKey = new NamespacedKey(subFolder, name.substring(0, name.lastIndexOf(".")));
            try {
                Registry.RECIPES.register(type.getInstance(namespacedKey, objectMapper.readTree(file)));
            } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                customCrafting.getLogger().severe(String.format("Could not load recipe '%s': %s", namespacedKey.toString(), e.getMessage()));
            }
        }
    }

    public void onSave() {
        customCrafting.getConfigHandler().getConfig().setDisabledRecipes(disabledRecipes);
        customCrafting.getConfigHandler().getConfig().save();
    }

    public List<ICustomRecipe<?,?>> getIndexedRecipeItems(Player player, Category mainCategory, Category switchCategory) {
        return Registry.RECIPES.getAvailable(indexedCategoryRecipes.getOrDefault(mainCategory, new HashMap<>()).getOrDefault(switchCategory, new ArrayList<>()), player);
    }

    /**
     * Indexes the recipes for all the available categories.
     * <p>
     * If there are already indexed recipes they will be cleared and re-indexed.
     */
    public void indexRecipeItems() {
        chat.sendConsoleMessage("Indexing Recipes for Recipe Book...");
        indexedCategoryRecipes.clear();
        for (Category mainCategory : categories.getMainCategories().values()) {
            Map<Category, List<ICustomRecipe<?,?>>> indexSwitchCategories = new HashMap<>();
            for (Category switchCategory : categories.getSwitchCategories().values()) {
                indexSwitchCategories.put(switchCategory, Registry.RECIPES.getAvailable().parallelStream().filter(recipe -> {
                    if (switchCategory == null) return true;
                    List<CustomItem> items = recipe.getRecipeBookItems();
                    if (mainCategory != null && !mainCategory.isValid(recipe) && items.parallelStream().noneMatch(customItem -> mainCategory.isValid(customItem.getItemStack().getType()))) {
                        return false;
                    }
                    return switchCategory.isValid(recipe) || items.parallelStream().anyMatch(customItem -> switchCategory.isValid(customItem.getItemStack().getType()));
                }).collect(Collectors.toList()));
            }
            indexedCategoryRecipes.put(mainCategory, indexSwitchCategories);
        }
    }

    //DISABLED RECIPES AND GET ALL RECIPES

    public Set<NamespacedKey> getDisabledRecipes() {
        return disabledRecipes;
    }

    public List<Recipe> getMinecraftRecipes() {
        if (minecraftRecipes.isEmpty()) {
            minecraftRecipes = Streams.stream(Bukkit.recipeIterator()).filter(recipe -> {
                if (recipe instanceof ComplexRecipe || recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof CookingRecipe || (ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) && recipe instanceof SmithingRecipe)) {
                    return ((Keyed) recipe).getKey().getNamespace().equals("minecraft");
                }
                return false;
            }).sorted(Comparator.comparing(recipe -> ((Keyed) recipe).getKey().toString())).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(minecraftRecipes);
    }

    public List<String> getBukkitNamespacedKeys() {
        return getMinecraftRecipes().stream().filter(recipe -> recipe instanceof Keyed).map(recipe -> NamespacedKey.of(((Keyed) recipe).getKey()).toString()).collect(Collectors.toList());
    }

    private int gridSize(ItemStack[] ingredients) {
        switch (ingredients.length) {
            case 9:
                return 3;
            case 16:
                return 4;
            case 25:
                return 5;
            case 36:
                return 6;
        }
        return (int) Math.sqrt(ingredients.length);
    }

    public List<List<ItemStack>> getIngredients(ItemStack[] ingredients) {
        List<List<ItemStack>> items = new ArrayList<>();
        int gridSize = gridSize(ingredients);
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
            isColumnOccupied(items, -1);
            isColumnOccupied(items, items.get(0).size());
        }
        return items;
    }

    private void isColumnOccupied(List<List<ItemStack>> items, int column) {
        int columnToCheck = column <= -1 ? 0 : --column;
        if (items.size() > 0 && columnToCheck >= 0 && columnToCheck < items.get(0).size()) {
            if (items.parallelStream().anyMatch(item -> item.get(columnToCheck) != null)) return;
            items.forEach(item -> item.remove(columnToCheck));
            isColumnOccupied(items, columnToCheck);
        }
    }

    public boolean loadRecipeIntoCache(ICustomRecipe<?,?> recipe, GuiHandler<CCCache> guiHandler) {
        if (guiHandler.getCustomCache().getRecipeType().equals(recipe.getRecipeType())) {
            ICustomRecipe<?,?> recipeCopy = recipe.clone();
            recipeCopy.setNamespacedKey(recipe.getNamespacedKey());
            guiHandler.getCustomCache().setCustomRecipe(recipeCopy);
            return true;
        }
        return false;
    }

    public Categories getCategories() {
        return categories;
    }
}
