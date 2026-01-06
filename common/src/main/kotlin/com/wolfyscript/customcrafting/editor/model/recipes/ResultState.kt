package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultActionState
import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultModifierState
import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.RecipeChoicesImpl
import com.wolfyscript.customcrafting.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.recipes.RecipeItemModifierImpl
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.RecipeResultImpl
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

class ResultModelImpl : ResultModel {

    override val stacks: MutableList<ScafallItemStack> = mutableListOf()
    override val tags: MutableList<Key> = mutableListOf()
    override val actions: MutableList<ResultActionState<*>> = mutableListOf()
    override val modifier: ResultModifierState = ResultModifierStateImpl()

    override fun complete(): Result<RecipeResult> {
        if (stacks.isEmpty() && tags.isEmpty()) {
            return Result.failure(IllegalArgumentException("Result must have at least one stack or tag."))
        }
        val stackRefs = mutableListOf<ItemStackRef>()
        for (stack in stacks) {
            stackRefs.add(ItemStackRef.parse(stack) ?: ItemStackRef.create(stack))
        }

        // TODO

        return Result.success(
            RecipeResultImpl(
                choices = RecipeChoicesImpl(
                    stackRefs, tags
                ),
                modifier = RecipeItemModifierImpl(),
                actions = mutableListOf(),
                bulkActions = mutableListOf(),
                alwaysKeepPrevious = false
            )
        )
    }
}

class ResultModifierStateImpl : ResultModifierState {

    override val transformations: MutableList<RecipeItemModifier.Transformation> = mutableListOf()

}