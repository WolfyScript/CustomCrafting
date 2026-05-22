package com.wolfyscript.customcrafting.core.resource.database

internal data class DatabaseInfo(
    val jdbcConnectionURL: String,
    val driver: String,
    val user: String = "",
)
