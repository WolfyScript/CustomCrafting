package com.wolfyscript.customcrafting.recipes.process

/**
 * Specifies how the name from the Anvil menu is applied to a result item.
 */
interface ProcedureRename {

    /**
     * Whether to use the MiniMessage parser to apply a formatted name with decorations and style.
     */
    val formatted: Boolean

    /**
     * An optional prefix to add before the name on the result
     */
    val prefix: String?

    /**
     * An optional suffix to add after the name on the result
     */
    val suffix: String?

    /**
     * The level cost of renaming
     *
     * Must be >= 1; Default: 1
     */
    val cost: Int

    /**
     * Whether to increase the repair cost for the result stack, even if it has only been renamed.
     *
     * Default: false
     */
    val increaseRepairCost: Boolean

}