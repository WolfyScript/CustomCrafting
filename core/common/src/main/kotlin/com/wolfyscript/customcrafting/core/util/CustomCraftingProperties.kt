package com.wolfyscript.customcrafting.core.util

import java.util.Properties

/**
 * Object to handle the loading and accessing of properties from the vals.properties file.
 */
object CustomCraftingProperties {

    /**
     * Properties object to store the properties loaded from the vals.properties file.
     */
    val properties: Properties = Properties()

    init {
        properties.load(javaClass.classLoader.getResourceAsStream("com/wolfyscript/customcrafting/vals.properties"))
    }

    /**
     * The release version of the application.
     *
     * @return The release version as a string, or "unknown" if not found in the properties.
     */
    val release: String = properties.getProperty("release") ?: "unknown"

    /**
     * The Sentry DSN (Data Source Name) for error reporting.
     *
     * @return The Sentry DSN as a string, or an empty string if not found in the properties.
     */
    val sentryDsn: String = properties.getProperty("sentry.dsn") ?: ""

    /**
     * Whether Sentry error reporting is enabled.
     *
     * @return True if Sentry is enabled, false otherwise.
     */
    val sentryEnabled: Boolean = properties.getProperty("sentry.enabled").toBoolean()

}