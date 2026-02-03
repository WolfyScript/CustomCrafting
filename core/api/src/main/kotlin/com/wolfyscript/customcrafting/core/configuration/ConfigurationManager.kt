package com.wolfyscript.customcrafting.core.configuration

import com.wolfyscript.customcrafting.core.configuration.cli.CLISettings
import com.wolfyscript.customcrafting.core.configuration.editor.EditorSettings
import com.wolfyscript.customcrafting.core.configuration.gui.GUISettings
import com.wolfyscript.customcrafting.core.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.core.configuration.resources.ResourceSettings

/**
 * Handles the configurations of all the different plugin modules.
 */
interface ConfigurationManager {

    fun load()

    val resourceSettings: com.wolfyscript.customcrafting.core.configuration.resources.ResourceSettings

    val gameMechanicSettings: com.wolfyscript.customcrafting.core.configuration.mechanics.GameMechanicSettings

    val guiSettings: com.wolfyscript.customcrafting.core.configuration.gui.GUISettings

    val cliSettings: com.wolfyscript.customcrafting.core.configuration.cli.CLISettings

    val editorSettings: com.wolfyscript.customcrafting.core.configuration.editor.EditorSettings

}