package com.wolfyscript.customcrafting.resource.database

import com.fasterxml.jackson.module.kotlin.readValue
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json

/**
 * A table of recipes associated with a directory and name
 */
object RecipesTable : Table() {
    val id = integer("id").autoIncrement()
    val dir = varchar("dir", 255)
    val name = varchar("name", 255)
    val config = json(
        "config",
        { CustomCraftingProvider.get().dataManager.jacksonObjectMapper.writeValueAsString(it) },
        { CustomCraftingProvider.get().dataManager.jacksonObjectMapper.readValue<CustomRecipe<*,*>>(it) })

}