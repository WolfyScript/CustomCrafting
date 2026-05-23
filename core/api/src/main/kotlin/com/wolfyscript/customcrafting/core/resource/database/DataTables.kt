package com.wolfyscript.customcrafting.core.resource.database

import com.wolfyscript.customcrafting.core.resource.DataType

internal object DataTables {

    private val tables: MutableMap<DataType<*>, JsonValueTable<*>> = HashMap()

    fun <T: Any> getTable(type: DataType<T>): JsonValueTable<T>? {
        return tables.getOrPut(type) {
            JsonValueTable(type.id, type.classType)
        } as JsonValueTable<T>?
    }

}

