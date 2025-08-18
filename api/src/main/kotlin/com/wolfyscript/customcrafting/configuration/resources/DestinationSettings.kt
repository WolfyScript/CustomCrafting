package com.wolfyscript.customcrafting.configuration.resources

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.resource.ResourceLoader
import kotlin.io.path.Path

/**
 * Settings for a destination to save resources to and load resources from.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSubTypes(
    JsonSubTypes.Type(value = DestinationSettings.SQLDestinationSettings::class, name = "sql"),
    JsonSubTypes.Type(value = DestinationSettings.DirectoryDestinationSettings::class, name = "directory")
)
@JsonPropertyOrder(value = ["type"])
interface DestinationSettings {

    /**
     * Optional filter to specify which resources to save to this destination.
     * If not specified, all resources will be saved to this destination.
     */
    val filter: FilterSettings?

    /**
     * Whether to overwrite existing resources by resources from this destination.
     */
    val overwriteExisting: Boolean

    /**
     * Whether resources saved to this destination should propagate to destinations of lower priority.
     */
    val propagateSavedResources: Boolean

    /**
     * Optional setting to use this destination as a backup destination.
     * Backups are done before updates and resource upgrades.
     *
     * **Backup destinations are not used to load resources!**
     * **They are only used to save resources! Filter settings still apply!**
     */
    val backup: BackupSettings?

    fun configureDestination(customCrafting: CustomCrafting, resourceLoader: ResourceLoader): ResourceLoader.Destination

    interface DirectoryDestinationSettings : DestinationSettings {

        /**
         * An optional path to the resource directory.
         */
        val path: String?

    }

    /**
     * Defines a Destination for an SQL database.
     */
    interface SQLDestinationSettings : DestinationSettings {

        val connection: DatabaseConnectionType

        /**
         * The type of database connection that is used.
         * It supports the following SQL databases:
         * - H2
         * - MariaDB
         * - MySQL
         * - Oracle
         * - PostgreSQL
         * - SQL Server
         * - SQLite
         *
         */
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        @JsonSubTypes(
            JsonSubTypes.Type(value = DatabaseConnectionType.H2::class, name = "h2"),
            JsonSubTypes.Type(value = DatabaseConnectionType.MariaDB::class, name = "mariadb"),
            JsonSubTypes.Type(value = DatabaseConnectionType.MySQL::class, name = "mysql"),
            JsonSubTypes.Type(value = DatabaseConnectionType.OracleDB::class, name = "oracle"),
            JsonSubTypes.Type(value = DatabaseConnectionType.PostgreSQL::class, name = "postgresql"),
            JsonSubTypes.Type(value = DatabaseConnectionType.MSSQLServer::class, name = "mssqlserver"),
            JsonSubTypes.Type(value = DatabaseConnectionType.SQLite::class, name = "sqlite"),
        )
        @JsonPropertyOrder(value = ["type"])
        interface DatabaseConnectionType {

            val user: String

            val password: String

            val jdbcUrl: String

            val driver: String

            class H2(
                val path: String,
                override val user: String = "",
                override val password: String = "",
            ) : DatabaseConnectionType {
                override val jdbcUrl: String
                    get() {
                        val finalPath = if (path.startsWith("/")) {
                            path
                        } else {
                            Path(CustomCraftingProvider.get().resourceManager.resourceLoader.directory.path, path)
                        }
                        return "jdbc:h2:$finalPath"
                    }
                override val driver: String = "org.h2.Driver"
            }

            class MariaDB(
                host: String,
                database: String,
                override val user: String,
                override val password: String,
            ) : DatabaseConnectionType {
                override val jdbcUrl: String = "jdbc:mariadb://$host/$database"
                override val driver: String = "org.mariadb.jdbc.Driver"
            }

            class MySQL(
                host: String,
                database: String,
                override val user: String,
                override val password: String,
            ) : DatabaseConnectionType {
                override val jdbcUrl: String = "jdbc:mysql://$host/$database"
                override val driver: String = "com.mysql.cj.jdbc.Driver"
            }

            class OracleDB(
                host: String,
                database: String,
                override val user: String,
                override val password: String,
            ) : DatabaseConnectionType {
                override val jdbcUrl: String = "jdbc:oracle:thin:@$host/$database"
                override val driver: String = "oracle.jdbc.OracleDriver"
            }

            class PostgreSQL(
                host: String,
                database: String,
                override val user: String,
                override val password: String,
            ) : DatabaseConnectionType {
                override val jdbcUrl: String = "jdbc:postgresql://$host/$database"
                override val driver: String = "org.postgresql.Driver"
            }

            class MSSQLServer(
                host: String,
                database: String,
                override val user: String,
                override val password: String,
            ) : DatabaseConnectionType {
                override val jdbcUrl: String = "jdbc:sqlserver://$host;databaseName=$database"
                override val driver: String = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
            }

            class SQLite(
                val path: String,
                override val user: String = "",
                override val password: String = "",
            ) : DatabaseConnectionType {
                override val jdbcUrl: String
                    get() {
                        val finalPath = if (path.startsWith("/")) {
                            path
                        } else {
                            Path(CustomCraftingProvider.get().resourceManager.resourceLoader.directory.path, path)
                        }
                        return "jdbc:sqlite:$finalPath"
                    }
                override val driver: String = "org.sqlite.JDBC"
            }

        }

    }

    interface FilterSettings {

        /**
         * Which resources to include in this destination.
         * Includes everything when not specified.
         */
        val includes: FilterEntry?

        /**
         * Which resources to exclude from this destination.
         */
        val excludes: FilterEntry?

        interface FilterEntry {

            /**
             * Namespaces that are filtered.
             */
            val namespaces: List<String>

            /**
             * Resource paths that are filtered.
             * Emtpy String means root path (recipes without a parent directory)
             *
             * **Does not include subdirectories!**
             * Only the direct children of the specified path are filtered!
             *
             * `<namespace>:<**This path here**>/recipe`
             */
            val paths: List<String>

            /**
             * Resources matching the specified regex will be filtered.
             */
            val regex: List<String>

        }

    }

    interface BackupSettings {

        // TODO
        val compress: Boolean

    }

}