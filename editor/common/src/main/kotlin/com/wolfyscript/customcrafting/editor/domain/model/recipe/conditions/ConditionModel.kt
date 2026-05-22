package com.wolfyscript.customcrafting.editor.domain.model.recipe.conditions

import com.wolfyscript.customcrafting.core.recipe.condition.Condition

/**
 * Stores the settings for a condition in the editor.
 */
interface ConditionModel<T: Condition> {

    /**
     * Completes the condition and validates it.
     *
     * @return A Result containing the completed condition or an error if the condition is invalid.
     */
    fun complete() : Result<T>

}