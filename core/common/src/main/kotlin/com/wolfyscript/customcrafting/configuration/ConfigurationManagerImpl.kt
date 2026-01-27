package com.wolfyscript.customcrafting.configuration

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.cli.CLISettings
import com.wolfyscript.customcrafting.configuration.editor.EditorSettings
import com.wolfyscript.customcrafting.configuration.gui.GUISettings
import com.wolfyscript.customcrafting.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.configuration.resources.*
import com.wolfyscript.customcrafting.util.exportResource
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import java.io.File

class ConfigurationManagerImpl(val customCrafting: CustomCrafting, val rootDir: File) : ConfigurationManager {

    private val configDir = File(rootDir, "config")
    private val configMapper = HoconMapper()

    override var resourceSettings: ResourceSettings = ResourceSettingsImpl(emptyList(), BackupSettingsImpl(emptyList()))
    override val gameMechanicSettings: GameMechanicSettings
        get() = TODO("Not yet implemented")
    override val guiSettings: GUISettings
        get() = TODO("Not yet implemented")
    override val cliSettings: CLISettings
        get() = TODO("Not yet implemented")
    override val editorSettings: EditorSettings
        get() = TODO("Not yet implemented")

    private val resourcesSettingsFile = File(configDir, "resources/resources.conf")
    private val gameMechanicSettingsFile = File(configDir, "mechanics/mechanics.conf")
    private val guiSettingsFile = File(configDir, "gui/gui.conf")
    private val cliSettingsFile = File(configDir, "cli/cli.conf")
    private val editorSettingsFile = File(configDir, "editor/editor.conf")

    init {
        val mappingModule = SimpleModule().apply {
            addAbstractTypeMapping(
                ResourceSettings::class.java,
                ResourceSettingsImpl::class.java
            )

            // Source Settings
            addAbstractTypeMapping(
                SourceSettings.SQLSourceSettings::class.java,
                SQLSourceSettingsImpl::class.java
            )
            addAbstractTypeMapping(
                SourceSettings.DirectorySourceSettings::class.java,
                DirectorySourceSettingsImpl::class.java
            )
            addAbstractTypeMapping(
                SourceSettings.FilterSettings::class.java,
                FilterSettingsImpl::class.java
            )
            addAbstractTypeMapping(
                SourceSettings.FilterSettings.FilterEntry::class.java,
                FilterEntryImpl::class.java
            )

            // Backup Settings
            addAbstractTypeMapping(
                BackupSettings::class.java,
                BackupSettingsImpl::class.java
            )
            addAbstractTypeMapping(
                BackupSettings.DirectoryBackupDestinationSettings::class.java,
                DirectoryBackupDestinationSettingsImpl::class.java
            )

        }
        configMapper.registerModule(mappingModule)
        configMapper.registerKotlinModule()
    }

    init {
        saveDefaults()

        resourceSettings = configMapper.readValue<ResourceSettings>(resourcesSettingsFile)
    }

    fun saveDefaults() {
        if (!resourcesSettingsFile.exists()) {
            exportResource(
                "com/wolfyscript/customcrafting/configuration/default/resources/resources.conf",
                resourcesSettingsFile
            )
        }
    }

    override fun load() {
        customCrafting.logger.info("Loading configurations...")

    }

}