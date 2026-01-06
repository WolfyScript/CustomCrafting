package com.wolfyscript.customcrafting.editor.model.recipes.result

import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

interface ResultModel {

    val stacks: MutableList<ScafallItemStack>

    val tags: MutableList<Key>

    val actions: List<ResultActionState<*>>

    val modifier: ResultModifierState

    fun complete(): Result<RecipeResult>

}