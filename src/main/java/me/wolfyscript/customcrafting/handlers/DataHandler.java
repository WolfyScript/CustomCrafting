package me.wolfyscript.customcrafting.handlers;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHandler {

    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");
    private final CustomCrafting customCrafting;
    private Categories categories;
    private List<Recipe> minecraftRecipes = new ArrayList<>();

    private final MainConfig config;
    private final WolfyUtilities api;
    private final DatabaseLoader databaseLoader;
    private final LocalStorageLoader localStorageLoader;
    private final ExtensionPackLoader extensionPackLoader;
    private final List<ResourceLoader> loaders = new ArrayList<>();
    private SaveDestination saveDestination = SaveDestination.LOCAL;

    public DataHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.config = customCrafting.getConfigHandler().getConfig();
        this.customCrafting = customCrafting;
        initCategories();

        if (customCrafting.getConfigHandler().getConfig().isDatabaseEnabled()) {
            //Currently, there is only support for SQL. MongoDB is planned!
            this.databaseLoader = new SQLDatabaseLoader(customCrafting);
            this.databaseLoader.setPriority(2);
            if (config.isLocalStorageEnabled()) {
                this.localStorageLoader = new LocalStorageLoader(customCrafting);
                this.localStorageLoader.setPriority(config.isLocalStorageBeforeDatabase() ? 3 : 1);
            } else {
                this.localStorageLoader = null;
            }
        } else {
            this.databaseLoader = null;
            this.localStorageLoader = new LocalStorageLoader(customCrafting);
        }
        this.extensionPackLoader = null; //No extension pack implementation yet. TODO

        initLoaders();
    }

    private void initLoaders() {
        loaders.add(localStorageLoader);
        if (databaseLoader != null) {
            loaders.add(databaseLoader);
        }
        if (extensionPackLoader != null) {
            loaders.add(extensionPackLoader);
        }
        loaders.sort(Comparator.comparingInt(ResourceLoader::getPriority));
    }

    public void initCategories() {
        this.categories = customCrafting.getConfigHandler().getRecipeBookConfig().getCategories();
    }

    public DatabaseLoader getDatabaseLoader() {
        return databaseLoader;
    }

    public LocalStorageLoader getLocalStorageLoader() {
        return localStorageLoader;
    }

    public SaveDestination getSaveDestination() {
        return saveDestination;
    }

    public void setSaveDestination(SaveDestination saveDestination) {
        this.saveDestination = saveDestination;
    }

    public void load(boolean update) {
        api.getConsole().info("$msg.startup.recipes.title$");
        for (ResourceLoader loader : loaders) {
            loader.load();
        }
        if (update) {
            var lastBukkitVersion = config.getInt("data.bukkit_version");
            var lastVersion = config.getInt("data.version");
            if (lastBukkitVersion < CustomCrafting.BUKKIT_VERSION || lastVersion < CustomCrafting.CONFIG_VERSION) {
                api.getConsole().info("[ Converting Items & Recipes to the latest Bukkit and Config format ]");
                saveData();
                api.getConsole().info("Loading Items & Recipes from updated configs...");
                load(false);
                api.getConsole().info("[ Conversion of Item & Recipes complete! ]");
                config.set("data.version", CustomCrafting.CONFIG_VERSION);
                config.set("data.bukkit_version", CustomCrafting.BUKKIT_VERSION);
                config.reload();
            }
        }
    }

    public void loadRecipesAndItems() {
        load(true);
        categories.index();
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    public void saveData() {
        api.getConsole().info("Saving Items & Recipes");
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.entrySet().forEach(entry -> ItemLoader.saveItem(entry.getKey(), entry.getValue()));
        CCRegistry.RECIPES.values().forEach(ICustomRecipe::save);
    }

    public ResourceLoader getActiveLoader() {
        if (saveDestination == SaveDestination.LOCAL) {
            return localStorageLoader;
        } else {
            return databaseLoader;
        }
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

    public Categories getCategories() {
        return categories;
    }
}
