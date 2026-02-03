package com.wolfyscript.customcrafting.spigotlike

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.core.commands.CCCommands
import com.wolfyscript.customcrafting.recipes.IngredientManagerCommon
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientManager
import com.wolfyscript.customcrafting.resource.ResourceManager
import com.wolfyscript.customcrafting.core.resource.ResourceManagerCommon
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.customcrafting.spigotlike.recipes.registerCommonRecipeListeners
import com.wolfyscript.customcrafting.spigotlike.recipes.registerDisplayRecipes
import com.wolfyscript.customcrafting.spigotlike.recipes.registerPlaceholderRecipes
import com.wolfyscript.scafall.ScafallProvider
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class CustomCraftingServerSpigotLike(val customCrafting: CustomCrafting, val plugin: Plugin) : CustomCraftingServer {

    override val resourceManager: ResourceManager = ResourceManagerCommon(customCrafting, plugin.dataFolder)
    override val ingredientManager: IngredientManager = IngredientManagerCommon(customCrafting)
    override val recipeManager: RecipeManager = RecipeManagerCommon(customCrafting)

    init {
        resourceManager.resourceLoader.registerListener(recipeManager)
    }

    override fun onLoad() {
        ScafallProvider.get().dependencyManager.onAllDependenciesInitialized {
            resourceManager.loadResources()
            registerPlaceholderRecipes(recipeManager.recipes())
            registerDisplayRecipes(recipeManager.recipes())
        }

        ScafallProvider.get().server?.minecraftServer?.commands?.dispatcher?.let {
            CCCommands.registerCommands(it)
        }

        Bukkit.getPluginManager().registerCommonRecipeListeners(plugin, customCrafting)
    }

    override fun onUnload() {

    }


}