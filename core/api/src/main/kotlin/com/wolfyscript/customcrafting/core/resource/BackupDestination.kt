package com.wolfyscript.customcrafting.core.resource

import java.io.File

interface BackupDestination {

    /**
     * Create a backup of the current state of resources.
     *
     * @return The file containing the backup. It may be a directory or a zip file.
     */
    fun backup() : Result<File>

}