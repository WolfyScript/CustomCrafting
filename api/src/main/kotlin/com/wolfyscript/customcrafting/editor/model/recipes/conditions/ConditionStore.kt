package com.wolfyscript.customcrafting.editor.model.recipes.conditions

import com.wolfyscript.customcrafting.recipes.conditions.Condition

/**
 * Stores the settings for a condition in the editor.
 */
interface ConditionStore<T: Condition> {

    /**
     * Completes the condition and validates it.
     *
     * @return A Result containing the completed condition or an error if the condition is invalid.
     */
    fun complete() : Result<T>

}