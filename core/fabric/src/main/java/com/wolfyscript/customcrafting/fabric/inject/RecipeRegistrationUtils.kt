package com.wolfyscript.customcrafting.fabric.inject

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipe.*
import com.wolfyscript.customcrafting.fabric.CustomCraftingFabric
import com.wolfyscript.customcrafting.fabric.recipes.proxy.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.RecipeHolder

object RecipeRegistrationUtils {

    fun collectProxyRecipes(): Collection<RecipeHolder<*>> {
        val customCrafting = CustomCraftingProvider.get() as CustomCraftingFabric
        return customCrafting.server?.recipeManager?.recipes()?.flatMap {
            val proxyRecipes = it.toVanillaProxyRecipe()
            customCrafting.logger.debug("  [{}] proxies: {}", it.key, proxyRecipes)
            proxyRecipes
        } ?: emptyList()
    }

    fun removeExistingProxyRecipes(recipes: Collection<RecipeHolder<*>>): MutableCollection<RecipeHolder<*>> {
        val mutableRecipes = recipes.toMutableList()
        mutableRecipes.removeAll { it.value is ProxyRecipe }
        return mutableRecipes
    }

    fun RecipeReference<*>.toVanillaProxyRecipe(): List<RecipeHolder<*>> {
        val recipe = this.value ?: return emptyList()
        val proxy = when (recipe) {
            is CustomRecipeCrafting -> {
                this as RecipeReference<CustomRecipeCrafting>
                val formula = recipe.formula
                when (formula) {
                    is CraftingFormula.Shaped -> CustomRecipeShapedProxy(this)
                    is CraftingFormula.Shapeless -> CustomRecipeShapelessProxy(this)
                    else -> null
                }
            }

            is CustomRecipeCooking -> {
                this as RecipeReference<CustomRecipeCooking>
                val processing = recipe.processing
                when (processing) {
                    is CustomRecipeCooking.WorkstationProcessing.Smelting -> CustomSmeltingRecipeProxy(this)
                    is CustomRecipeCooking.WorkstationProcessing.Smoking -> CustomSmokingRecipeProxy(this)
                    is CustomRecipeCooking.WorkstationProcessing.Blasting -> CustomBlastingRecipeProxy(this)
                    is CustomRecipeCooking.WorkstationProcessing.Campfire -> CustomCampfireRecipeProxy(this)
                }
            }

            is CustomRecipeSmithing -> {
                this as RecipeReference<CustomRecipeSmithing>
                CustomSmithingRecipeProxy(this)
            }

            is CustomRecipeStonecutting -> {
                this as RecipeReference<CustomRecipeStonecutting>
                recipe as CustomRecipeStonecutting

                if (recipe.flattenResult) {
                    return recipe.result.choices.all().mapIndexed { index, result ->
                        val key = ResourceKey.create(
                            Registries.RECIPE,
                            Identifier.fromNamespaceAndPath(key.namespace, "${key.value}_$index")
                        )
                        RecipeHolder(key, CustomStonecutterRecipeProxy(this, result.create().unwrap()))
                    }.toList()
                }

                CustomStonecutterRecipeProxy(this)
            }

            else -> null
        }
        if (proxy != null) {
            val key = ResourceKey.create(Registries.RECIPE, this.key.toMc())
            return listOf(RecipeHolder(key, proxy))
        }
        return emptyList()
    }

}