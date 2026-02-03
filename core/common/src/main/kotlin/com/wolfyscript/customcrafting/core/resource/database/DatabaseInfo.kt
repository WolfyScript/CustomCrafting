package com.wolfyscript.customcrafting.core.resource.database

data class DatabaseInfo(
    val jdbcConnectionURL: String,
    val driver: String,
    val user: String = "",
)
