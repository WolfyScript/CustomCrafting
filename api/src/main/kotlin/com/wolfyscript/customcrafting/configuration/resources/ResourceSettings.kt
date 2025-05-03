package com.wolfyscript.customcrafting.configuration.resources

interface ResourceSettings {

    /**
     * A list of destinations to save resources to and load resources from.
     *
     * The list also specifies the order and therefore priority of destinations.
     *
     * The order in which the resources are loaded from destinations is equal to the order specified in this list.
     * Destinations may overwrite resources loaded from other destinations that were loaded beforehand.
     *
     * The same applies to saving resources.
     * So resources are first stored at the first destination and then to the next, etc.
     * A destination can block resources stored to it to propagate to the next.
     *
     */
    val destinations: List<DestinationSettings>

}

