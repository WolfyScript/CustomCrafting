package com.wolfyscript.customcrafting.core.resource

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.InjectableValues
import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.configuration.resources.SourceSettings
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
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

internal class DirectorySource(
    val customCrafting: CustomCrafting,
    resourceLoaderImpl: ResourceLoader,
    override val settings: SourceSettings.DirectorySourceSettings,
) : Source {

    val path: String = settings.path ?: resourceLoaderImpl.directory.path

    val directory: File

    init {
        val resolvedPath = Paths.get(path)
        directory = if (!resolvedPath.isAbsolute) {
            // The path may be relative to the resources directory, make sure to complete it
            File(resourceLoaderImpl.directory, resolvedPath.pathString)
        } else {
            File(path)
        }
    }

    override val filter: Source.Filter? =
        settings.filter?.let { DestinationFilter(customCrafting, it) }

    private fun assureDir() {
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    override fun <T : Any> load(
        type: DataType<T>,
        accept: (value: LoadedObject<T>) -> Unit,
    ) {
        assureDir()
        readFiles(directory) { relative: Path, file: Path, attrs: BasicFileAttributes ->
            val injectableValues = InjectableValues.Std().apply {
                addValue("customCrafting", customCrafting)
                addValue(CustomCrafting::class.java, customCrafting)
            }

            val key = relative.toKey(Key.CUSTOMCRAFTING_NAMESPACE)
            customCrafting.logger.info("Loading recipe: $key")
            try {
                val value = customCrafting.server!!.resourceManager.jacksonObjectMapper
                    .reader(injectableValues)
                    .readValue(file.toFile(), type.classType)

                accept(ResourceLoaderImpl.LoadedObjectImpl(key, value))
            } catch (e: Exception) {
                customCrafting.logger.error("  Error loading $key from ${type.id}: ", e)
            }
            return@readFiles FileVisitResult.CONTINUE
        }
    }

    override fun <T : Any> save(type: DataType<T>, key: Key, value: T): Result<Boolean> {
        assureDir()
        val destFile = File(directory, "${type.id}/${key.value}.conf")

        if (destFile.getParentFile().exists() || destFile.getParentFile().mkdirs()) {
            try {
                if (destFile.isFile() || destFile.createNewFile()) {
                    customCrafting.server!!.resourceManager.jacksonObjectMapper.writer(DefaultPrettyPrinter())
                        .writeValue(destFile, value)
                    return Result.success(true)
                }
            } catch (e: IOException) {
                return Result.failure(e)
            }
        }
        return Result.failure(Exception("Could not create file $destFile to save recipe $key!"))
    }

    override fun delete(type: DataType<Any>, key: Key): Result<Boolean> {
        val destFile = File(directory, "${type.id}/${key.value}.conf")

        return try {
            Result.success(destFile.delete())
        } catch (e: Exception) {
            Result.failure(Exception("Could not delete file $directory to delete recipe $key!", e))
        }

    }

    private fun readFiles(rootDir: File, visitor: NamespaceFileVisitor.CustomFileVisitor) {
        customCrafting.logger.info("Read files in ${rootDir.path}")
        if (!rootDir.exists()) return
        try {
            val root = rootDir.toPath()
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
            pathString = pathString.replace(File.separatorChar, '/')
        }
        return Key.key(namespace, pathString.take(pathString.lastIndexOf('.')))
    }

}
