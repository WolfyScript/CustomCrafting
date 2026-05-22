package com.wolfyscript.customcrafting.core.configuration

import com.wolfyscript.customcrafting.core.configuration.cli.CLISettings
import com.wolfyscript.customcrafting.core.configuration.gui.GUISettings
import com.wolfyscript.customcrafting.core.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.core.configuration.resources.ResourceSettings

/**
 * Handles the configurations of all the different plugin modules.
 */
interface ConfigurationManager {

    fun load()

    val resourceSettings: ResourceSettings

    val gameMechanicSettings: GameMechanicSettings

    val guiSettings: GUISettings

    val cliSettings: CLISettings

}