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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependency;
import com.wolfyscript.utilities.dependency.Dependency;
import com.wolfyscript.utilities.dependency.DependencyResolver;
import com.wolfyscript.utilities.json.jackson.MissingImplementationException;
import com.wolfyscript.utilities.verification.Verifier;
import com.wolfyscript.utilities.verification.VerifierContainer;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.BackupSettings;
import me.wolfyscript.customcrafting.configs.DataSettings;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeLoader;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.lib.com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.InjectableValues;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.compatibility.PluginIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LocalStorageLoader extends ResourceLoader {

    private static final String PREFIX = "[LOCAL] ";
    // Backup
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    public static final File DATA_BACKUP_DIR = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data_backups");
    public static final File DATA_FOLDER = new File(CustomCrafting.inst().getDataFolder() + File.separator + "data");
    // directories
    private static final String ITEMS_FOLDER = "items";
    private static final String RECIPES_FOLDER = "recipes";
    // log messages
    private static final String LOG_LOADED_RECIPES = PREFIX + "Loaded %d recipes in %sms";
    private static final String LOG_FAILED_RECIPES = PREFIX + "Failed to load %d recipes";

    private DataSettings dataSettings;
    private ExecutorService executor;

    protected LocalStorageLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "local_loader"));
        this.dataSettings = customCrafting.getConfigHandler().getConfig().getDataSettings();
    }

    public static void pack(File sourceDirPath, File zipFilePath) throws IOException {
        Path p = Files.createFile(zipFilePath.toPath());
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = sourceDirPath.toPath();
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            CustomCrafting.inst().getLogger().log(Level.SEVERE, e, () -> "Failed to create backup of data directory!");
                        }
                    });
        } catch (IOException e) {
            CustomCrafting.inst().getLogger().log(Level.SEVERE, e, () -> "Failed to create backup of data directory!");
        }
    }

    @Override
    public boolean backup() {
        BackupSettings backupSettings = customCrafting.getConfigHandler().getConfig().getLocalStorageSettings().backupSettings();
        if (!backupSettings.enabled()) {
            return true;
        }

        if (!DATA_BACKUP_DIR.exists()) {
            if (!DATA_BACKUP_DIR.mkdirs()) {
                customCrafting.getLogger().severe("Failed to create backup directory!");
                return false;
            }
        }

        LocalDateTime date = LocalDateTime.now();
        String text = BACKUP_DATE_FORMAT.format(date);
        try {
            customCrafting.getLogger().info("Creating backup of data directory '" + text + ".zip'...");
            pack(DATA_FOLDER, new File(DATA_BACKUP_DIR, text));

            // Purge older backups
            File[] files = DATA_BACKUP_DIR.listFiles();
            var duration = backupSettings.keepFor();
            long currentTime = System.currentTimeMillis();
            for (File file : files) {
                if (currentTime > file.lastModified() + duration.toMillis()) {
                    file.delete();
                }
            }

            return true;
        } catch (IOException e) {
            customCrafting.getLogger().log(Level.SEVERE, e, () -> "Failed to create backup of data directory!");
            return false;
        }
    }

    @Override
    public void load() {
        /* CustomCrafting/data/<namespace>/
         *   recipes/<folder>/<recipe_name>
         *   items/<folder>/<item_name>
         */
        api.getConsole().info("- - - - [Local Storage] - - - -");
        int processors = Math.min(Runtime.getRuntime().availableProcessors(), dataSettings.maxProcessors());
        customCrafting.getLogger().info(PREFIX + "Using " + processors + " threads");
        executor = Executors.newWorkStealingPool(processors);
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

            Pair<Long, TimeUnit> timeout = config.getDataSettings().timeoutLoading();
            boolean successful;
            try {
                successful = executor.awaitTermination(timeout.getKey(), timeout.getValue());
            } catch (InterruptedException e) {
                api.getConsole().getLogger().info(String.format(LOG_LOADED_RECIPES + "; Process was interrupted: %s!",
                        customCrafting.getRegistries().getRecipes().values().size(),
                        stopWatch.getTime(TimeUnit.MILLISECONDS),
                        e.getMessage())
                );
                if (!failedRecipes.isEmpty()) {
                    api.getConsole().getLogger().warning(String.format(LOG_FAILED_RECIPES, failedRecipes.size()));
                }
                e.printStackTrace();
                return;
            }
            stopWatch.stop();
            int recipeCount = customCrafting.getRegistries().getRecipes().values().size();
            api.getConsole().getLogger().info(String.format(LOG_LOADED_RECIPES, recipeCount, stopWatch.getTime(TimeUnit.MILLISECONDS)));
            if (!failedRecipes.isEmpty()) {
                api.getConsole().getLogger().warning(String.format(LOG_FAILED_RECIPES, failedRecipes.size()));
            }
            if (!successful) {
                api.getConsole().getLogger().severe(String.format("[LOCAL] Process was interrupted, took longer than %s %s!", timeout.getKey(), timeout.getValue().toString().toLowerCase()));
                return;
            }

            printInvalidRecipes();
            printPendingRecipes();
        }
    }

    @Override
    public int validatePending(PluginIntegration pluginIntegration) {
        for (CustomRecipe<?> customRecipe : recipeDependencies.keySet()) {
            Collection<Dependency> dependencies = recipeDependencies.get(customRecipe);

            dependencies.removeIf(dependency -> dependency instanceof PluginIntegrationDependency integrationDependency && integrationDependency.getPluginIntegration().equals(pluginIntegration));
            if (!dependencies.isEmpty()) {
                continue;
            }
            // Verify and register recipe
            validateRecipe(customRecipe).ifPresentOrElse(container -> {
                switch (container.type()) {
                    case INVALID -> markInvalid(container);
                    case VALID -> customCrafting.getRegistries().getRecipes().register(customRecipe);
                }
            }, () -> customCrafting.getRegistries().getRecipes().register(customRecipe));
        }
        return 0;
    }

    private void printInvalidRecipes() {
        if (!invalidRecipes.isEmpty()) {
            api.getConsole().getLogger().info(String.format("[LOCAL] %d recipes are invalid!", invalidRecipes.size()));
            if (!dataSettings.printInvalid()) return;
            for (VerifierContainer<? extends CustomRecipe<?>> invalidRecipe : invalidRecipes) {
                api.getConsole().getLogger().info("[LOCAL] |--------------------------------------------------------------");
                api.getConsole().getLogger().info("[LOCAL] |");
                invalidRecipe.value().ifPresent(recipe -> invalidRecipe.toString().lines().forEach(s -> api.getConsole().getLogger().info("[LOCAL] |   " + s)));
                api.getConsole().getLogger().info("[LOCAL] |");
            }
            api.getConsole().getLogger().info("[LOCAL] ----------------------------");
        }
    }

    private void printPendingRecipes() {
        if (!recipeDependencies.isEmpty()) {
            api.getConsole().getLogger().info(String.format("[LOCAL] %d recipes waiting for dependencies!", recipeDependencies.size()));
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
                    if (e.getCause() instanceof MissingImplementationException missingDependencyException) {
                        customCrafting.getLogger().severe(String.format("Could not load item '%s': %s", namespacedKey, missingDependencyException.getMessage()));
                    } else {
                        if (CustomCrafting.inst().getConfigHandler().getConfig().getDataSettings().printStackTrace()) {
                            customCrafting.getLogger().log(Level.SEVERE, String.format("Could not load item '%s': ", namespacedKey), e);
                        }
                    }
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

    private void checkDependenciesAndRegister(CustomRecipe<?> recipe) {
        Set<Dependency> dependencies = DependencyResolver.resolveDependenciesFor(recipe, recipe.getClass());
        dependencies.removeIf(Dependency::isAvailable);
        if (!dependencies.isEmpty()) {
            recipeDependencies.putAll(recipe, dependencies);
            return;
        }

        // Verify and register recipe
        validateRecipe(recipe).ifPresentOrElse(container -> {
            switch (container.type()) {
                case INVALID -> markInvalid(container);
                case VALID -> customCrafting.getRegistries().getRecipes().register(recipe);
            }
        }, () -> customCrafting.getRegistries().getRecipes().register(recipe));
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
                            checkDependenciesAndRegister(recipe);

                        } catch (IOException e) {
                            markFailed(namespacedKey);

                            if (e.getCause() instanceof MissingImplementationException missingDependencyException) {
                                ChatUtils.sendRecipeItemLoadingError(PREFIX, namespacedKey.getNamespace(), namespacedKey.getKey(), missingDependencyException, false);
                            } else {
                                ChatUtils.sendRecipeItemLoadingError(PREFIX, namespacedKey.getNamespace(), namespacedKey.getKey(), e);
                            }
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
                            checkDependenciesAndRegister(recipe);
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
