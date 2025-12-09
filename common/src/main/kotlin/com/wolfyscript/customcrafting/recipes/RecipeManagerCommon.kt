package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.recipes.RecipeManager.Companion.LOG_PREFIX
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.resource.DataType
import com.wolfyscript.customcrafting.resource.LoadedObject
import com.wolfyscript.customcrafting.resource.ResourceListener
import com.wolfyscript.customcrafting.resource.ResourceLoader
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.verification.VerificationResult
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.io.File
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.*
import kotlin.io.path.copyTo
import kotlin.io.path.pathString
import kotlin.io.path.walk

class RecipeManagerCommon(val customCrafting: CustomCrafting) : RecipeManager, ResourceListener {

    private var index: RecipeIndex = RecipeIndex(emptyList())

    /**
     * Recipes can be loaded by other plugins. We keep track of which recipes CC registers, to not unload third-party recipes, for example, on a reload.
     */
    val recipesLoadedByCC: MutableSet<Key> = ObjectOpenHashSet()

    override val disabledRecipes: Set<Key>
        get() {
            return Collections.unmodifiableSet(backingDisabledRecipes)
        }
    val backingDisabledRecipes: MutableSet<Key> = ObjectOpenHashSet()

    val awaitingVerificationRecipes: MutableList<LoadedObject<CustomRecipe<*,*>>> = mutableListOf()
    val invalidRecipes: MutableList<VerificationResult<CustomRecipe<*, *>>> = mutableListOf()
    val scafall = ScafallProvider.get()

    init {
        scafall.dependencyManager.onDependencyInitialized {
            verifyRecipesAndLoad()
        }
    }

    /**
     * Prepare everything before recipes are loaded.
     */
    override fun onPrepare(resourceLoader: ResourceLoader) {
        exportDefaults(resourceLoader)
    }

    private fun exportDefaults(resourceLoader: ResourceLoader) {
        customCrafting.logger.info("${LOG_PREFIX}Exporting default recipes...")
        val dir = "com/wolfyscript/customcrafting/recipes/default"
        val resource = javaClass.classLoader.getResource(dir)?.toURI()
        if (resource == null) {
            customCrafting.logger.error("${LOG_PREFIX}Could not find default recipes!")
            return
        }
        val target = File(resourceLoader.directory, "default")
        target.mkdirs()
        copyToFromFileSystem(resource, dir, target.toPath())
        customCrafting.logger.info("${LOG_PREFIX}Default recipes exported to $target")
    }

    private fun FileSystem.copyTo(dir: String, target: Path) {
        getPath(dir).walk().forEach {
            it.copyTo(target.resolve(it.fileName.pathString), true)
        }
    }

    private fun copyToFromFileSystem(uri: URI, fromDir: String, target: Path) {
        try {
            val fs = FileSystems.getFileSystem(uri)
            // The file system is already open so we shouldn't close it as it may cause issues (e.g. on Fabric)
            fs.copyTo(fromDir, target)
        } catch (_: FileSystemNotFoundException) {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>(), javaClass.classLoader).use { fs ->
                fs.copyTo(
                    fromDir,
                    target
                ) // In this case we control the life-time of the file system, so close it after copying.
            }
        }
    }

    /**
     * How recipes should be loaded on startup
     */
    override fun onInitialLoad(resourceLoader: ResourceLoader) {
        resourceLoader.sources.forEach { dest ->
            dest.load(DataType.Recipes) {
                customCrafting.logger.info("${LOG_PREFIX}loaded: ${it.key} -> ${it.value}")
                awaitingVerificationRecipes.add(it)
            }
        }
    }

    /**
     * How recipes should be loaded when reloaded at runtime.
     * This should run on a separate thread, async to the main thread.
     */
    override fun onReload(resourceLoader: ResourceLoader) {

    }

    /**
     * Finalize the loaded recipes (for which the dependencies are already available).
     * Verify them, add them to the manager, and remove any recipes that were previously loaded and no longer loaded. (important for reloads)
     */
    override fun onFinalize(resourceLoader: ResourceLoader) {
        val previousLoaded = recipesLoadedByCC.toSet()
        recipesLoadedByCC.clear()

        verifyRecipesAndLoad()

        // Remove recipes that are no longer loaded
        val removed = previousLoaded.subtract(recipesLoadedByCC)
        if (removed.isNotEmpty()) {
            customCrafting.logger.info("${LOG_PREFIX}Removing ${removed.size} recipes that are no longer loaded")
            removeRecipes(*removed.toTypedArray())
        }
    }

    private fun verifyRecipesAndLoad() {
        customCrafting.logger.info("${LOG_PREFIX}Verifying ${awaitingVerificationRecipes.size} recipes")
        for (loadedRecipe in awaitingVerificationRecipes) {
            // TODO: verify recipe
            recipesLoadedByCC.add(loadedRecipe.key)
        }
        customCrafting.server?.recipeManager?.registerOrUpdateRecipes(awaitingVerificationRecipes)
    }

    override fun <I : RecipeInput, D : RecipeEvaluationResult.Data, T : CustomRecipe<I, D>> evaluateRecipesOfType(
        type: RecipeType<T>,
        input: I,
        context: EvaluationContext,
    ): RecipeEvaluationResult<D, T>? {
        val recipes: Collection<RecipeReference<T>> = index.byType(type)
        for (recipe in recipes) {
            if (isRecipeDisabled(recipe.key)) {
                continue
            }
            val data = recipe.value?.evaluate(input, context) ?: continue
            return RecipeEvaluationResultImpl(recipe, data)
        }
        return null
    }

    override fun disableRecipe(recipe: Key) {
        val ref = index.get(recipe)
        if (ref != null) {
            backingDisabledRecipes.add(recipe)
        }
    }

    override fun enableRecipe(key: Key) {
        backingDisabledRecipes.remove(key)
    }

    override fun isRecipeDisabled(key: Key): Boolean {
        return backingDisabledRecipes.contains(key)
    }

    override fun getRecipe(key: Key): RecipeReference<*>? {
        return index.get(key)
    }

    override fun registerOrUpdateRecipes(recipes: Collection<LoadedObject<CustomRecipe<*,*>>>) {
        index = index.registerOrUpdateAll(recipes)
    }

    override fun removeRecipes(vararg recipes: Key) {
        index = index.removeBatch(*recipes)
    }

    override fun recipes(): Collection<RecipeReference<*>> {
        return index.values()
    }

}