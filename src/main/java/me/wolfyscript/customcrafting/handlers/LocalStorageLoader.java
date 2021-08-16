package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class LocalStorageLoader extends ResourceLoader {

    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");

    protected LocalStorageLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "local_loader"));
    }

    @Override
    public void load() {
        api.getConsole().info("- - - - [Local Storage] - - - -");
        api.getConsole().info("Searching for namespaces...");
        String[] dirs = DATA_FOLDER.list();
        if (dirs != null) {
            api.getConsole().info("Namespaces: [" + String.join(", ", dirs) + "]");
            api.getConsole().info(" - ");
            api.getConsole().info("Loading items...");
            for (String dir : dirs) {
                loadItems(dir);
            }
            api.getConsole().info("Loading recipes...");
            for (String dir : dirs) {
                for (RecipeType<? extends ICustomRecipe<?>> type : RecipeType.values()) {
                    loadRecipe(dir, type);
                }
            }
            api.getConsole().info("");
        }
    }

    private File getFileAt(String namespace, String typeId, String key) {
        return new File(DataHandler.DATA_FOLDER + File.separator + namespace + File.separator + typeId, key + ".json");
    }

    @Override
    public boolean save(ICustomRecipe<?> recipe) {
        File file = getFileAt(recipe.getNamespacedKey().getNamespace(), recipe.getRecipeType().getId(), recipe.getNamespacedKey().getKey());
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                if (file.isFile() || file.createNewFile()) {
                    JacksonUtil.getObjectWriter(CustomCrafting.inst().getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, recipe);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean save(CustomItem item) {
        if (item.getNamespacedKey() != null) {
            var internalKey = NamespacedKeyUtils.toInternal(item.getNamespacedKey());
            var file = getFileAt(internalKey.getNamespace(), "items", internalKey.getKey());
            if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
                try {
                    if (file.exists() || file.createNewFile()) {
                        JacksonUtil.getObjectWriter(CustomCrafting.inst().getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, item);
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean delete(ICustomRecipe<?> recipe) {
        File file = getFileAt(recipe.getNamespacedKey().getNamespace(), recipe.getRecipeType().getId(), recipe.getNamespacedKey().getKey());
        System.gc();
        if (file.delete()) {
            return true;
        } else {
            file.deleteOnExit();
        }
        return false;
    }

    @Override
    public boolean delete(CustomItem item) {
        System.gc();
        var internalKey = NamespacedKeyUtils.toInternal(item.getNamespacedKey());
        var file = getFileAt(internalKey.getNamespace(), "items", internalKey.getKey());
        if (file.delete()) {
            return true;
        } else {
            file.deleteOnExit();
        }
        return false;
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

    private void loadRecipe(String subFolder, RecipeType<?> type) {
        for (File file : getFiles(subFolder, type.getId())) {
            String name = file.getName();
            if (checkForOldCraftingRecipes(RecipeType.WORKBENCH, subFolder, name) || checkForOldCraftingRecipes(RecipeType.ELITE_WORKBENCH, subFolder, name)) {
                continue;
            }
            var namespacedKey = new NamespacedKey(subFolder, name.substring(0, name.lastIndexOf(".")));
            try {
                CCRegistry.RECIPES.register(type.getInstance(namespacedKey, objectMapper.readTree(file)));
            } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), type.getId(), e);
            }
        }
    }

    private boolean checkForOldCraftingRecipes(RecipeType<? extends CraftingRecipe<?, ?>> type, String namespace, String key) {
        if (type.equals(RecipeType.WORKBENCH)) {
            return hasOldCraftingRecipe(RecipeType.WORKBENCH_SHAPELESS, namespace, key) || hasOldCraftingRecipe(RecipeType.WORKBENCH_SHAPED, namespace, key);
        } else if (type.equals(RecipeType.ELITE_WORKBENCH)) {
            return hasOldCraftingRecipe(RecipeType.ELITE_WORKBENCH_SHAPELESS, namespace, key) || hasOldCraftingRecipe(RecipeType.ELITE_WORKBENCH_SHAPED, namespace, key);
        }
        return false;
    }

    private boolean hasOldCraftingRecipe(RecipeType<?> type, String folder, String name) {
        return new File(DATA_FOLDER, folder + "/" + type.toString().toLowerCase(Locale.ROOT) + "/" + name).isFile();
    }
}
