package com.wolfyscript.customcrafting.core.configuration.resources

import com.wolfyscript.customcrafting.core.configuration.resources.BackupSettings

class BackupSettingsImpl(override val destinations: List<BackupSettings.BackupDestinationSettings>) : BackupSettings