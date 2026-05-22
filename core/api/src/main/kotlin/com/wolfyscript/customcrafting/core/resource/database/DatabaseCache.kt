package com.wolfyscript.customcrafting.core.resource.database

import org.jetbrains.exposed.v1.jdbc.Database

/**
 * Cache databases that are already used, so destinations using the same database use just one connection.
 */
internal object DatabaseCache {

    val connections: MutableMap<DatabaseInfo, Database> = mutableMapOf()

}