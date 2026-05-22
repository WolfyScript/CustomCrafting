package com.wolfyscript.customcrafting.fabric

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.recipe.RecipeManager
import com.wolfyscript.customcrafting.fabric.inject.RecipeManagerCustomRecipesExt
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientManager
import com.wolfyscript.customcrafting.core.resource.ResourceManager
import com.wolfyscript.customcrafting.core.server.CustomCraftingServer
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import java.io.File

class CustomCraftingServerFabric(customCrafting: CustomCrafting, val minecraftServer: MinecraftServer) :
    CustomCraftingServer {

    override val resourceManager: ResourceManager = ResourceManager.createNewForDir(
        customCrafting,
        File(FabricLoader.getInstance().configDir.toFile(), Key.CUSTOMCRAFTING_NAMESPACE)
    )
    override val ingredientManager: IngredientManager = IngredientManager.createNew(customCrafting)
    override val recipeManager: RecipeManager = RecipeManager.createNew(customCrafting)

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