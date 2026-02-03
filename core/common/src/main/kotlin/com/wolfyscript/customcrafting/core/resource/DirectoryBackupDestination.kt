package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.configuration.resources.BackupSettings
import com.wolfyscript.customcrafting.core.resource.BackupDestination
import com.wolfyscript.customcrafting.core.resource.ResourceLoader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.pathString

class DirectoryBackupDestination(
    customCrafting: CustomCrafting,
    val resourceLoader: ResourceLoader,
    val settings: BackupSettings.DirectoryBackupDestinationSettings,
) : BackupDestination {

    companion object {
        private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    }

    val path: String = settings.path
    val directory: File = File(resourceLoader.directory.toPath().resolve(path).pathString)

    override fun backup(): Result<File> {
        val date = LocalDateTime.now()
        val backupName = dateFormat.format(date)

        if (!directory.exists() && !directory.mkdirs()) {
            return Result.failure(Exception("Failed to create backup directory $directory"))
        }

        if (settings.compress) {
            val zipBackupFile = File(directory, "$backupName.zip")
            ZipOutputStream(zipBackupFile.outputStream()).use { zipOutputStream ->
                for (source in resourceLoader.sources) {
                    if (source is DirectorySource) {
                        source.directory.walkTopDown()
                            .filter { it.isFile }
                            .forEach { file ->
                                val zipEntry = ZipEntry(
                                    // relative path including the root directory
                                    source.directory.parentFile.toPath().relativize(file.toPath()).pathString
                                )
                                zipOutputStream.putNextEntry(zipEntry)
                                file.inputStream().use { it.copyTo(zipOutputStream) }
                                zipOutputStream.closeEntry()
                            }
                    }
                }
            }
            return Result.success(zipBackupFile)
        } else {
            val backupDirectory = File(directory, backupName)
            if (!backupDirectory.exists() && !backupDirectory.mkdirs()) {
                return Result.failure(Exception("Failed to create backup directory $backupDirectory"))
            }

            for (source in resourceLoader.sources) {
                if (source is DirectorySource) {
                    source.directory.copyRecursively(File(backupDirectory, source.directory.name), overwrite = true)
                }
            }
            return Result.success(backupDirectory)
        }
    }

}