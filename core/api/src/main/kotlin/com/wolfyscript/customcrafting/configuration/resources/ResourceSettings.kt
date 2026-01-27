package com.wolfyscript.customcrafting.configuration.resources

interface ResourceSettings {

    /**
     * A list of sources to load resources from and save resources to.
     *
     * The list also specifies the order and therefore priority of sources.
     *
     * The order in which the resources are loaded from sources is equal to the order specified in this list.
     * Sources may overwrite resources loaded from other sources that were loaded beforehand.
     *
     * The same applies to saving resources.
     * So resources are first stored at the first source and then to the next, etc.
     * A source can block resources stored to it to propagate to the next.
     *
     */
    val sources: List<SourceSettings>

    /**
     * Configures how the resources should be backed up.
     *
     * If omitted, then no backups will be created.
     */
    val backup: BackupSettings

}

