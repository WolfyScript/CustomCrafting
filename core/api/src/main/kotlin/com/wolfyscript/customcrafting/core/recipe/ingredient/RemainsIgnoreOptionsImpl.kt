package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.recipe.RemainsIgnoreOptions

internal data class RemainsIgnoreOptionsImpl(override val vanilla: Boolean, override val others: Boolean) :
    RemainsIgnoreOptions {

    override fun toString(): String {
        return "(vanilla=$vanilla, others=$others)"
    }
}