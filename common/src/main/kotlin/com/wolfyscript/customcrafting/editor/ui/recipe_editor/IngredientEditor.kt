package com.wolfyscript.customcrafting.editor.ui.recipe_editor

import androidx.compose.runtime.*
import com.wolfyscript.customcrafting.editor.domain.usecase.IngredientUseCases
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxHeight
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

private class IngredientEditorStore(
    val ingredientIndex: Int,
    val getIngredientUseCase: IngredientUseCases.GetIngredientByIndexUseCase,
    val setIngredientUseCase: IngredientUseCases.SetIngredientAtUseCase,
    val getStackChoicesUseCases: IngredientUseCases.Choices.Get,
    val setStackChoiceUseCase: IngredientUseCases.Choices.Set,
    val removeStackChoiceUseCase: IngredientUseCases.Choices.Remove,
    val addStackChoiceUseCase: IngredientUseCases.Choices.Add,
) : Store() {

    data class StackChoicesState(val choices: List<ItemStackRef>)

    data class TagChoicesState(val tags: List<Key>)

    val choices: StateFlow<StackChoicesState>
        field = MutableStateFlow(StackChoicesState(getStackChoicesUseCases.get(ingredientIndex)))

    // TODO
    val tags: StateFlow<TagChoicesState>
        field = MutableStateFlow(TagChoicesState(emptyList()))

    fun addStackChoice(stack: ItemStackRef) {
        addStackChoiceUseCase.add(ingredientIndex, stack)
        updateChoices()
    }

    fun removeStackChoiceAt(index: Int) {
        removeStackChoiceUseCase.remove(ingredientIndex, index)
        updateChoices()
    }

    fun setStackChoiceAt(index: Int, stack: ItemStackRef) {
        setStackChoiceUseCase.set(ingredientIndex, index, stack)
        updateChoices()
    }

    fun addTag(tagKey: Key) {
        // TODO
        updateTags()
    }

    fun removeTag(tagKey: Key) {
        // TODO
        updateTags()
    }

    private fun updateChoices() {
        storeCoroutineScope.launch {
            choices.update {
                StackChoicesState(getStackChoicesUseCases.get(ingredientIndex))
            }
        }
    }

    private fun updateTags() {
        // TODO
    }

}

private enum class SubMenu {
    STACK_CHOICES,
    TAG_CHOICES,
    MATCHER,
    CONSUMER
}

@Composable
fun IngredientEditor(
    index: Int,
    onComplete: () -> Unit,
    getIngredientsUseCase: IngredientUseCases.GetIngredientByIndexUseCase,
    setIngredientUseCase: IngredientUseCases.SetIngredientAtUseCase,
) {
    val store = store(Key.customCrafting("ingredient_editor")) {
        IngredientEditorStore(
            index,
            getIngredientsUseCase,
            setIngredientUseCase,
            IngredientUseCases.Choices.Get(getIngredientsUseCase),
            IngredientUseCases.Choices.Set(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Remove(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Add(getIngredientsUseCase, setIngredientUseCase),
        )
    }
    var currentSubMenu: SubMenu? by mutableStateOf(null)
    val choicesState by store.choices.collectAsState()
    val tagsState by store.tags.collectAsState()

    Column(Modifier.height(5.slots)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                if (currentSubMenu != null) {
                    currentSubMenu = null
                } else {
                    onComplete()
                }
            }) {
                Icon(stack = ItemStack(Items.BARRIER).apply {
                    set(
                        DataComponents.ITEM_NAME,
                        "<b><dark_purple>↩ Done".deser().vanilla()
                    )
                }.snapshot())
            }
        }
        when (currentSubMenu) {
            SubMenu.STACK_CHOICES -> {
                StackChoicesMenu(
                    choices = { choicesState.choices },
                    onRemove = { store.removeStackChoiceAt(it) },
                    onAdd = { index, stack -> store.addStackChoice(stack) },
                    onReplace = { index, stack -> store.setStackChoiceAt(index, stack) }
                )
            }

            SubMenu.TAG_CHOICES -> {
                TagChoicesMenu(
                    { tagsState.tags },
                    { store.removeTag(it) },
                    { store.addTag(it) })
            }

            SubMenu.MATCHER -> {
                MatcherMenu()
            }

            SubMenu.CONSUMER -> {
                ConsumerMenu()
            }

            else -> {
                Row(Modifier.fillMaxWidth().height(3.slots), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                        Button(onClick = {
                            currentSubMenu = SubMenu.STACK_CHOICES
                        }) {
                            Icon(stack = IngredientEditorDefaults.EditChoices)
                        }

                        Button(onClick = {
                            currentSubMenu = SubMenu.TAG_CHOICES
                        }) {
                            Icon(stack = IngredientEditorDefaults.EditTags)
                        }
                    }

                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                        Button(onClick = {
                            currentSubMenu = SubMenu.MATCHER
                        }) {
                            Icon(stack = IngredientEditorDefaults.EditMatcher)
                        }

                        Button(onClick = {
                            currentSubMenu = SubMenu.CONSUMER
                        }) {
                            Icon(stack = IngredientEditorDefaults.EditConsumer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChoices(state: IngredientEditorStore.TagChoicesState) {

}

@Composable
private fun MatcherMenu() {

}

@Composable
private fun ConsumerMenu() {

}

private object IngredientEditorDefaults {

    val EditChoices = ItemStack(Items.BOOKSHELF).apply {
        set(DataComponents.ITEM_NAME, "Edit Choices".deser().vanilla())
    }.snapshot()

    val EditTags = ItemStack(Items.NAME_TAG).apply {
        set(DataComponents.ITEM_NAME, "Edit Tags".deser().vanilla())
    }.snapshot()

    val EditMatcher = ItemStack(Items.CRAFTING_TABLE).apply {
        set(DataComponents.ITEM_NAME, "Edit Matcher".deser().vanilla())
    }.snapshot()

    val EditConsumer = ItemStack(Items.HOPPER).apply {
        set(DataComponents.ITEM_NAME, "Edit Consumer".deser().vanilla())
    }.snapshot()

}