package com.wolfyscript.customcrafting.ui.editor.home

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.viewportl.gui.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class EditorHomeStore(val viewer: UUID) : Store() {

    internal val recipeTypeSpecificStates = EditorRegistryTypes.recipeTypeSpecificModelFactories.resolveOrThrow()

    private val homeStateFlow = MutableStateFlow(HomeState(recipeTypeSpecificStates.map { it.recipeType }))
    val homeState: StateFlow<HomeState> = homeStateFlow.asStateFlow()

    fun selectRecipeType(recipeType: RecipeType<*>) {
        CustomCraftingProvider.get().server?.recipeEditor?.getOrCreateSession(viewer)?.fold(
            {
                it.create(recipeType)
            }
        ){
            // Do nothing for now
        }
    }

}