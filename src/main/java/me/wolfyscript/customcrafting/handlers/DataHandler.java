package me.wolfyscript.customcrafting.handlers;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class DataHandler {

    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");
    private final CustomCrafting customCrafting;
    private Categories categories;
    private final Set<NamespacedKey> disabledRecipes = new HashSet<>();
    private List<Recipe> minecraftRecipes = new ArrayList<>();


    private final MainConfig mainConfig;
    private final WolfyUtilities api;
    private final ObjectMapper objectMapper;

    public DataHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.mainConfig = customCrafting.getConfigHandler().getConfig();
        this.customCrafting = customCrafting;
        initCategories();
        this.objectMapper = JacksonUtil.getObjectMapper();
    }

    public void initCategories() {
        this.categories = customCrafting.getConfigHandler().getRecipeBookConfig().getCategories();
    }

    public void load(boolean update) {
        api.getConsole().info("$msg.startup.recipes.title$");
        if (CustomCrafting.inst().hasDataBaseHandler()) {
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
                api.getConsole().info("[ Converting Items & Recipes to the latest Bukkit and Config format ]");
                saveData();
                api.getConsole().info("Loading Items & Recipes from updated configs...");
                load(false);
                api.getConsole().info("[ Conversion of Item & Recipes complete! ]");
                mainConfig.set("data.version", CustomCrafting.CONFIG_VERSION);
                mainConfig.set("data.bukkit_version", CustomCrafting.BUKKIT_VERSION);
                mainConfig.reload();
            }
        }
    }

    public void loadRecipesAndItems() {
        if (!customCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            getDisabledRecipes().addAll(customCrafting.getConfigHandler().getConfig().getDisabledRecipes().parallelStream().map(NamespacedKey::of).collect(Collectors.toList()));
        }
        load(true);
        categories.indexCategories();
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    private void loadDataBase() {
        DataBaseHandler dataBaseHandler = CustomCrafting.inst().getDataBaseHandler();
        api.getConsole().info("- - - - [Database Storage] - - - -");
        dataBaseHandler.loadItems();
        api.getConsole().info("");
        dataBaseHandler.loadRecipes();
    }

    private void loadConfigs() {
        api.getConsole().info("- - - - [Local Storage] - - - -");
        String[] dirs = DATA_FOLDER.list();
        if (dirs != null) {
            for (String dir : dirs) {
                api.getConsole().info("> " + dir);
                loadItems(dir);
            }
            for (String dir : dirs) {
                for (RecipeType<? extends ICustomRecipe<?, ?>> type : Types.values()) {
                    loadRecipe(dir, type);
                }
            }
            api.getConsole().info("");
        }
    }

    public void saveData() {
        api.getConsole().info("Saving Items & Recipes");
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.entrySet().forEach(entry -> ItemLoader.saveItem(entry.getKey(), entry.getValue()));
        Registry.RECIPES.values().forEach(ICustomRecipe::save);
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
                me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.register(namespacedKey, objectMapper.readValue(file, CustomItem.class));
            } catch (IOException e) {
                customCrafting.getLogger().severe(String.format("Could not load item '%s': %s", namespacedKey, e.getMessage()));
            }
        }
    }

    private void loadRecipe(String subFolder, RecipeType<? extends ICustomRecipe<?, ?>> type) {
        for (File file : getFiles(subFolder, type.getType().toString().toLowerCase(Locale.ROOT))) {
            String name = file.getName();
            NamespacedKey namespacedKey = new NamespacedKey(subFolder, name.substring(0, name.lastIndexOf(".")));
            try {
                Registry.RECIPES.register(type.getInstance(namespacedKey, objectMapper.readTree(file)));
            } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                customCrafting.getLogger().severe(String.format("Could not load recipe '%s': %s", namespacedKey, e.getMessage()));
            }
        }
    }

    public void onSave() {
        customCrafting.getConfigHandler().getConfig().setDisabledRecipes(disabledRecipes);
        customCrafting.getConfigHandler().getConfig().save();
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
        return getMinecraftRecipes().stream().filter(recipe -> recipe instanceof Keyed).map(recipe -> NamespacedKey.fromBukkit(((Keyed) recipe).getKey()).toString()).collect(Collectors.toList());
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
            default:
                return (int) Math.sqrt(ingredients.length);
        }
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
            isColumnOccupied(items, 0);
            isColumnOccupied(items, items.get(0).size());
        }
        return items;
    }

    private void isColumnOccupied(List<List<ItemStack>> items, int column) {
        int columnToCheck = Math.max(0, --column);
        if (!items.isEmpty() && columnToCheck < items.get(0).size() && items.parallelStream().allMatch(item -> ItemUtils.isAirOrNull(item.get(columnToCheck)))) {
            items.forEach(item -> item.remove(columnToCheck));
            isColumnOccupied(items, columnToCheck);
        }
    }

    public boolean loadRecipeIntoCache(ICustomRecipe<?, ?> recipe, GuiHandler<CCCache> guiHandler) {
        if (guiHandler.getCustomCache().getRecipeType().equals(recipe.getRecipeType())) {
            ICustomRecipe<?, ?> recipeCopy = recipe.clone();
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
