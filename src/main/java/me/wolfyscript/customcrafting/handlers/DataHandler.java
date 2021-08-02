package me.wolfyscript.customcrafting.handlers;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
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
            var lastBukkitVersion = mainConfig.getInt("data.bukkit_version");
            var lastVersion = mainConfig.getInt("data.version");
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
            getDisabledRecipes().addAll(customCrafting.getConfigHandler().getConfig().getDisabledRecipes().parallelStream().map(NamespacedKey::of).toList());
        }
        load(true);
        categories.index();
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    private void loadDataBase() {
        var dataBaseHandler = CustomCrafting.inst().getDataBaseHandler();
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
                for (RecipeType<? extends ICustomRecipe<?>> type : RecipeType.values()) {
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
        var data = new File(DATA_FOLDER, subFolder + File.separator + type);
        if (!data.exists()) return new File[0];
        return data.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
    }

    private void loadItems(String subFolder) {
        for (File file : getFiles(subFolder, "items")) {
            String name = file.getName();
            var namespacedKey = new NamespacedKey(customCrafting, subFolder + "/" + name.substring(0, name.lastIndexOf(".")));
            try {
                me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.register(namespacedKey, objectMapper.readValue(file, CustomItem.class));
            } catch (IOException e) {
                customCrafting.getLogger().severe(String.format("Could not load item '%s':", namespacedKey));
                e.printStackTrace();
                customCrafting.getLogger().severe("----------------------");
            }
        }
    }

    private void loadRecipe(String subFolder, RecipeType<? extends ICustomRecipe<?>> type) {
        for (File file : getFiles(subFolder, type.getType().toString().toLowerCase(Locale.ROOT))) {
            String name = file.getName();
            var namespacedKey = new NamespacedKey(subFolder, name.substring(0, name.lastIndexOf(".")));
            try {
                Registry.RECIPES.register(type.getInstance(namespacedKey, objectMapper.readTree(file)));
            } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                customCrafting.getLogger().severe(String.format("Could not load recipe '%s':", namespacedKey));
                e.printStackTrace();
                customCrafting.getLogger().severe("----------------------");
            }
        }
    }

    public void saveDisabledRecipes() {
        customCrafting.getConfigHandler().getConfig().setDisabledRecipes(disabledRecipes);
        customCrafting.getConfigHandler().getConfig().save();
    }

    /**
     * @return A list of recipes that are disabled.
     */
    public Set<NamespacedKey> getDisabledRecipes() {
        return disabledRecipes;
    }

    /**
     * @param recipe The recipe to check.
     * @return if the recipe is disabled.
     * @deprecated Replaced by {@link ICustomRecipe#isDisabled()}
     */
    @Deprecated
    public boolean isRecipeDisabled(ICustomRecipe<?> recipe) {
        return recipe.isDisabled();
    }

    public void toggleRecipe(ICustomRecipe<?> recipe) {
        if (recipe.isDisabled()) {
            enableRecipe(recipe);
        } else {
            disableRecipe(recipe);
        }
    }

    public void disableRecipe(ICustomRecipe<?> recipe) {
        var namespacedKey = recipe.getNamespacedKey();
        disabledRecipes.add(namespacedKey);
        if (recipe instanceof ICustomVanillaRecipe<?>) {
            disableBukkitRecipe(namespacedKey.toBukkit(customCrafting));
        }
    }

    public void enableRecipe(ICustomRecipe<?> recipe) {
        var namespacedKey = recipe.getNamespacedKey();
        if (recipe instanceof ICustomVanillaRecipe) {
            enableBukkitRecipe(namespacedKey.toBukkit(customCrafting));
        }
        disabledRecipes.remove(namespacedKey);
    }

    public boolean isBukkitRecipeDisabled(org.bukkit.NamespacedKey namespacedKey) {
        return disabledRecipes.contains(NamespacedKey.fromBukkit(namespacedKey));
    }

    public void toggleBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        if (isBukkitRecipeDisabled(namespacedKey)) {
            enableBukkitRecipe(namespacedKey);
        } else {
            disableBukkitRecipe(namespacedKey);
        }
    }

    public void disableBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        disabledRecipes.add(NamespacedKey.fromBukkit(namespacedKey));
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.undiscoverRecipe(namespacedKey);
        }
    }

    public void enableBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        disabledRecipes.remove(NamespacedKey.fromBukkit(namespacedKey));
    }

    public List<Recipe> getMinecraftRecipes() {
        if (minecraftRecipes.isEmpty()) {
            minecraftRecipes = Streams.stream(Bukkit.recipeIterator()).filter(recipe -> {
                if (recipe instanceof ComplexRecipe || recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof CookingRecipe || recipe instanceof SmithingRecipe) {
                    return ((Keyed) recipe).getKey().getNamespace().equals("minecraft");
                }
                return false;
            }).sorted(Comparator.comparing(recipe -> ((Keyed) recipe).getKey().toString())).toList();
        }
        return Collections.unmodifiableList(minecraftRecipes);
    }

    public List<String> getBukkitNamespacedKeys() {
        return getMinecraftRecipes().stream().filter(Keyed.class::isInstance).map(recipe -> NamespacedKey.fromBukkit(((Keyed) recipe).getKey()).toString()).toList();
    }

    @Deprecated
    private int gridSize(ItemStack[] ingredients) {
        return switch (ingredients.length) {
            case 9 -> 3;
            case 16 -> 4;
            case 25 -> 5;
            case 36 -> 6;
            default -> (int) Math.sqrt(ingredients.length);
        };
    }

    @Deprecated
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
        var leftPos = gridSize;
        var rightPos = 0;
        for (List<ItemStack> itemsY : items) {
            var size = itemsY.size();
            for (int i = 0; i < size; i++) {
                if (itemsY.get(i) != null) {
                    leftPos = Math.min(leftPos, i);
                    break;
                }
            }
            if (leftPos == 0) break;
        }
        for (List<ItemStack> itemsY : items) {
            var size = itemsY.size();
            for (int i = size - 1; i > 0; i--) {
                if (itemsY.get(i) != null) {
                    rightPos = Math.max(rightPos, i);
                    break;
                }
            }
            if (rightPos == gridSize) break;
        }
        var finalLeftPos = leftPos;
        var finalRightPos = rightPos + 1;
        return items.stream().map(itemStacks -> itemStacks.subList(finalLeftPos, finalRightPos)).collect(Collectors.toList());
    }

    /**
     * Loads a recipe copy into the {@link CCCache} of the {@link GuiHandler}.
     *
     * @param recipe     The recipe to load.
     * @param guiHandler The {@link GuiHandler} to load into.
     * @return If the recipe was successfully loaded into cache.
     */
    public boolean loadRecipeIntoCache(ICustomRecipe<?> recipe, GuiHandler<CCCache> guiHandler) {
        if (guiHandler.getCustomCache().getRecipeType().isInstance(recipe)) {
            ICustomRecipe<?> recipeCopy = recipe.clone();
            recipeCopy.setNamespacedKey(recipe.getNamespacedKey());
            if (recipeCopy instanceof CraftingRecipe<?, ?> craftingRecipe) {
                if (RecipeType.WORKBENCH.isInstance(craftingRecipe)) {
                    guiHandler.getCustomCache().setCustomRecipe(RecipeType.WORKBENCH, RecipeType.WORKBENCH.cast(craftingRecipe));
                } else if (RecipeType.ELITE_WORKBENCH.isInstance(craftingRecipe)) {
                    guiHandler.getCustomCache().setCustomRecipe(RecipeType.ELITE_WORKBENCH, RecipeType.ELITE_WORKBENCH.cast(craftingRecipe));
                }
            } else {
                guiHandler.getCustomCache().setCustomRecipe(recipeCopy);
            }
            return true;
        }
        return false;
    }

    public Categories getCategories() {
        return categories;
    }
}
