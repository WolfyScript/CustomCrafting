package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key

interface CustomRecipeSmithing : CustomRecipe<RecipeInput.SmithingRecipeInput, RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeSmithing>
        get() = RecipeTypes.smithing.resolveOrThrow()

    /**
     * The template required to upgrade the [base]
     */
    val template: Ingredient?

    /**
     * The base item to upgrade with the [addition].
     * A base ingredient is required and cannot be empty!
     */
    val base: Ingredient

    /**
     * The addition with which to upgrade the [base]
     */
    val addition: Ingredient?

    val result: RecipeResult

    /**
     * The options specifying how ItemStack components are copied from the base into the result.
     *
     * Optional; when not specified, then the vanilla behaviour is used. (copying all components)
     */
    val copyOptions: CopyOptions?

    interface CopyOptions {

        /**
         * List of data components to copy from the base item to the result.
         */
        val preserveComponents: List<Key>

        /**
         * List of data components to not copy from the base to the result stack
         */
        val excludeComponents: List<Key>

    }

}