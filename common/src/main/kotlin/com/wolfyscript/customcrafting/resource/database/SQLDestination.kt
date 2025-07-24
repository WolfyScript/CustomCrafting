package com.wolfyscript.customcrafting.resource.database

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.resource.AbstractDestination
import com.wolfyscript.customcrafting.resource.DestinationFilter
import com.wolfyscript.customcrafting.resource.LoadedRecipe
import com.wolfyscript.customcrafting.resource.ResourceLoader
import com.wolfyscript.customcrafting.resource.ResourceLoaderImpl
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.compat.DependencyResolver
import com.wolfyscript.scafall.identifier.Key
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SQLDestination(customCrafting: CustomCrafting, resourceLoader: ResourceLoader, settings: DestinationSettings.SQLDestinationSettings) :
    AbstractDestination<DestinationSettings.SQLDestinationSettings>(customCrafting, resourceLoader as ResourceLoaderImpl, settings) {

    override val filter: ResourceLoader.Destination.Filter? =
        settings.filter?.let { DestinationFilter(customCrafting, it) }

    private fun getOrCreateDBConnection(): Database {
        val connector = settings.connection
        val databaseInfo = DatabaseInfo(connector.jdbcUrl, connector.driver, connector.user)

        var database = DatabaseCache.connections[databaseInfo]
        if (database == null) {
            database = Database.connect(connector.jdbcUrl, connector.driver, user = connector.user, password = connector.password)
            DatabaseCache.connections[databaseInfo] = database
        }
        return database
    }

    override fun load(accept: (LoadedRecipe) -> Unit) {
        transaction(getOrCreateDBConnection()) {
            SchemaUtils.create(RecipesTable)

            RecipesTable.selectAll().forEach {
                var dir = it[RecipesTable.dir]
                var key = it[RecipesTable.name]
                if (dir.endsWith('/')) {
                    dir = dir.substring(0, dir.length - 1)
                }
                if (key.startsWith('/')) {
                    key = key.substring(1)
                }

                val recipeKey = Key.key(Key.CUSTOMCRAFTING_NAMESPACE, "$dir/$key")
                val recipe = it[RecipesTable.config]
                val loadedRecipe = ResourceLoaderImpl.LoadedRecipeImpl(recipeKey, recipe, DependencyResolver.resolveDependenciesFor(recipe, recipe::class.java))
                accept(loadedRecipe)
            }
        }
    }

    override fun save(key: Key, recipe: CustomRecipe<*, *>): Result<Boolean> {
        val dir = key.value.substringBeforeLast("/")
        val name = key.value.substringAfterLast("/")

        transaction(getOrCreateDBConnection()) {
            RecipesTable.insert {
                it[RecipesTable.dir] = dir
                it[RecipesTable.name] = name
                it[config] = recipe
            }

        }

        return Result.success(true)
    }

    override fun delete(key: Key, recipe: CustomRecipe<*, *>): Result<Boolean> {
        val dir = key.value.substringBeforeLast("/")
        val name = key.value.substringAfterLast("/")

        transaction(getOrCreateDBConnection()) {
            val removed = RecipesTable.deleteWhere {
                (RecipesTable.dir eq dir) and (RecipesTable.name eq name)
            }

            if (removed > 0) {
                return@transaction Result.success(true)
            }
        }

        return Result.success(false)
    }

}
