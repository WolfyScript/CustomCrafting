package com.wolfyscript.customcrafting.resource.database

data class DatabaseInfo(
    val jdbcConnectionURL: String,
    val driver: String,
    val user: String = "",
)
