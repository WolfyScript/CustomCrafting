package com.wolfyscript.customcrafting.configuration

import com.wolfyscript.customcrafting.configuration.gui.RecipeBookSettings
import com.wolfyscript.customcrafting.configuration.mechanics.GameMechanicSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings

interface ConfigurationManager {

    fun load()

    val resourceSettings: ResourceSettings

    val gameMechanicSettings: GameMechanicSettings

    val recipeBookSettings: RecipeBookSettings

}