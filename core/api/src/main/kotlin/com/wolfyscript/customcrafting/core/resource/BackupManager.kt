package com.wolfyscript.customcrafting.core.resource

/**
 * Manages backups of resources.
 */
interface BackupManager {

    /**
     * Requests a new backup to be created of the current state of resources.
     */
    fun createBackup()

}