package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.process.ProcessRepairing

/**
 * Recipe used to repair items in the Anvil
 */
interface CustomRecipeRepairing : CustomRecipe<com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.RepairingRecipeInput, com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.RepairingRecipeData> {

    override val type: RecipeType<CustomRecipeRepairing>
        get() = RecipeTypes.repairing.resolveOrThrow()

    /**
     * The base ingredient, the first slot in the Anvil menu (the item to repair/enchant)
     */
    val base: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

    /**
     * The addition ingredient, the second slot in the Anvil menu (the item to sacrifice for repair/enchanting)
     */
    val addition: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient?

    /**
     * The process to produce the result in the Anvil menu.
     * This is processed **after** the recipe has been evaluated.
     */
    val process: com.wolfyscript.customcrafting.core.recipes.process.ProcessRepairing

    override fun evaluate(
        input: com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.RepairingRecipeData?

}