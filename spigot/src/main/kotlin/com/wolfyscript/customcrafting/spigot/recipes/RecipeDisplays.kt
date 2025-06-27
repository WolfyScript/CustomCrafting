package com.wolfyscript.customcrafting.spigot.recipes

import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

const val DISPLAY_RECIPE_PREFIX = "cc_placeholder."

fun registerDisplayRecipes(recipes: Collection<RecipeReference<*>>) {
    for (recipe in recipes) {
        val display = recipe.toDisplay()
        if (display == null) {
            continue
        }
        if (Bukkit.getRecipe((display as Keyed).key) != null) {
            Bukkit.removeRecipe((display as Keyed).key)
        }
        Bukkit.addRecipe(display)
    }
}

fun Recipe.isDisplay(): Boolean {
    return (this as Keyed).key.key.startsWith(DISPLAY_RECIPE_PREFIX)
}

fun Key.toDisplayRecipeKey(): NamespacedKey {
    return NamespacedKey(this.namespace, "$DISPLAY_RECIPE_PREFIX${this.value}")
}

fun RecipeReference<*>.toDisplay(): Recipe? {
    val recipe = value
    when (recipe) {
        is CustomRecipeCrafting -> recipe.toDisplay(key)
    }
    return null
}

fun CustomRecipeCrafting.toDisplay(key: Key): CraftingRecipe? {
    val formula = this.formula
    when (formula) {
        is CraftingFormula.Shaped -> {
            val recipe = ShapedRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrap())
            recipe.shape(*formula.shape.rows.toTypedArray())

            for ((index, ingredientKey) in formula.shape.ingredientIndices.withIndex()) {
                val ingredient = formula.ingredients[index]
                recipe.setIngredient(
                    ingredientKey,
                    RecipeChoice.ExactChoice(ingredient.choices.all().map { it.create().unwrap() })
                )
            }
            return recipe
        }

        is CraftingFormula.Shapeless -> {
            val recipe = ShapelessRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrap())
            for (ingredient in formula.ingredients) {
                recipe.addIngredient(
                    RecipeChoice.ExactChoice(
                        ingredient.choices.all().map { it.create().unwrap() })
                )
            }

            return recipe
        }

        else -> {
            return null
        }
    }
}
