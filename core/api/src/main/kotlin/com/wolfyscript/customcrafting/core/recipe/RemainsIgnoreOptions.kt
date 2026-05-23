package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.ingredient.RemainsIgnoreOptionsImpl

interface RemainsIgnoreOptions {

    companion object {

        fun of(vanilla: Boolean = false, others: Boolean = false): RemainsIgnoreOptions =
            RemainsIgnoreOptionsImpl(vanilla, others)

    }

    val vanilla: Boolean

    val others: Boolean

}