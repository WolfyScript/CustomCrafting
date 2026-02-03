package com.wolfyscript.customcrafting.fabric

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.fabric.inject.RecipeManagerCustomRecipesExt
import com.wolfyscript.customcrafting.recipes.IngredientManagerCommon
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientManager
import com.wolfyscript.customcrafting.resource.ResourceManager
import com.wolfyscript.customcrafting.core.resource.ResourceManagerCommon
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import java.io.File

class CustomCraftingServerFabric(customCrafting: CustomCrafting, val minecraftServer: MinecraftServer) :
    CustomCraftingServer {

    override val resourceManager: ResourceManager = ResourceManagerCommon(
        customCrafting,
        File(FabricLoader.getInstance().configDir.toFile(), Key.CUSTOMCRAFTING_NAMESPACE)
    )
    override val ingredientManager: IngredientManager = IngredientManagerCommon(customCrafting)
    override val recipeManager: RecipeManagerCommon = RecipeManagerCommon(customCrafting)

    init {
        resourceManager.resourceLoader.registerListener(recipeManager)
    }

    override fun onLoad() {
        ScafallProvider.get().dependencyManager.onAllDependenciesInitialized {
            resourceManager.loadResources()
        }

        (minecraftServer.recipeManager as RecipeManagerCustomRecipesExt).registerProxyRecipes()
    }

    override fun onUnload() {

    }
}