package com.wolfyscript.customcrafting.core.configuration.resources

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.resource.BackupDestination
import com.wolfyscript.customcrafting.core.resource.DirectoryBackupDestination
import com.wolfyscript.customcrafting.core.resource.ResourceLoader

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
        return DirectoryBackupDestination(resourceLoader, this)
    }

}