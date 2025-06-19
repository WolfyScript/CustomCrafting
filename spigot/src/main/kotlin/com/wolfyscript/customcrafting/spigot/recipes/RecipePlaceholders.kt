package com.wolfyscript.customcrafting.spigot.recipes

import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

const val PLACEHOLDER_RECIPE_PREFIX = "cc_placeholder."

fun registerPlaceholderRecipes(recipes: Collection<CustomRecipe<*, *>>) {
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
    return NamespacedKey(this.namespace, "$PLACEHOLDER_RECIPE_PREFIX${this.value}")
}

fun CustomRecipe<*, *>.toPlaceholder(): Recipe? {
    return when (this) {
        is CustomRecipeCrafting -> toPlaceholder()
        is CustomRecipeCooking -> toPlaceholder()
        else -> null
    }
}

fun CustomRecipeCooking.toPlaceholder(): Recipe? {
    val key = CustomCraftingRegistryTypes.customRecipes.resolveOrThrow().getKey(this)
    if (key == null) {
        return null
    }

    val processing = this.processing
    val spigotResult = result.choices.all().first().create().unwrap()
    val sourceChoices = processing.source.toMaterialChoice()
    return when (processing) {
        is CustomRecipeCooking.WorkstationProcessing.Smelting -> FurnaceRecipe(
            key.toPlaceholderRecipeKey(),
            spigotResult,
            sourceChoices,
            xp,
            processing.processingTime
        )

        is CustomRecipeCooking.WorkstationProcessing.Blasting -> BlastingRecipe(
            key.toPlaceholderRecipeKey(),
            spigotResult,
            sourceChoices,
            xp,
            processing.processingTime
        )

        is CustomRecipeCooking.WorkstationProcessing.Smoking -> SmokingRecipe(
            key.toPlaceholderRecipeKey(),
            spigotResult,
            sourceChoices,
            xp,
            processing.processingTime
        )

        is CustomRecipeCooking.WorkstationProcessing.Campfire -> null // do not register Campfire recipes! Not necessary for function and cannot cancel conflicting vanilla behaviour!

        else -> null
    }
}

private fun Ingredient.toMaterialChoice(): RecipeChoice.MaterialChoice {
    return RecipeChoice.MaterialChoice(choices.all().map { it.create().unwrap().type })
}

fun CustomRecipeCrafting.toPlaceholder(): CraftingRecipe? {
    val key = CustomCraftingRegistryTypes.customRecipes.resolveOrThrow().getKey(this)
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
                recipe.setIngredient(ingredientKey, ingredient.toMaterialChoice())
            }
            return recipe
        }

        is CraftingFormula.Shapeless -> {
            val recipe = ShapelessRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrap())
            for (ingredient in formula.ingredients) {
                recipe.addIngredient(ingredient.toMaterialChoice())
            }

            return recipe
        }

        else -> {
            return null
        }
    }
}

