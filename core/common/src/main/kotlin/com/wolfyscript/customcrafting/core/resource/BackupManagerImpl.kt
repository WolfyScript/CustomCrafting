package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.BackupSettings
import com.wolfyscript.customcrafting.resource.BackupManager
import com.wolfyscript.customcrafting.resource.ResourceManager

class BackupManagerImpl(val customCrafting: CustomCrafting, val resourceManager: ResourceManager, val settings: BackupSettings) :
    BackupManager {

    val destinations = settings.destinations.map { it.configureFor(customCrafting, resourceManager.resourceLoader, settings) }

    override fun createBackup() {
        for (destination in destinations) {
            destination.backup()
        }
    }

}