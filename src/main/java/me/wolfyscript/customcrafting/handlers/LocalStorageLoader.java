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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeLoader;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.InjectableValues;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalStorageLoader extends ResourceLoader {

    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");
    private static final String ITEMS_FOLDER = "items";
    private static final String RECIPES_FOLDER = "recipes";

    protected LocalStorageLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "local_loader"));
    }

    @Override
    public void load() {
        /*
        New Folder structure:
        CustomCrafting/data
        |- <namespace>
           |- recipes
              |- <folder>
                 |- <recipe_name>
           |- items
              |- <folder>
                 |- <item_name>
         */
        api.getConsole().info("- - - - [Local Storage] - - - -");
        api.getConsole().info("Searching for namespaces...");
        String[] dirs = DATA_FOLDER.list();
        if (dirs != null) {
            api.getConsole().info("Namespaces: [" + String.join(", ", dirs) + "]");
            api.getConsole().info(" - ");
            api.getConsole().info("Loading items...");
            for (String dir : dirs) {
                loadItemsInNamespace(dir);
            }
            api.getConsole().info("Loading recipes...");
            api.getConsole().info(" new format: ");
            for (String dir : dirs) {
                loadRecipesInNamespace(dir); //Load new recipe format files
            }
            for (String dir : dirs) {
                String[] typeFolders = new File(DATA_FOLDER + "/" + dir).list((dir1, name) -> !name.equals(ITEMS_FOLDER) && !name.equals(RECIPES_FOLDER));
                if (typeFolders != null && typeFolders.length > 0) {
                    //Required to load the old recipes.
                    api.getConsole().info(" legacy format: ");
                    loadAndRegisterRecipe(RecipeType.Container.CRAFTING, dir);
                    loadAndRegisterRecipe(RecipeType.Container.ELITE_CRAFTING, dir);
                    api.getConsole().info(" old format: ");
                    for (RecipeType<? extends CustomRecipe<?>> type : RecipeType.values()) {
                        loadAndRegisterRecipe(type, dir);
                    }
                }
            }
            api.getConsole().info("");
        }
    }

    private File getFileAt(NamespacedKey namespacedKey, String typeFolder) {
        return new File(DataHandler.JSON_OBJ_PATH.formatted(NamespacedKeyUtils.getKeyRoot(namespacedKey), typeFolder, NamespacedKeyUtils.getRelativeKeyObjPath(namespacedKey), NamespacedKeyUtils.getKeyObj(namespacedKey)));
    }

    @Override
    public boolean save(CustomRecipe<?> recipe) {
        File file = getFileAt(recipe.getNamespacedKey(), RECIPES_FOLDER);
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                if (file.isFile() || file.createNewFile()) {
                    JacksonUtil.getObjectWriter(customCrafting.getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, recipe);
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
            var key = item.getNamespacedKey();
            if (key != null) {
                var file = getFileAt(key, ITEMS_FOLDER);
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
        }
        return false;
    }

    @Override
    public boolean delete(CustomRecipe<?> recipe) {
        File file = getFileAt(recipe.getNamespacedKey(), recipe.getRecipeType().getId());
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
        var key = item.getNamespacedKey();
        if (key != null) {
            var file = getFileAt(key, ITEMS_FOLDER);
            if (file.delete()) {
                return true;
            } else {
                file.deleteOnExit();
            }
        }
        return false;
    }

    private List<File> getFiles(String subFolder, String type) {
        var data = new File(DATA_FOLDER, subFolder + File.separator + type);
        if (!data.exists()) return new ArrayList<>();
        File[] files = data.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
        return files != null ? Arrays.stream(files).collect(Collectors.toList()) : new ArrayList<>();
    }

    private void loadItemsInNamespace(String namespace) {
        readFiles(namespace, ITEMS_FOLDER, (relative, file, attrs) -> {
            var namespacedKey = keyFromFile(namespace, relative);
            try {
                customCrafting.getApi().getRegistries().getCustomItems().register(namespacedKey, objectMapper.readValue(file.toFile(), CustomItem.class));
            } catch (IOException e) {
                customCrafting.getLogger().severe(String.format("Could not load item '%s':", namespacedKey));
                e.printStackTrace();
                customCrafting.getLogger().severe("----------------------");
            }
            return FileVisitResult.CONTINUE;
        });
    }

    private void loadRecipesInNamespace(String namespace) {
        var injectableValues = new InjectableValues.Std();
        readFiles(namespace, RECIPES_FOLDER, (relative, file, attrs) -> {
            var namespacedKey = keyFromFile(namespace, relative);
            try {
                injectableValues.addValue("key", namespacedKey);
                customCrafting.getRegistries().getRecipes().register(objectMapper.reader(injectableValues).readValue(file.toFile(), CustomRecipe.class));
                customCrafting.getLogger().info("   - " + namespacedKey);
            } catch (IOException e) {
                ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), "", e);
            }
            return FileVisitResult.CONTINUE;
        });
    }

    private void readFiles(String namespace, String subFolder, NamespaceFileVisitor.VisitFile<Path> visitFile) {
        var dataFile = new File(DATA_FOLDER, namespace + File.separator + subFolder);
        if (dataFile.exists()) {
            try {
                var root = dataFile.toPath();
                Files.walkFileTree(root, new NamespaceFileVisitor<>(root, visitFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private NamespacedKey keyFromFile(String namespace, Path path) {
        String pathString = path.toString();
        return new NamespacedKey(customCrafting, namespace + "/" + pathString.substring(0, pathString.lastIndexOf(".")));
    }

    private void loadAndRegisterRecipe(RecipeLoader<?> loader, String namespace) {
        if (loader instanceof RecipeType.Container<?> container && container.hasLegacy()) {
            //Loading legacy recipes
            String legacyId = container.getLegacyID();
            List<File> legacyFiles = getFiles(namespace, legacyId);
            if (!legacyFiles.isEmpty()) { //If there are no legacy recipes we can skip it.
                for (RecipeType<?> type : container.getTypes()) {
                    //Remove the files that are present in new recipe folders
                    List<File> latestFiles = getFiles(namespace, type.getId());
                    legacyFiles.removeIf(legacyFile -> latestFiles.stream().anyMatch(file -> file.getName().equals(legacyFile.getName())));
                }
                loadRecipesFiles(loader, legacyFiles, namespace);
            }
            return;
        }
        loadRecipesFiles(loader, getFiles(namespace, loader.getId()), namespace);
    }

    private void loadRecipesFiles(RecipeLoader<?> loader, List<File> files, String namespace) {
        for (File file : files) {
            var name = file.getName();
            var namespacedKey = new NamespacedKey(customCrafting, namespace + "/" + name.substring(0, name.lastIndexOf(".")));
            try {
                customCrafting.getRegistries().getRecipes().register(loader.getInstance(namespacedKey, objectMapper.readTree(file)));
                customCrafting.getLogger().info("   - " + namespacedKey);
            } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), loader.getId(), e);
            }
        }
    }

    private static class NamespaceFileVisitor<T extends Path> extends SimpleFileVisitor<T> {

        private final Path root;
        private final VisitFile<T> visitFile;

        private NamespaceFileVisitor(Path root, VisitFile<T> visitFile) {
            this.root = root;
            this.visitFile = visitFile;
        }

        @Override
        public FileVisitResult visitFile(T file, BasicFileAttributes attrs) throws IOException {
            return visitFile.visit(root.relativize(file), file, attrs);
        }

        private interface VisitFile<T extends Path> {

            FileVisitResult visit(Path relative, T file, BasicFileAttributes attrs);

        }

    }

}
