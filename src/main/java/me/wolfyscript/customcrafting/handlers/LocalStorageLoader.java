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
import me.wolfyscript.customcrafting.configs.DataSettings;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeLoader;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.validator.ValidationContainer;
import me.wolfyscript.customcrafting.recipes.validator.Validator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.lib.com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.InjectableValues;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;

public class LocalStorageLoader extends ResourceLoader {

    private static final String PREFIX = "[LOCAL] ";
    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");
    private static final String ITEMS_FOLDER = "items";
    private static final String RECIPES_FOLDER = "recipes";
    private DataSettings dataSettings;
    private final ExecutorService executor;
    private final List<NamespacedKey> failedRecipes;
    private final List<ValidationContainer<? extends CustomRecipe<?>>> pendingRecipes;
    private final List<ValidationContainer<? extends CustomRecipe<?>>> invalidRecipes;

    protected LocalStorageLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "local_loader"));
        this.pendingRecipes = new ArrayList<>();
        this.failedRecipes = new ArrayList<>();
        this.invalidRecipes = new ArrayList<>();
        this.dataSettings = customCrafting.getConfigHandler().getConfig().getDataSettings();
        executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    }

    protected void markPending(ValidationContainer<? extends CustomRecipe<?>> recipe) {
        synchronized (pendingRecipes) {
            pendingRecipes.add(recipe);
        }
    }

    protected void markInvalid(ValidationContainer<? extends CustomRecipe<?>> recipe) {
        synchronized (invalidRecipes) {
            invalidRecipes.add(recipe);
        }
    }

    protected void markFailed(NamespacedKey recipe) {
        synchronized (failedRecipes) {
            failedRecipes.add(recipe);
        }
    }

    private static <T extends CustomRecipe<?>> Optional<ValidationContainer<T>> validateRecipe(T recipe) {
        Validator<T> validator = (Validator<T>) CustomCrafting.inst().getRegistries().getValidators().get(recipe.getRecipeType().getNamespacedKey());
        if (validator == null) return Optional.empty();
        return Optional.of(validator.validate(recipe));
    }

    @Override
    public void load() {
        /* CustomCrafting/data/<namespace>/
         *   recipes/<folder>/<recipe_name>
         *   items/<folder>/<item_name>
         */
        api.getConsole().info("- - - - [Local Storage] - - - -");
        api.getConsole().info(PREFIX + "Looking through data folder...");
        String[] dirs = DATA_FOLDER.list();
        if (dirs != null) {
            api.getConsole().info(PREFIX + "$msg.startup.recipes.items$");
            for (String dir : dirs) {
                loadItemsInNamespace(dir);
            }
            StopWatch stopWatch = StopWatch.createStarted();

            api.getConsole().info(PREFIX + "$msg.startup.recipes.recipes$");
            new NewDataLoader(dirs).load();
            //Loading old & legacy recipes
            //The recipes are only loaded if they are not already loaded in previous stages! So if a new version of a recipe exists, then the older ones are ignored.
            new OldDataLoader(dirs).load();
            new LegacyDataLoader(dirs).load();

            executor.shutdown();

            Pair<Long, TimeUnit> timeout = config.getDataSettings().getTimeoutLoading();
            boolean successful;
            try {
                successful = executor.awaitTermination(timeout.getKey(), timeout.getValue());
            } catch (InterruptedException e) {
                api.getConsole().getLogger().info(String.format("[LOCAL] Loaded %d recipes in %sms; Process was interrupted: %s!",
                        customCrafting.getRegistries().getRecipes().values().size(),
                        stopWatch.getTime(TimeUnit.MILLISECONDS),
                        e.getMessage())
                );
                if (!failedRecipes.isEmpty()) {
                    api.getConsole().getLogger().warning(String.format("[LOCAL] Failed to load %d recipes", failedRecipes.size()));
                }
                e.printStackTrace();
                return;
            }
            stopWatch.stop();
            int recipeCount = customCrafting.getRegistries().getRecipes().values().size();
            api.getConsole().getLogger().info(String.format("[LOCAL] Loaded %d recipes in %sms", recipeCount, stopWatch.getTime(TimeUnit.MILLISECONDS)));
            if (!failedRecipes.isEmpty()) {
                api.getConsole().getLogger().warning(String.format("[LOCAL] Failed to load %d recipes", failedRecipes.size()));
            }
            if (!successful) {
                api.getConsole().getLogger().severe(String.format("[LOCAL] Process was interrupted, took longer than %s %s!", timeout.getKey(), timeout.getValue().toString().toLowerCase()));
                return;
            }

            printPendingRecipes();
            printInvalidRecipes();

            Pair<Long, TimeUnit> timeoutPending = dataSettings.getTimeoutPending();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    for (ValidationContainer<? extends CustomRecipe<?>> pendingRecipe : pendingRecipes) {
                        markInvalid(pendingRecipe);
                    }
                    printInvalidRecipes();
                    cancel();
                }
            }, timeoutPending.getValue().toMillis(timeoutPending.getKey()));
        }
    }

    @Override
    public void validatePending() {
        int pendingSize = pendingRecipes.size();
        int validatedRecipeCount = 0;

        Iterator<ValidationContainer<? extends CustomRecipe<?>>> pendingIterator = pendingRecipes.iterator();
        while (pendingIterator.hasNext()) {
            ValidationContainer<? extends CustomRecipe<?>> container = pendingIterator.next();
            container.revalidate();
            if (container.type() == ValidationContainer.ResultType.PENDING) continue;
            pendingIterator.remove();
            if (container.type() == ValidationContainer.ResultType.INVALID) {
                markInvalid(container);
            }
            if (container.type() == ValidationContainer.ResultType.VALID) {
                validatedRecipeCount++;
                container.value().ifPresent(recipe -> customCrafting.getRegistries().getRecipes().register(recipe));
            }
        }

        api.getConsole().getLogger().info(String.format("[LOCAL] Validated %d recipes from %d pending recipes", validatedRecipeCount, pendingSize));

        printPendingRecipes();
        printInvalidRecipes();
    }

    private void printPendingRecipes() {
        if (!pendingRecipes.isEmpty()) {
            api.getConsole().getLogger().info(String.format("[LOCAL] %d recipes still pending for validation (waiting for dependencies)", pendingRecipes.size()));
            if (!dataSettings.isPrintPending()) return;
            for (ValidationContainer<? extends CustomRecipe<?>> pendingRecipe : pendingRecipes) {
                api.getConsole().getLogger().info("[LOCAL] |--------------------------------------------------------------");
                api.getConsole().getLogger().info("[LOCAL] |");
                pendingRecipe.value().ifPresent(recipe -> pendingRecipe.toString().lines().forEach(s -> api.getConsole().getLogger().info("[LOCAL] |   " + s)));
                api.getConsole().getLogger().info("[LOCAL] |");
            }
            api.getConsole().getLogger().info("[LOCAL] ----------------------------");
        }
    }

    private void printInvalidRecipes() {
        if (!invalidRecipes.isEmpty() && dataSettings.isPrintInvalid()) {
            api.getConsole().getLogger().info(String.format("[LOCAL] %d recipes are invalid!", invalidRecipes.size()));
            if (!dataSettings.isPrintInvalid()) return;
            for (ValidationContainer<? extends CustomRecipe<?>> invalidRecipe : invalidRecipes) {
                api.getConsole().getLogger().info("[LOCAL] |--------------------------------------------------------------");
                api.getConsole().getLogger().info("[LOCAL] |");
                invalidRecipe.value().ifPresent(recipe -> invalidRecipe.toString().lines().forEach(s -> api.getConsole().getLogger().info("[LOCAL] |   " + s)));
                api.getConsole().getLogger().info("[LOCAL] |");
            }
            api.getConsole().getLogger().info("[LOCAL] ----------------------------");
        }
    }

    /**
     * Gets the file at the specific path from the {@link NamespacedKey} and sub-folder with the .conf extension.
     *
     * @param namespacedKey The NamespacedKey for the path.
     * @param typeFolder    The sub-folder of the path. Like {@link #ITEMS_FOLDER} or {@link #RECIPES_FOLDER}.
     * @return The File at the specific path.
     */
    private File getFileAt(NamespacedKey namespacedKey, String typeFolder) {
        return new File(DataHandler.HOCON_OBJ_PATH.formatted(NamespacedKeyUtils.getKeyRoot(namespacedKey), typeFolder, NamespacedKeyUtils.getRelativeKeyObjPath(namespacedKey)));
    }

    /**
     * Gets the file at the specific path from the {@link NamespacedKey} and sub-folder with the .json extension.
     *
     * @param namespacedKey The NamespacedKey for the path.
     * @param typeFolder    The sub-folder of the path. Like {@link #ITEMS_FOLDER} or {@link #RECIPES_FOLDER}.
     * @return The File at the specific path.
     */
    private File getFileAtJson(NamespacedKey namespacedKey, String typeFolder) {
        return new File(DataHandler.JSON_OBJ_PATH.formatted(NamespacedKeyUtils.getKeyRoot(namespacedKey), typeFolder, NamespacedKeyUtils.getRelativeKeyObjPath(namespacedKey)));
    }

    /**
     * Gets the NamespacedKey from the namespace and path.
     *
     * @param namespace The namespace in the data folder.
     * @param path      The rest of the path.
     * @return The NamespacedKey from the namespace and path.
     */
    private NamespacedKey keyFromFile(String namespace, Path path) {
        String pathString = path.toString();
        if (!File.separator.equals("/")) {
            // #205: Required to work with Windows file separators (And possibly other separators).
            pathString = pathString.replace(File.separatorChar, '/');
        }
        return new NamespacedKey(customCrafting, namespace + "/" + pathString.substring(0, pathString.lastIndexOf(".")));
    }

    private void loadItemsInNamespace(String namespace) {
        var customItems = customCrafting.getApi().getRegistries().getCustomItems();
        readFiles(namespace, ITEMS_FOLDER, (relative, file, attrs) -> {
            if (isValidFile(file.toFile())) return FileVisitResult.CONTINUE;
            var namespacedKey = keyFromFile(namespace, relative);
            if (isReplaceData() || !customItems.has(namespacedKey)) {
                try {
                    customItems.register(namespacedKey, objectMapper.readValue(file.toFile(), CustomItem.class));
                } catch (IOException e) {
                    customCrafting.getLogger().severe(String.format("Could not load item '%s':", namespacedKey));
                    e.printStackTrace();
                    customCrafting.getLogger().severe("----------------------");
                }
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

    @Override
    public boolean save(CustomRecipe<?> recipe) {
        File file = getFileAt(recipe.getNamespacedKey(), RECIPES_FOLDER);
        File jsonFile = getFileAtJson(recipe.getNamespacedKey(), RECIPES_FOLDER);
        if (jsonFile.exists()) { // In case there is already an old json file let's rename it.
            if (!jsonFile.renameTo(file)) {
                return false;
            }
        }
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                if (file.isFile() || file.createNewFile()) {
                    customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper().writer(customCrafting.getConfigHandler().getConfig().isPrettyPrinting() ? new DefaultPrettyPrinter() : null).writeValue(file, recipe);
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
                File jsonFile = getFileAtJson(key, ITEMS_FOLDER);
                if (jsonFile.exists()) { // In case there is already an old json file let's rename it.
                    if (!jsonFile.renameTo(file)) {
                        return false;
                    }
                }
                if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
                    try {
                        if (file.exists() || file.createNewFile()) {
                            customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper().writer(customCrafting.getConfigHandler().getConfig().isPrettyPrinting() ? new DefaultPrettyPrinter() : null).writeValue(file, item);
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
    public boolean delete(CustomRecipe<?> recipe) throws IOException {
        // Deletes the recipe file that is saved under the recipe type specific folder
        File legacyFile = getFileAt(recipe.getNamespacedKey(), recipe.getRecipeType().getId());
        if (legacyFile.exists()) {
            Files.delete(legacyFile.toPath());
        }

        // Deletes both the HOCON and JSON file
        File hoconFile = getFileAt(recipe.getNamespacedKey(), RECIPES_FOLDER);
        if (hoconFile.exists()) {
            Files.delete(hoconFile.toPath());
        }
        File jsonFile = getFileAtJson(recipe.getNamespacedKey(), RECIPES_FOLDER);
        if (jsonFile.exists()) {
            Files.delete(jsonFile.toPath());
        }
        return true;
    }

    @Override
    public boolean delete(CustomItem item) throws IOException {
        var key = item.getNamespacedKey();
        if (key != null) {
            // Deletes both the HOCON and JSON file
            var hoconFile = getFileAt(key, ITEMS_FOLDER);
            if (hoconFile.exists()) {
                Files.delete(hoconFile.toPath());
            }
            var jsonFile = getFileAt(key, ITEMS_FOLDER);
            if (jsonFile.exists()) {
                Files.delete(jsonFile.toPath());
            }
            return true;
        }
        return false;
    }

    /**
     * Used to load data & cache the loaded, skipped errors & already existing keys.
     */
    private abstract static class DataLoader {

        protected final String[] dirs;

        private DataLoader(String[] dirs) {
            this.dirs = dirs;
        }

        protected abstract void load();

    }

    private class NewDataLoader extends DataLoader {

        private NewDataLoader(String[] dirs) {
            super(dirs);
        }

        @Override
        protected void load() {
            for (String dir : dirs) {
                loadRecipesInNamespace(dir); //Load new recipe format files
            }
        }

        private void loadRecipesInNamespace(String namespace) {
            TypeReference<CustomRecipe<?>> recipeTypeRef = new TypeReference<>() {
            };
            readFiles(namespace, RECIPES_FOLDER, (relative, file, attrs) -> {
                if (isValidFile(file.toFile())) return FileVisitResult.CONTINUE;
                final var namespacedKey = keyFromFile(namespace, relative);
                if (isReplaceData() || !customCrafting.getRegistries().getRecipes().has(namespacedKey)) {
                    executor.execute(() -> {
                        try {
                            var injectableValues = new InjectableValues.Std();
                            injectableValues.addValue("key", namespacedKey);
                            injectableValues.addValue("customcrafting", customCrafting);

                            CustomRecipe<?> recipe = objectMapper.reader(injectableValues).forType(recipeTypeRef).readValue(file.toFile());

                            validateRecipe(recipe).ifPresentOrElse(container -> {
                                switch (container.type()) {
                                    case INVALID -> markInvalid(container);
                                    case PENDING -> markPending(container);
                                    case VALID -> customCrafting.getRegistries().getRecipes().register(recipe);
                                }
                            }, () -> customCrafting.getRegistries().getRecipes().register(recipe));
                        } catch (IOException e) {
                            ChatUtils.sendRecipeItemLoadingError(PREFIX, namespacedKey.getNamespace(), namespacedKey.getKey(), e);
                            markFailed(namespacedKey);
                        }
                    });
                }
                return FileVisitResult.CONTINUE;
            });
        }
    }

    private class LegacyDataLoader extends OldDataLoader {

        private LegacyDataLoader(String[] dirs) {
            super(dirs);
        }

        @Override
        protected void load() {
            for (String dir : this.dirs) {
                String[] typeFolders = getOldTypeFolders(dir);
                if (typeFolders != null && typeFolders.length > 0) {
                    loadAndRegisterOldOrLegacyRecipe(RecipeType.Container.CRAFTING, dir);
                    loadAndRegisterOldOrLegacyRecipe(RecipeType.Container.ELITE_CRAFTING, dir);
                }
            }
        }
    }

    private class OldDataLoader extends DataLoader {

        private OldDataLoader(String[] dirs) {
            super(dirs);
        }

        protected void load() {
            for (String dir : this.dirs) {
                String[] typeFolders = getOldTypeFolders(dir);
                if (typeFolders != null && typeFolders.length > 0) {
                    for (RecipeType<? extends CustomRecipe<?>> type : RecipeType.values()) {
                        loadAndRegisterOldOrLegacyRecipe(type, dir);
                    }
                }
            }
        }

        protected List<File> getOldOrLegacyFiles(String subFolder, String type) {
            var data = new File(DATA_FOLDER, subFolder + File.separator + type);
            if (!data.exists()) return new ArrayList<>();
            File[] files = data.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
            return files != null ? Arrays.stream(files).toList() : new ArrayList<>();
        }

        protected void loadAndRegisterOldOrLegacyRecipe(RecipeLoader<?> loader, String namespace) {
            if (loader instanceof RecipeType.Container<?> container && container.hasLegacy()) {
                //Loading legacy recipes
                List<File> legacyFiles = getOldOrLegacyFiles(namespace, container.getLegacyID());
                if (!legacyFiles.isEmpty()) { //If there are no legacy recipes we can skip it.
                    loadOldOrLegacyRecipeFiles(loader, legacyFiles, namespace);
                }
                return;
            }
            loadOldOrLegacyRecipeFiles(loader, getOldOrLegacyFiles(namespace, loader.getId()), namespace);
        }

        protected void loadOldOrLegacyRecipeFiles(RecipeLoader<?> loader, List<File> files, String namespace) {
            for (File file : files) {
                var name = file.getName();
                if (isValidFile(file)) continue;
                var namespacedKey = new NamespacedKey(customCrafting, namespace + "/" + name.substring(0, name.lastIndexOf(".")));
                if (!customCrafting.getRegistries().getRecipes().has(namespacedKey)) {
                    executor.execute(() -> {
                        try {
                            CustomRecipe<?> recipe = loader.getInstance(namespacedKey, objectMapper.readTree(file));

                            validateRecipe(recipe).ifPresentOrElse(container -> {
                                switch (container.type()) {
                                    case INVALID -> markInvalid(container);
                                    case PENDING -> markPending(container);
                                    case VALID -> customCrafting.getRegistries().getRecipes().register(recipe);
                                }
                            }, () -> customCrafting.getRegistries().getRecipes().register(recipe));
                        } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException |
                                 IllegalAccessException e) {
                            ChatUtils.sendRecipeItemLoadingError("[LOCAL_OLD] ", namespacedKey.getNamespace(), namespacedKey.getKey(), e);
                            markFailed(namespacedKey);
                        }
                    });
                }
            }
        }

        protected String[] getOldTypeFolders(String namespace) {
            return new File(DATA_FOLDER + "/" + namespace).list((dir1, name) -> !name.equals(ITEMS_FOLDER) && !name.equals(RECIPES_FOLDER));
        }

    }

    /**
     * A simple file visitor that that goes through the file tree of the specified namespace using the custom {@link VisitFile<T>} callback.
     *
     * @param <T> The type of the path.
     */
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
