package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.model.CustomIngredientModelImpl
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.usecase.IngredientUseCases
import com.wolfyscript.customcrafting.editor.domain.usecase.RecipeCraftingUseCases
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.state.UIIngredientPreview
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.state.toUIState
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
import com.wolfyscript.viewportl.gui.model.LocalView
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

class AddIngredientStore(
    val viewer: UUID,
    val getIngredientsUseCase: RecipeCraftingUseCases.IngredientCollection.GetUseCase,
    val addIngredientUseCase: RecipeCraftingUseCases.IngredientCollection.AddIngredientUseCase,
    val removeIngredientUseCase: RecipeCraftingUseCases.IngredientCollection.RemoveIngredientUseCase,
    val setStackChoiceUseCase: IngredientUseCases.SetStackChoiceUseCase,
    val removeStackChoiceUseCase: IngredientUseCases.RemoveStackChoiceUseCase,
    val addStackChoiceUseCase: IngredientUseCases.AddStackChoiceUseCase,
) : Store() {

    data class State(
        val previews: List<UIIngredientPreview>,
    )

    val ingredientCollection: StateFlow<State> = MutableStateFlow(State(getIngredientsUseCase.getCollection().toUIState()))

    fun addIngredient() {
        addIngredientUseCase.add(CustomIngredientModelImpl())
        updateIngredients()
    }

    fun addIngredient(ingredientModel: IngredientModel) {
        addIngredientUseCase.add(ingredientModel)
        updateIngredients()
    }

    fun removeIngredient(index: Int) {
        removeIngredientUseCase.remove(index)
        updateIngredients()
    }

    fun addFirstStackChoice(ingredientIndex: Int, stack: ItemStackRef) {
        addStackChoiceUseCase.add(ingredientIndex, stack)
    }

    fun removeFirstStackChoiceFor(ingredientIndex: Int) {
        removeStackChoiceUseCase.remove(ingredientIndex, 0)
    }

    fun setFirstStackChoiceFor(ingredientIndex: Int, stack: ItemStackRef) {
        setStackChoiceUseCase.set(ingredientIndex, 0, stack)
    }

    @Deprecated("Temporary! updating should be moved to the yet to be implemented domain repository")
    fun updateIngredients() {
        storeCoroutineScope.launch {
            (ingredientCollection as MutableStateFlow).update {
                State(getIngredientsUseCase.getCollection().toUIState())
            }
        }
    }

}

@Composable
fun AddIngredientsPage() {
    val store = store(key = Key.customCrafting("ingredient_collection")) {
        val session = CustomCraftingProvider.get().server!!.recipeEditor.getOrCreateSession(it).getOrThrow()
        val getIngredientCollectionUseCase = RecipeCraftingUseCases.IngredientCollection.GetUseCase(session)
        val getIngredientUseCase = IngredientUseCases.GetIngredientUseCase(getIngredientCollectionUseCase)
        val setIngredientUseCase = RecipeCraftingUseCases.IngredientCollection.SetIngredientUseCase(session, getIngredientCollectionUseCase)

        AddIngredientStore(
            it,
            getIngredientCollectionUseCase,
            RecipeCraftingUseCases.IngredientCollection.AddIngredientUseCase(session, getIngredientCollectionUseCase),
            RecipeCraftingUseCases.IngredientCollection.RemoveIngredientUseCase(session),
            IngredientUseCases.SetStackChoiceUseCase(getIngredientUseCase, setIngredientUseCase),
            IngredientUseCases.RemoveStackChoiceUseCase(getIngredientUseCase, setIngredientUseCase),
            IngredientUseCases.AddStackChoiceUseCase(getIngredientUseCase, setIngredientUseCase),
        )
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
                                onAdd = { store.addFirstStackChoice(index, it) },
                                onRemove = { store.removeFirstStackChoiceFor(index) },
                                onReplace = { store.setFirstStackChoiceFor(index, it) },
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