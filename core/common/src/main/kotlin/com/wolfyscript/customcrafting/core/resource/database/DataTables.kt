package com.wolfyscript.customcrafting.core.resource.database

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.resource.DataType
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json

object DataTables {

    private val tables: MutableMap<DataType<*>, JsonValueTable<*>> = HashMap()

    fun <T: Any> getTable(type: DataType<T>): JsonValueTable<T>? {
        return tables.getOrPut(type) {
            JsonValueTable(type.id, type.classType)
        } as JsonValueTable<T>?
    }

}

open class JsonValueTable<T: Any>(tableName: String, type: Class<T>) : Table(tableName) {

    val dir = varchar("dir", 255)
    val name = varchar("name", 255)
    val config = json(
        "config",
        { CustomCraftingProvider.get().server!!.resourceManager.jacksonObjectMapper.writeValueAsString(it) },
        { CustomCraftingProvider.get().server!!.resourceManager.jacksonObjectMapper.readValue(it, type) })

    override val primaryKey = PrimaryKey(dir, name)

}
