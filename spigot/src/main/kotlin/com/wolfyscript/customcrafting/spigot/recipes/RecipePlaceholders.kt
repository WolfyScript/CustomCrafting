package com.wolfyscript.customcrafting.spigot.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
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

const val PLACEHOLDER_RECIPE_PREFIX = "cc_placeholder."

fun registerPlaceholderRecipes(recipes: Collection<CustomRecipe<*,*>>) {
    for (recipe in recipes) {
        val placeholder = recipe.toPlaceholder()
        if (placeholder == null) {
            continue
        }
        if (Bukkit.getRecipe((placeholder as Keyed).key) != null) {
            Bukkit.removeRecipe((placeholder as Keyed).key)
        }
        Bukkit.addRecipe(placeholder)
    }
}

fun Recipe.isPlaceholder(): Boolean {
    return (this as Keyed).key.key.startsWith(PLACEHOLDER_RECIPE_PREFIX)
}

fun Key.toPlaceholderRecipeKey(): NamespacedKey {
    return NamespacedKey(this.namespace, "$PLACEHOLDER_RECIPE_PREFIX$this")
}

fun CustomRecipe<*,*>.toPlaceholder(): Recipe? {
    when (this) {
        is CustomRecipeCrafting -> {
            return this.toPlaceholder()
        }
    }
    return null
}

fun CustomRecipeCrafting.toPlaceholder(): CraftingRecipe? {
    val key = CustomCraftingProvider.get().registries.customRecipes.getKey(this)
    if (key == null) {
        return null
    }
    val formula = this.formula
    when (formula) {
        is CraftingFormula.Shaped -> {
            val recipe = ShapedRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrap())
            recipe.shape(*formula.shape.rows.toTypedArray())
            for ((index, ingredientKey) in formula.shape.ingredientIndices.withIndex()) {
                val ingredient = formula.ingredients[index]
                recipe.setIngredient(
                    ingredientKey,
                    RecipeChoice.MaterialChoice(ingredient.choices.all().map { it.create().unwrap().type })
                )
            }
            return recipe
        }

        is CraftingFormula.Shapeless -> {
            val recipe = ShapelessRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrap())
            for (ingredient in formula.ingredients) {
                recipe.addIngredient(
                    RecipeChoice.MaterialChoice(
                        ingredient.choices.all().map { it.create().unwrap().type })
                )
            }

            return recipe
        }

        else -> {
            return null
        }
    }
}

