/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeLoader;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalStorageLoader extends ResourceLoader {

    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");
    private static final String ITEMS_FOLDER = "items";

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
                //Required to load the old recipes.
                loadAndRegisterRecipe(RecipeType.Container.CRAFTING, dir);
                loadAndRegisterRecipe(RecipeType.Container.ELITE_CRAFTING, dir);
                for (RecipeType<? extends CustomRecipe<?>> type : RecipeType.values()) {
                    loadAndRegisterRecipe(type, dir);
                }
            }
            api.getConsole().info("");
        }
    }

    private File getFileAt(String namespace, String typeId, String key) {
        return new File(DataHandler.DATA_FOLDER + File.separator + namespace + File.separator + typeId, key + ".json");
    }

    @Override
    public boolean save(CustomRecipe<?> recipe) {
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
            var file = getFileAt(internalKey.getNamespace(), ITEMS_FOLDER, internalKey.getKey());
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
    public boolean delete(CustomRecipe<?> recipe) {
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
        var file = getFileAt(internalKey.getNamespace(), ITEMS_FOLDER, internalKey.getKey());
        if (file.delete()) {
            return true;
        } else {
            file.deleteOnExit();
        }
        return false;
    }

    private List<File> getFiles(String subFolder, String type) {
        var data = new File(DATA_FOLDER, subFolder + File.separator + type);
        if (!data.exists()) return new ArrayList<>();
        File[] files = data.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
        return files != null ? Arrays.stream(files).collect(Collectors.toList()) : new ArrayList<>();
    }

    private void loadItems(String subFolder) {
        for (File file : getFiles(subFolder, ITEMS_FOLDER)) {
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

    private void loadAndRegisterRecipe(RecipeLoader<?> loader, String namespace) {
        if (loader instanceof RecipeType.Container<?> container && container.hasLegacy()) {
            //Loading legacy recipes
            String legacyId = container.getLegacyID();
            List<File> legacyFiles = getFiles(namespace, legacyId);
            if (!legacyFiles.isEmpty()) { //If there are no legacy recipes we can skip it.
                for (RecipeType<?> type : container.getTypes()) {
                    List<File> latestFiles = getFiles(namespace, type.getId());
                    legacyFiles.removeIf(legacyFile -> latestFiles.stream().anyMatch(file -> file.getName().equals(legacyFile.getName()))); //Remove the files that are present in new recipe folders
                }
                loadRecipesFiles(loader, legacyFiles, namespace);
            }
            return;
        }
        loadRecipesFiles(loader, getFiles(namespace, loader.getId()), namespace);
    }

    private void loadRecipesFiles(RecipeLoader<?> loader, List<File> files, String namespace) {
        for (File file : files) {
            var namespacedKey = new NamespacedKey(namespace, file.getName().replace(".json", ""));
            try {
                CCRegistry.RECIPES.register(loader.getInstance(namespacedKey, objectMapper.readTree(file)));
            } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), loader.getId(), e);
            }
        }
    }

}
