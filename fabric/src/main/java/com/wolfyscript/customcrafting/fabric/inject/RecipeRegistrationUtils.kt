package com.wolfyscript.customcrafting.fabric.inject

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.api.CustomCraftingFabric
import com.wolfyscript.customcrafting.fabric.recipes.proxy.toVanilla
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.RecipeReference
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.RecipeHolder

object RecipeRegistrationUtils {

    fun registerProxyRecipes(): Collection<RecipeHolder<*>> {
        val list = mutableListOf<RecipeHolder<*>>()
        val customCrafting = CustomCraftingProvider.get() as CustomCraftingFabric
        customCrafting.logger.info("  proxy recipes: ${customCrafting.recipeManager.index.values()}")
        customCrafting.recipeManager.index.values().forEach {
            customCrafting.logger.info("  -> ${it.key}")
            if (it.value is CustomRecipeCrafting) {
                val proxyRecipe = (it as RecipeReference<CustomRecipeCrafting>).toVanilla() ?: return@forEach
                val key = ResourceKey.create(Registries.RECIPE, it.key.toMc())

                customCrafting.logger.info("  proxy: $key")
                list.add(RecipeHolder(key, proxyRecipe))
            }
        }
        return list
    }

    fun removeExistingProxyRecipes(recipes: Collection<RecipeHolder<*>>) : MutableCollection<RecipeHolder<*>> {
        val mutableRecipes = recipes.toMutableList()
        mutableRecipes.removeAll { it.value is ProxyRecipe }
        return mutableRecipes
    }

}