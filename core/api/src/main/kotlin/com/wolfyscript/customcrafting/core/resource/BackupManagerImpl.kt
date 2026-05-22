package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.configuration.resources.BackupSettings

internal class BackupManagerImpl(val customCrafting: CustomCrafting, val resourceManager: ResourceManager, val settings: BackupSettings) :
    BackupManager {

    val destinations = settings.destinations.map { it.configureFor(customCrafting, resourceManager.resourceLoader, settings) }

    override fun createBackup() {
        for (destination in destinations) {
            destination.backup()
        }
    }

}