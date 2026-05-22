package com.wolfyscript.customcrafting.core.resource.database

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.configuration.resources.SourceSettings
import com.wolfyscript.customcrafting.core.resource.DataType
import com.wolfyscript.customcrafting.core.resource.Source
import com.wolfyscript.customcrafting.core.resource.DestinationFilter
import com.wolfyscript.customcrafting.core.resource.LoadedObject
import com.wolfyscript.customcrafting.core.resource.ResourceLoaderImpl
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal class SQLSource(customCrafting: CustomCrafting, override val settings: SourceSettings.SQLSourceSettings) : Source {

    override val filter: Source.Filter? =
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

    override fun <T : Any> load(type: DataType<T>, accept: (value: LoadedObject<T>) -> Unit) {
        DataTables.getTable(type)?.let { table ->
            transaction(getOrCreateDBConnection()) {
                SchemaUtils.create(table)

                table.selectAll().forEach {
                    var dir = it[table.dir]
                    var key = it[table.name]
                    if (dir.endsWith('/')) {
                        dir = dir.dropLast(1)
                    }
                    if (key.startsWith('/')) {
                        key = key.substring(1)
                    }

                    val recipeKey = Key.key(Key.CUSTOMCRAFTING_NAMESPACE, "$dir/$key")
                    val recipe = it[table.config]
                    val loadedRecipe = ResourceLoaderImpl.LoadedObjectImpl(recipeKey, recipe)
                    accept(loadedRecipe)
                }
            }
        }
    }

    override fun <T: Any> save(
        type: DataType<T>,
        key: Key,
        value: T,
    ): Result<Boolean> {
        DataTables.getTable(type)?.let { table ->
            val dir = key.value.substringBeforeLast("/")
            val name = key.value.substringAfterLast("/")
            transaction(getOrCreateDBConnection()) {
                table.insert {
                    it[table.dir] = dir
                    it[table.name] = name
                    it[config] = value
                }

            }
            return Result.success(true)
        }
        return Result.failure(UnsupportedOperationException("Unsupported data type: $type"))
    }

    override fun delete(type: DataType<Any>, key: Key): Result<Boolean> {
        DataTables.getTable(type)?.let { table ->
            val dir = key.value.substringBeforeLast("/")
            val name = key.value.substringAfterLast("/")

            transaction(getOrCreateDBConnection()) {
                val removed = table.deleteWhere {
                    (table.dir eq dir) and (table.name eq name)
                }

                if (removed > 0) {
                    return@transaction Result.success(true)
                }
            }
            return Result.success(false)
        }

        return Result.failure(UnsupportedOperationException("Unsupported data type: $type"))
    }

}
