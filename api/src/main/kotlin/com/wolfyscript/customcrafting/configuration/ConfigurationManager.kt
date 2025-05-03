package com.wolfyscript.customcrafting.configuration

import com.wolfyscript.customcrafting.configuration.cli.CLISettings
import com.wolfyscript.customcrafting.configuration.editor.EditorSettings
import com.wolfyscript.customcrafting.configuration.gui.GUISettings
import com.wolfyscript.customcrafting.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings

/**
 * Handles the configurations of all the different plugin modules.
 */
interface ConfigurationManager {

    fun load()

    val resourceSettings: ResourceSettings

    val gameMechanicSettings: GameMechanicSettings

    val guiSettings: GUISettings

    val clicSettings: CLISettings

    val editorSettings: EditorSettings

}