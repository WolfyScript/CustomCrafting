package com.wolfyscript.customcrafting.fabric.inject

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.api.CustomCraftingFabric
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomBlastingRecipeProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomCampfireRecipeProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomRecipeShapedProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomRecipeShapelessProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomSmeltingRecipeProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomSmithingRecipeProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomSmokingRecipeProxy
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomStonecutterRecipeProxy
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeSmithing
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.scafall.wrappers.unwrap
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.RecipeHolder

object RecipeRegistrationUtils {

    fun registerProxyRecipes(): Collection<RecipeHolder<*>> {
        val list = mutableListOf<RecipeHolder<*>>()
        val customCrafting = CustomCraftingProvider.get() as CustomCraftingFabric
        customCrafting.logger.info("  proxy recipes:")
        customCrafting.server?.recipeManager?.recipes()?.forEach {
            customCrafting.logger.info("  -> ${it.key}")
            val proxyRecipe = it.toVanillaProxyRecipe()
            proxyRecipe.forEach { recipe ->
                customCrafting.logger.info("  proxy: ${recipe.id().identifier()}")
                list.add(recipe)
            }
        }
        return list
    }

    fun removeExistingProxyRecipes(recipes: Collection<RecipeHolder<*>>) : MutableCollection<RecipeHolder<*>> {
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
                        val key = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(key.namespace, "${key.value}_$index"))
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