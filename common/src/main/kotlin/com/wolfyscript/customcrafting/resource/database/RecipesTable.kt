package com.wolfyscript.customcrafting.resource.database

import com.fasterxml.jackson.module.kotlin.readValue
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json

/**
 * A table of recipes associated with a directory and name
 */
object RecipesTable : Table("recipes") {
    val dir = varchar("dir", 255)
    val name = varchar("name", 255)
    val config = json(
        "config",
        { CustomCraftingProvider.get().server!!.resourceManager.jacksonObjectMapper.writeValueAsString(it) },
        { CustomCraftingProvider.get().server!!.resourceManager.jacksonObjectMapper.readValue<CustomRecipe<*,*>>(it) })

    override val primaryKey = PrimaryKey(dir, name)

}