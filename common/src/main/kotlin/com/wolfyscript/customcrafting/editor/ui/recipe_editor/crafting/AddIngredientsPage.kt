package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.model.recipes.IngredientState
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.editor.model.recipes.RecipeCraftingState
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

private class AddIngredientStore(val viewer: UUID) : Store {

    private val ingredients: MutableStateFlow<List<IngredientState>> = MutableStateFlow(listOf())
    val ingredientCollection = ingredients.asStateFlow()

    private fun forCraftingState(fn: (RecipeCraftingState) -> Unit) {
        val session = CustomCraftingProvider.get().server?.recipeEditor?.getSession(viewer)
        if (session != null) {
            val state = session.state?.recipeState?.recipeTypeSpecificState as? RecipeCraftingState
                ?: error("Expected RecipeCraftingState, but was ${session.state?.recipeState?.recipeTypeSpecificState}")
            fn(state)
        }
    }

    fun addIngredient() {
        forCraftingState { state ->
            state.ingredientCollection.addNew()
            ingredients.value = state.ingredientCollection.ingredients.toList()
        }
    }

    fun addIngredient(ingredientState: IngredientState) {
        forCraftingState { state ->
            state.ingredientCollection.add(ingredientState)
            ingredients.value = state.ingredientCollection.ingredients.toList()
        }
    }

    fun removeIngredient(index: Int) {
        forCraftingState { state ->
            state.ingredientCollection.remove(index)
            ingredients.value = state.ingredientCollection.ingredients.toList()
        }
    }

}

@Composable
fun AddIngredientsPage() {
    val store = store(key = Key.customCrafting("ingredient")) {
        AddIngredientStore(it)
    }
    val ingredients by store.ingredientCollection.collectAsState()

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        for ((index, state) in ingredients.withIndex()) {
            IngredientSelector(
                index,
                state,
                onRemove = {
                    store.removeIngredient(index)
                }
            )
        }
        if (ingredients.size < 9) {
            Button(onClick = {
                store.addIngredient()
            }) {
                Icon(stack = ItemStack(Items.GREEN_CONCRETE).snapshot())
            }
        }
    }

}

@Composable
fun IngredientSelector(index: Int, ingredient: IngredientState, onRemove: () -> Unit) {
    Column(Modifier.height(4.slots)) {
        Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())

        when (ingredient) {
            is IngredientState.SavedIngredientState -> {
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).apply {
                    set(DataComponents.ITEM_NAME, "${ingredient.key}".deser().vanilla())
                }.snapshot())
            }

            is IngredientState.CustomIngredientState -> {
                Slot(
                    value = {
                        ingredient.stacks.getOrNull(0)?.create()?.snapshot() ?: ItemStack.EMPTY.snapshot()
                    },
                    onValueChange = {

                    }
                )
            }
        }

        if (ingredient is IngredientState.CustomIngredientState && (ingredient.stacks.isEmpty() || ingredient.tags.isEmpty())) {
            Icon(stack = ItemStack(Items.ITEM_FRAME).snapshot())
        } else {
            Button(onClick = {}) {
                Icon(stack = ItemStack(Items.GLOW_ITEM_FRAME).snapshot())
            }
        }
        Button(onClick = {}) {
            Icon(stack = ItemStack(Items.BOOKSHELF).snapshot())
        }
    }

}