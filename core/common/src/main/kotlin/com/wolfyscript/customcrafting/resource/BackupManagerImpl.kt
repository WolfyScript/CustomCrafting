package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.BackupSettings

class BackupManagerImpl(val customCrafting: CustomCrafting, val resourceManager: ResourceManager, val settings: BackupSettings) : BackupManager {

    val destinations = settings.destinations.map { it.configureFor(customCrafting, resourceManager.resourceLoader, settings) }

    override fun createBackup() {
        for (destination in destinations) {
            destination.backup()
        }
    }

}