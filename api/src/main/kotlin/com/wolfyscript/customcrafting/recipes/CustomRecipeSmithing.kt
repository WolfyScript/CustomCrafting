package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface CustomRecipeSmithing : CustomRecipe {

    override val type: RecipeType<CustomRecipeSmithing>
        get() = RecipeTypes.smithing

    /**
     * The template required to upgrade the [base]
     */
    val template: Ingredient?

    /**
     * The base item to upgrade with the [addition]
     */
    val base: Ingredient?

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

    fun evaluate(context: EvaluationContext, templateStack: ItemStack?, baseStack: ItemStack?, additionStack: ItemStack?): RecipeData<CustomRecipeSmithing>?

    interface CopyOptions {

        /**
         * List of data components to copy from the base item to the result.
         */
        val preserveComponents: List<Key>

    }

}