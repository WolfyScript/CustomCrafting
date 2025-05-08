package com.wolfyscript.customcrafting.configuration

import com.wolfyscript.customcrafting.configuration.cli.CLISettings
import com.wolfyscript.customcrafting.configuration.editor.EditorSettings
import com.wolfyscript.customcrafting.configuration.gui.GUISettings
import com.wolfyscript.customcrafting.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings

class ConfigurationManagerImpl : ConfigurationManager {

    override val resourceSettings: ResourceSettings
        get() = TODO("Not yet implemented")
    override val gameMechanicSettings: GameMechanicSettings
        get() = TODO("Not yet implemented")
    override val guiSettings: GUISettings
        get() = TODO("Not yet implemented")
    override val clicSettings: CLISettings
        get() = TODO("Not yet implemented")
    override val editorSettings: EditorSettings
        get() = TODO("Not yet implemented")

    override fun load() {

    }

}