package com.wolfyscript.customcrafting.resource

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.InjectableValues
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.pathString

class LocalDestination(
    customCrafting: CustomCrafting,
    resourceLoaderImpl: ResourceLoaderImpl,
    settings: DestinationSettings.LocalDestinationSettings,
) :
    AbstractDestination<DestinationSettings.LocalDestinationSettings>(customCrafting, resourceLoaderImpl, settings) {

    val path: String = settings.path ?: resourceLoaderImpl.directory.path

    override val filter: ResourceLoader.Destination.Filter? =
        settings.filter?.let { DestinationFilter(customCrafting, it) }

    private fun assureDir() {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    override fun load() {
        assureDir()

        val resolvedPath = Paths.get(path)
        val rootDir = if (!resolvedPath.isAbsolute) {
            customCrafting.logger.info("Use relative path: ${resolvedPath.pathString}")
            File(resourceLoaderImpl.directory, resolvedPath.pathString)
        } else {
            customCrafting.logger.info("Use absolute path: $path")
            File(path)
        }

        if (!rootDir.exists()) rootDir.mkdirs()
        if (settings.backup != null) {
            return
        }

        customCrafting.logger.info("Read files in ${rootDir.path}")
        readFiles(rootDir.path) { relative: Path, file: Path, attrs: BasicFileAttributes ->
            customCrafting.logger.info("  Found ${file.fileName}")
            val injectableValues = InjectableValues.Std().apply {
                addValue("customCrafting", customCrafting)
                addValue(CustomCrafting::class.java, customCrafting)
            }

            val key = relative.toKey(Key.CUSTOMCRAFTING_NAMESPACE)
            try {
                val recipe = customCrafting.dataManager.jacksonObjectMapper
                    .reader(injectableValues)
                    .readValue(file.toFile(), CustomRecipe::class.java)

                customCrafting.logger.info("Loaded recipe: $key")

                // Temporarily store the recipe to check dependencies later
                resourceLoaderImpl.addRecipeFrom(ResourceLoaderImpl.LoadedRecipe(key, recipe, listOf()), this)
            } catch (e: Exception) {
                customCrafting.logger.error("Error loading recipe: ", e)
            }
            return@readFiles FileVisitResult.CONTINUE
        }
    }

    override fun save(recipe: CustomRecipe<*, *>): Result<Boolean> {
        assureDir()

        val key = customCrafting.registries.customRecipes.getKey(recipe)
            ?: return Result.failure(Exception("No key found for recipe $recipe!"))
        val destPath = "${path}/${key.value}.conf"

        val destFile = File(destPath)

        if (destFile.getParentFile().exists() || destFile.getParentFile().mkdirs()) {
            try {
                if (destFile.isFile() || destFile.createNewFile()) {
                    customCrafting.dataManager.jacksonObjectMapper.writer(DefaultPrettyPrinter())
                        .writeValue(destFile, recipe)
                    return Result.success(true)
                }
            } catch (e: IOException) {
                return Result.failure(e)
            }
        }
        return Result.failure(Exception("Could not create file $destPath to save recipe $key!"))
    }

    override fun delete(recipe: CustomRecipe<*, *>): Result<Boolean> {
        val key = customCrafting.registries.customRecipes.getKey(recipe)
            ?: return Result.failure(Exception("No key found for recipe $recipe!"))
        val destPath = "${path}/${key.value}.conf"
        val destFile = File(destPath)

        return try {
            Result.success(destFile.delete())
        } catch (e: Exception) {
            Result.failure(Exception("Could not delete file $destPath to delete recipe $key!", e))
        }

    }

    private fun readFiles(root: String, visitor: NamespaceFileVisitor.CustomFileVisitor) {
        val directoryPath = File(root)
        if (!directoryPath.exists()) return
        try {
            val root = directoryPath.toPath()
            customCrafting.logger.info("Walk file tree: $directoryPath")
            Files.walkFileTree(root, NamespaceFileVisitor(root, visitor))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private class NamespaceFileVisitor(val root: Path, val visitor: CustomFileVisitor) : SimpleFileVisitor<Path>() {

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            return visitor.visit(root.relativize(file), file, attrs)
        }

        fun interface CustomFileVisitor {

            fun visit(relative: Path, file: Path, attrs: BasicFileAttributes): FileVisitResult

        }

    }

    fun Path.toKey(namespace: String): Key {
        var pathString = this.toString()
        if (!File.separator.equals("/")) {
            // #205: Required to work with Windows file separators (And possibly other separators).
            pathString = pathString.replace(File.separatorChar, '/');
        }
        return Key.key(namespace, pathString.substring(0, pathString.lastIndexOf('.')))
    }

}
