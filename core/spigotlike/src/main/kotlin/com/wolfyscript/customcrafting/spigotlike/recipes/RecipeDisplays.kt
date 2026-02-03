package com.wolfyscript.customcrafting.spigotlike.recipes

import com.wolfyscript.customcrafting.core.recipes.CraftingFormula
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.core.recipes.RecipeReference
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

const val DISPLAY_RECIPE_PREFIX = "cc_display."

fun registerDisplayRecipes(recipes: Collection<RecipeReference<*>>) {
    for (recipe in recipes) {
        val display = recipe.toDisplay() ?: continue
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
    when (val recipe = value) {
        is CustomRecipeCrafting -> recipe.toDisplay(key)
    }
    return null
}

fun CustomRecipeCrafting.toDisplay(key: Key): CraftingRecipe? {
    when (val formula = this.formula) {
        is CraftingFormula.Shaped -> {
            val recipe = ShapedRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrapSpigot())
            recipe.shape(*formula.shape.rows.toTypedArray())

            for ((index, ingredientKey) in formula.shape.ingredientIndices.withIndex()) {
                val ingredient = formula.ingredients[index]
                recipe.setIngredient(
                    ingredientKey,
                    RecipeChoice.ExactChoice(ingredient.choices.all().map { it.create().unwrapSpigot() })
                )
            }
            return recipe
        }

        is CraftingFormula.Shapeless -> {
            val recipe = ShapelessRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrapSpigot())
            for (ingredient in formula.ingredients) {
                recipe.addIngredient(
                    RecipeChoice.ExactChoice(
                        ingredient.choices.all().map { it.create().unwrapSpigot() })
                )
            }

            return recipe
        }

        else -> {
            return null
        }
    }
}
