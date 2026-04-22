package com.wolfyscript.customcrafting.ui.editor.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.CustomIngredientModelImpl
import com.wolfyscript.customcrafting.editor.domain.usecase.IngredientUseCases
import com.wolfyscript.customcrafting.editor.domain.usecase.RecipeCraftingUseCases
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.IngredientEditor
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.state.UIIngredientPreview
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.state.toUIState
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
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
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import java.util.*

class AddIngredientStore(
    val viewer: UUID,
    val getIngredientsUseCase: RecipeCraftingUseCases.IngredientCollection.Get,
    val setIngredientsUseCase: RecipeCraftingUseCases.IngredientCollection.Set,
    val addIngredientUseCase: RecipeCraftingUseCases.IngredientCollection.Add,
    val removeIngredientUseCase: RecipeCraftingUseCases.IngredientCollection.RemoveIngredientUseCase,
    val setStackChoiceUseCase: IngredientUseCases.Choices.Set,
    val removeStackChoiceUseCase: IngredientUseCases.Choices.Remove,
    val addStackChoiceUseCase: IngredientUseCases.Choices.Add,
) : Store() {

    data class State(
        val previews: List<UIIngredientPreview>,
    )

    val ingredientCollection: StateFlow<State>
        field = MutableStateFlow(State(getIngredientsUseCase.getCollection().toUIState()))

    val selectedIngredient: StateFlow<Int?>
        field = MutableStateFlow(null)

    fun addIngredient() {
        addIngredientUseCase.add(CustomIngredientModelImpl())
        updateIngredients()
    }

    fun editIngredient(index: Int?) {
        storeCoroutineScope.launch {
            selectedIngredient.update { index }
        }
    }

    fun removeIngredient(index: Int) {
        removeIngredientUseCase.remove(index)
        updateIngredients()
    }

    fun addFirstStackChoice(ingredientIndex: Int, stack: ItemStackRef) {
        addStackChoiceUseCase.add(ingredientIndex, stack)
        updateIngredients()
    }

    fun removeFirstStackChoiceFor(ingredientIndex: Int) {
        removeStackChoiceUseCase.remove(ingredientIndex, 0)
        updateIngredients()
    }

    fun setFirstStackChoiceFor(ingredientIndex: Int, stack: ItemStackRef) {
        setStackChoiceUseCase.set(ingredientIndex, 0, stack)
        updateIngredients()
    }

    @Deprecated("Temporary! updating should be moved to the yet to be implemented domain repository")
    private fun updateIngredients() {
        storeCoroutineScope.launch {
            ingredientCollection.update {
                State(getIngredientsUseCase.getCollection().toUIState())
            }
        }
    }

}

@Composable
fun AddIngredientsPage() {
    val store = store(key = Key.customCrafting("ingredient_collection")) {
        val session = CustomCraftingProvider.get().server!!.recipeEditor.getOrCreateSession(it).getOrThrow()
        val getIngredientCollectionUseCase = RecipeCraftingUseCases.IngredientCollection.Get(session)
        val getIngredientUseCase = IngredientUseCases.GetIngredientUseCase(getIngredientCollectionUseCase)
        val setIngredientUseCase =
            RecipeCraftingUseCases.IngredientCollection.Set(session, getIngredientCollectionUseCase)

        AddIngredientStore(
            it,
            getIngredientCollectionUseCase,
            setIngredientUseCase,
            RecipeCraftingUseCases.IngredientCollection.Add(session, getIngredientCollectionUseCase),
            RecipeCraftingUseCases.IngredientCollection.RemoveIngredientUseCase(session),
            IngredientUseCases.Choices.Set(getIngredientUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Remove(getIngredientUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Add(getIngredientUseCase, setIngredientUseCase),
        )
    }
    val collection by store.ingredientCollection.collectAsState()
    val selectedIngredientIndex by store.selectedIngredient.collectAsState()

    Column(Modifier.fillMaxHeight()) {
        if (selectedIngredientIndex != null) {
            IngredientEditor(
                selectedIngredientIndex!!,
                { store.editIngredient(null) },
                IngredientUseCases.GetIngredientUseCase(store.getIngredientsUseCase),
                store.setIngredientsUseCase
            )
        } else {
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
                                    onEdit = {
                                        store.editIngredient(index)
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
                            Icon(stack = AddIngredientDefaults.AddNewIngredientIcon)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomIngredientSelector(
    ingredient: UIIngredientPreview.Custom,
    onRemove: () -> Unit,
    onAdd: (ItemStackRef) -> Unit,
    onReplace: (ItemStackRef) -> Unit,
    onEdit: () -> Unit
) {
    Column(Modifier.height(3.slots), verticalArrangement = Arrangement.Top) {
        Slot(
            value = {
                ingredient.icon
            },
            onValueChange = {
                val stackRef = ItemStackRef.parse(it.createStack())
                if (it.isEmpty || stackRef == null) {
                    onRemove()
                    return@Slot
                }
                if (ingredient.icon.isEmpty) {
                    onAdd(stackRef)
                } else {
                    onReplace(stackRef)
                }
            }
        )
        if (ingredient.icon.isEmpty /* && ingredient.tags.isEmpty()*/) {
            Icon(stack = AddIngredientDefaults.EditIngredientDisabledIcon)
        } else {
            Button(onClick = { onEdit() }) {
                Icon(stack = AddIngredientDefaults.EditIngredientIcon)
            }
        }
        Button(onClick = {}) {
            Icon(stack = AddIngredientDefaults.SelectSavedIngredientIcon)
        }
    }
}

private object AddIngredientDefaults {

    val AddNewIngredientIcon = ItemStack(Items.GREEN_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "Add Ingredient".deser().vanilla())
    }.snapshot()

    val EditIngredientDisabledIcon = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "<grey><st>Edit Ingredient".deser().vanilla())
        set(
            DataComponents.LORE,
            ItemLore(
                listOf(
                    "<!i><white>^ Place the first item ^".deser().vanilla(),
                    "<!i><white>^ into the slot above! ^".deser().vanilla()
                )
            )
        )
    }.snapshot()

    val EditIngredientIcon = ItemStack(Items.GLOW_ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "<yellow>Edit Ingredient".deser().vanilla())
        set(
            DataComponents.LORE,
            ItemLore(
                listOf(
                    "<!i><white>Edit this custom Ingredients'".deser().vanilla(),
                    "<!i><white>Choices, Tags & more".deser().vanilla(),
                )
            )
        )
    }.snapshot()

    val SelectSavedIngredientIcon = ItemStack(Items.BOOKSHELF).apply {
        set(DataComponents.ITEM_NAME, "<yellow>Select Ingredient".deser().vanilla())
        set(
            DataComponents.LORE,
            ItemLore(
                listOf(
                    "<!i><white>Select an existing ingredient".deser().vanilla(),
                    "<!i><white>that you've saved.".deser().vanilla()
                )
            )
        )
    }.snapshot()
}


