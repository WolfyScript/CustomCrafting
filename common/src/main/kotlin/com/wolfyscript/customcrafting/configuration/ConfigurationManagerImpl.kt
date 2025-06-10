package com.wolfyscript.customcrafting.configuration

import com.fasterxml.jackson.databind.module.SimpleModule
import com.wolfyscript.customcrafting.configuration.cli.CLISettings
import com.wolfyscript.customcrafting.configuration.editor.EditorSettings
import com.wolfyscript.customcrafting.configuration.gui.GUISettings
import com.wolfyscript.customcrafting.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.configuration.resources.BackupSettingsImpl
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.configuration.resources.FilterEntryImpl
import com.wolfyscript.customcrafting.configuration.resources.FilterSettingsImpl
import com.wolfyscript.customcrafting.configuration.resources.LocalDestinationSettingsImpl
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettingsImpl
import com.wolfyscript.customcrafting.configuration.resources.SQLDestinationSettingsImpl
import com.wolfyscript.customcrafting.util.exportResource
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import java.io.File
import kotlin.jvm.java

class ConfigurationManagerImpl(val rootDir: File) : ConfigurationManager {

    private val configDir = File(rootDir, "config")
    private val configMapper = HoconMapper()

    override var resourceSettings: ResourceSettings = ResourceSettingsImpl(emptyList())
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

            // Resource Settings
            addAbstractTypeMapping(ResourceSettings::class.java, ResourceSettingsImpl::class.java)
            addAbstractTypeMapping(DestinationSettings.SQLDestinationSettings::class.java, SQLDestinationSettingsImpl::class.java)
            addAbstractTypeMapping(DestinationSettings.LocalDestinationSettings::class.java, LocalDestinationSettingsImpl::class.java)
            addAbstractTypeMapping(DestinationSettings.BackupSettings::class.java, BackupSettingsImpl::class.java)
            addAbstractTypeMapping(DestinationSettings.FilterSettings::class.java, FilterSettingsImpl::class.java)
            addAbstractTypeMapping(DestinationSettings.FilterSettings.FilterEntry::class.java, FilterEntryImpl::class.java)

        }
        configMapper.registerModule(mappingModule)
    }

    fun saveDefaults() {
        if (!resourcesSettingsFile.exists()) {
            exportResource("com/wolfyscript/customcrafting/configuration/default/resources/resources.conf", resourcesSettingsFile)
        }



    }


    override fun load() {
        saveDefaults()

        resourceSettings = configMapper.readValue(resourcesSettingsFile, ResourceSettingsImpl::class.java)




    }

}