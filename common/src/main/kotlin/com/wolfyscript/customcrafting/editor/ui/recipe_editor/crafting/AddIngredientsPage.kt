package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.model.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.model.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting.AddIngredientStore.State
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting.AddIngredientStore.UIIngredientPreview
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxHeight
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.jetbrains.exposed.v1.core.Column
import java.util.*

private fun RecipeCraftingModel.uiState(): State {
    return State(ingredientCollection, ingredientCollection.uiState())
}

private fun RecipeCraftingModel.IngredientCollectionModel.uiState(): List<UIIngredientPreview> {
    return ingredients.mapNotNull { ingredientModel ->
        when (ingredientModel) {
            is IngredientModel.CustomIngredientModel -> UIIngredientPreview.Custom(
                ingredientModel,
                ingredientModel.stacks.firstOrNull()?.create()?.snapshot() ?: ItemStack.EMPTY.snapshot(),
                ingredientModel.replaceWithRemains
            )

            is IngredientModel.SavedIngredientModel -> UIIngredientPreview.Saved(
                ingredientModel,
                ingredientModel.key
            )

            else -> null
        }
    }
}

private class AddIngredientStore(val viewer: UUID) : Store() {

    data class State(
        val origin: RecipeCraftingModel.IngredientCollectionModel,
        val previews: List<UIIngredientPreview>,
    )

    interface UIIngredientPreview {

        val origin: IngredientModel

        data class Custom(
            override val origin: IngredientModel.CustomIngredientModel,
            val icon: ItemStackSnapshot,
            val replaceWithRemains: Boolean,
        ) : UIIngredientPreview

        data class Saved(
            override val origin: IngredientModel.SavedIngredientModel,
            val key: Key,
        ) : UIIngredientPreview

    }

    val ingredientCollection: StateFlow<State> = MutableStateFlow(forCraftingState { it.uiState() })

    private fun <T> forCraftingState(fn: (RecipeCraftingModel) -> T): T {
        val session = CustomCraftingProvider.get().server?.recipeEditor?.getSession(viewer)
        if (session != null) {
            val state = session.state?.recipeModel?.recipeTypeSpecificModel as? RecipeCraftingModel
                ?: error("Expected RecipeCraftingState, but was ${session.state?.recipeModel?.recipeTypeSpecificModel}")
            return fn(state)
        }
        error("Failed to fetch data from session: Session not available")
    }

    fun addIngredient() {
        forCraftingState { state ->
            state.ingredientCollection.addNew()
        }
        updateIngredients()
    }

    fun addIngredient(ingredientModel: IngredientModel) {
        forCraftingState { state ->
            state.ingredientCollection.add(ingredientModel)
        }
        updateIngredients()
    }

    fun removeIngredient(index: Int) {
        forCraftingState { state ->
            state.ingredientCollection.remove(index)
        }
        updateIngredients()
    }

    fun updateIngredients() {
        storeCoroutineScope.launch {
            (ingredientCollection as MutableStateFlow).update {
                forCraftingState { it.uiState() }
            }
        }
    }

}

@Composable
fun AddIngredientsPage() {
    val store = store(key = Key.customCrafting("ingredient_collection")) {
        AddIngredientStore(it)
    }
    val collection by store.ingredientCollection.collectAsState()

    Column(Modifier.fillMaxHeight()) {
        Row(Modifier.fillMaxWidth()) {
            repeat(9) {
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            }
        }

        Row(Modifier.fillMaxWidth().height(3.slots), verticalAlignment = Alignment.Top) {
            collection.previews.forEachIndexed { index, ingredient ->
                key(ingredient) {
                    when (ingredient) {
                        is UIIngredientPreview.Saved -> {
//                        Icon(stack = ingredient.icon)
                        }

                        is UIIngredientPreview.Custom -> {
                            CustomIngredientSelector(
                                ingredient,
                                onAdd = {
                                    ingredient.origin.stacks.add(it)
                                },
                                onRemove = {
                                    ingredient.origin.stacks.removeAt(0)
                                },
                                onReplace = {
                                    ingredient.origin.stacks[0] = it
                                },
                                onModify = {
                                    store.updateIngredients() // Refresh entire list TODO: look for a better solution
                                })
                        }
                    }
                }
            }
            if (collection.previews.size < 9) {
                Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Button(onClick = {
                        store.addIngredient()
                    }) {
                        Icon(stack = ItemStack(Items.GREEN_CONCRETE).snapshot())
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomIngredientSelector(
    ingredient: UIIngredientPreview.Custom,
    onModify: (ItemStackSnapshot) -> Unit,
    onRemove: () -> Unit,
    onAdd: (ItemStackRef) -> Unit,
    onReplace: (ItemStackRef) -> Unit,
) {
    Column(Modifier.height(3.slots), verticalArrangement = Arrangement.Top) {
        Slot(
            value = {
                ingredient.icon
            },
            onValueChange = {
                if (it.isEmpty) {
                    onRemove()
                    onModify(it)
                    return@Slot
                }
                val stackRef = ItemStackRef.parse(it.createStack())
                if (stackRef != null) {
                    if (ingredient.icon.isEmpty) {
                        onAdd(stackRef)
                    } else {
                        onReplace(stackRef)
                    }
                } else if (!ingredient.icon.isEmpty) {
                    onRemove()
                }
                onModify(it)
            }
        )
        if (ingredient.icon.isEmpty /* && ingredient.tags.isEmpty()*/) {
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