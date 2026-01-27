package com.wolfyscript.customcrafting.configuration.resources

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.resource.BackupDestination
import com.wolfyscript.customcrafting.resource.DirectoryBackupDestination
import com.wolfyscript.customcrafting.resource.ResourceLoader

class DirectoryBackupDestinationSettingsImpl(
    override val path: String,
    override val compress: Boolean,
    override val keep: Int
) : BackupSettings.DirectoryBackupDestinationSettings {

    override fun configureFor(
        customCrafting: CustomCrafting,
        resourceLoader: ResourceLoader,
        backupSettings: BackupSettings,
    ): BackupDestination {
        return DirectoryBackupDestination(customCrafting, resourceLoader, this)
    }

}