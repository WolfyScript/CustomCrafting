package com.wolfyscript.customcrafting.spigot.recipes

import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.wrappers.utils.unwrap
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.TransmuteResult
import net.minecraft.world.level.ItemLike
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import java.util.Optional

const val PLACEHOLDER_RECIPE_PREFIX = "cc_placeholder."

fun registerPlaceholderRecipes(recipes: Collection<RecipeReference<*>>) {
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

fun Key.toMcPlaceholderRecipeKey(): ResourceKey<net.minecraft.world.item.crafting.Recipe<*>> {
    return ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(this.namespace, "$PLACEHOLDER_RECIPE_PREFIX${this.value}"))
}

fun RecipeReference<*>.toPlaceholder(): Recipe? {
    val recipe = value
    return when (recipe) {
        is CustomRecipeCrafting -> recipe.toPlaceholder(key)
        is CustomRecipeCooking -> recipe.toPlaceholder(key)
        is CustomRecipeSmithing -> recipe.toPlaceholder(key)
        else -> null
    }
}

fun CustomRecipeCooking.toPlaceholder(key: Key): Recipe? {
    val processing = this.processing
    val spigotResult = result.choices.all().first().create().unwrapSpigot()
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
    return RecipeChoice.MaterialChoice(choices.all().map { it.create().unwrapSpigot().type })
}

fun CustomRecipeCrafting.toPlaceholder(key: Key): CraftingRecipe? {
    val formula = this.formula
    when (formula) {
        is CraftingFormula.Shaped -> {
            val recipe = ShapedRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrapSpigot())
            recipe.shape(*formula.shape.rows.toTypedArray())
            for ((index, ingredientKey) in formula.shape.ingredientIndices.withIndex()) {
                val ingredient = formula.ingredients[index]
                recipe.setIngredient(ingredientKey, ingredient.toMaterialChoice())
            }
            return recipe
        }

        is CraftingFormula.Shapeless -> {
            val recipe = ShapelessRecipe(key.toPlaceholderRecipeKey(), result.choices.all().first().create().unwrapSpigot())
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

fun Ingredient?.toMinecraft() : net.minecraft.world.item.crafting.Ingredient {
    if (this == null) {
        return net.minecraft.world.item.crafting.Ingredient.of(*emptyArray<ItemLike>())
    }
    if (matching is IngredientMatcher.Exact) {
        return net.minecraft.world.item.crafting.Ingredient.ofStacks(choices.all().map { it.create().unwrap() })
    }
    return  net.minecraft.world.item.crafting.Ingredient.of(HolderSet.direct(choices.all().map { it.create().unwrap().itemHolder }))
}

fun CustomRecipeSmithing.toPlaceholder(key: Key): SmithingTransformRecipe? {
    // Use Minecraft internals for now. Spigot/Paper API is too broken/different that I cannot properly use it.
    // Makes me think... why use the spigot API for the other recipes at all?
    val stack: ItemStack = result.choices.all().first().create().unwrap()

    val recipe = net.minecraft.world.item.crafting.SmithingTransformRecipe(
        Optional.ofNullable(template?.toMinecraft()),
        base.toMinecraft(),
        Optional.ofNullable(addition?.toMinecraft()),
        TransmuteResult(stack.itemHolder, stack.count, stack.componentsPatch)
    )
    ScafallProvider.get().server.minecraftServer.recipeManager.addRecipe(RecipeHolder(key.toMcPlaceholderRecipeKey(), recipe))

    return null
}

