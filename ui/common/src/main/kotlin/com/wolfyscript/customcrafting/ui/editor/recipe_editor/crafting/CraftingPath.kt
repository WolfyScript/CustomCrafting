package com.wolfyscript.customcrafting.ui.editor.recipe_editor.crafting

import com.wolfyscript.viewportl.gui.elements.NavKey

/**
 * Navigation Paths in the Crafting Recipe Editor
 */
interface CraftingPath : NavKey {

    val name: String

    /**
     * UI for simple recipes with simple ingredients and results.
     * (similar to legacy UI)
     *
     */
    interface Simple : CraftingPath {

        companion object {

            val values = listOf(
                Formula, Result, ExtraProperties, Saving
            )

        }

        /**
         * Configures the formula of the recipe.
         *
         * Place stacks into a grid of slots.
         * **Ingredients cannot be edited!**
         *
         * Toggle shaped/shapeless
         *
         * Mirror options
         *
         * Trim options
         */
        object Formula : Simple {
            override val name: String = "Formula"
        }

        /**
         * Configure Result.
         *
         * Choices, Persistency
         */
        object Result : Simple {
            override val name: String = "Result"
        }

        /**
         * Extra properties that do not fit into other categories, or are common across recipe types.
         *
         * Recipe Group, Priority, etc.
         */
        object ExtraProperties : Simple {
            override val name: String = "Common Settings"
        }

        /**
         * Saving/Exporting the recipe.
         *
         * Name, Directory, Save As, etc.
         */
        object Saving : Simple {
            override val name: String = "Save"
        }

    }

    /**
     * UI for complex recipes or complex ingredients or results.
     *
     * This UI allows to configure each ingredient with extra options.
     *
     *
     */
    interface Advanced : CraftingPath {

        companion object {
            val values = listOf(
                AddIngredients, Formula, Result, Conditions, ExtraProperties, Saving
            )
        }

        /**
         * Accumulates the ingredients to be used in the [Formula] step.
         *
         * Ingredients may be selected from either existing saved ingredients,
         * or can be created and configured after placing in the initial stack.
         * (similar to the legacy shift+right-click on ingredients, but with separate buttons for those functions)
         *
         */
        object AddIngredients : Advanced {
            override val name: String = "Ingredients"
        }

        /**
         * Assigns the ingredients from the [AddIngredients] to specific slots
         * in the recipe formula.
         *
         * An ingredient from [AddIngredients] may be used multiple times in different slots.
         *
         * Toggle between shaped and shapeless
         *
         * Configure mirror options
         */
        object Formula : Advanced {
            override val name: String = "Formula"
        }

        /**
         * Configures the result with advanced features.
         *
         * Result persistency, choices, actions, and modifiers.
         */
        object Result : Advanced {
            override val name: String = "Result"
        }

        /**
         * Configures the conditions of the recipe
         */
        object Conditions : Advanced {
            override val name: String = "Conditions"
        }

        /**
         * Extra properties that do not fit into other categories, or are common across recipe types.
         *
         * Recipe Group, Priority, etc.
         */
        object ExtraProperties : Advanced {
            override val name: String = "Common Settings"
        }

        /**
         * Saving/Exporting the recipe.
         *
         * Name, Directory, Save As, etc.
         */
        object Saving : Advanced {
            override val name: String = "Save"
        }

    }

}