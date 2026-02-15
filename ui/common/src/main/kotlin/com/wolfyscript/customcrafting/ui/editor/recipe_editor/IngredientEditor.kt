package com.wolfyscript.customcrafting.ui.editor.recipe_editor

import androidx.compose.runtime.*
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientMatcherModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherExactModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.usecase.IngredientUseCases
import com.wolfyscript.customcrafting.core.util.customCrafting
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
    val getStackChoicesUseCases: IngredientUseCases.Choices.Get,
    val setStackChoiceUseCase: IngredientUseCases.Choices.Set,
    val removeStackChoiceUseCase: IngredientUseCases.Choices.Remove,
    val addStackChoiceUseCase: IngredientUseCases.Choices.Add,
    val getTagChoicesState: IngredientUseCases.Tags.Get,
    val addTagUseCase: IngredientUseCases.Tags.Add,
    val removeTagUseCase: IngredientUseCases.Tags.Remove,
    val getMatcher: IngredientUseCases.Matcher.Get,
    val setMatcher: IngredientUseCases.Matcher.Set,
) : Store() {

    data class StackChoicesState(val choices: List<ItemStackRef>)

    data class TagChoicesState(val tags: List<Key>)

    data class MatcherState(val matcher: IngredientMatcherModel<*> = IngredientMatcherExactModel())

    val choices: StateFlow<StackChoicesState>
        field = MutableStateFlow(StackChoicesState(emptyList()))

    val tags: StateFlow<TagChoicesState>
        field = MutableStateFlow(TagChoicesState(emptyList()))

    val matcher: StateFlow<MatcherState>
        field = MutableStateFlow(MatcherState())

    fun addStackChoice(ingredientIndex: Int, stack: ItemStackRef) {
        addStackChoiceUseCase.add(ingredientIndex, stack)
        fetchChoices(ingredientIndex)
    }

    fun removeStackChoiceAt(ingredientIndex: Int, index: Int) {
        removeStackChoiceUseCase.remove(ingredientIndex, index)
        fetchChoices(ingredientIndex)
    }

    fun setStackChoiceAt(ingredientIndex: Int, index: Int, stack: ItemStackRef) {
        setStackChoiceUseCase.set(ingredientIndex, index, stack)
        fetchChoices(ingredientIndex)
    }

    fun addTag(ingredientIndex: Int, tagKey: Key) {
        addTagUseCase.add(ingredientIndex, tagKey)
        fetchTags(ingredientIndex)
    }

    fun removeTag(ingredientIndex: Int, tagKey: Key) {
        removeTagUseCase.remove(ingredientIndex, tagKey)
        fetchTags(ingredientIndex)
    }

    fun setMatcher(ingredientIndex: Int, matcher: IngredientMatcherModel<*>) {
        setMatcher.set(ingredientIndex, matcher)
        fetchMatcher(ingredientIndex)
    }

    fun fetchMatcher(ingredientIndex: Int) {
        storeCoroutineScope.launch {
            matcher.update {
                getMatcher.get(ingredientIndex)?.let { MatcherState(it) } ?: MatcherState()
            }
        }
    }

    fun fetchChoices(ingredientIndex: Int) {
        storeCoroutineScope.launch {
            choices.update {
                StackChoicesState(getStackChoicesUseCases.get(ingredientIndex))
            }
        }
    }

    fun fetchTags(ingredientIndex: Int) {
        storeCoroutineScope.launch {
            tags.update {
                TagChoicesState(getTagChoicesState.get(ingredientIndex))
            }
        }
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
            IngredientUseCases.Choices.Get(getIngredientsUseCase),
            IngredientUseCases.Choices.Set(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Remove(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Add(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Tags.Get(getIngredientsUseCase),
            IngredientUseCases.Tags.Add(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Tags.Remove(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Matcher.Get(getIngredientsUseCase),
            IngredientUseCases.Matcher.Set(getIngredientsUseCase, setIngredientUseCase),
        )
    }
    var currentSubMenu: SubMenu? by mutableStateOf(null)
    val choicesState by store.choices.collectAsState()
    val tagsState by store.tags.collectAsState()
    val matcher by store.matcher.collectAsState()

    Column(Modifier.fillMaxHeight()) {
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
                    onRemove = { store.removeStackChoiceAt(index, it) },
                    onAdd = { _, stack -> store.addStackChoice(index, stack) },
                    onReplace = { choiceIndex, stack -> store.setStackChoiceAt(index, choiceIndex, stack) }
                )
            }

            SubMenu.TAG_CHOICES -> {
                TagChoicesMenu(
                    { tagsState.tags },
                    { store.removeTag(index, it) },
                    { store.addTag(index, it) })
            }

            SubMenu.MATCHER -> {
                IngredientMatcherMenu(
                    matcher.matcher,
                    {
                        store.setMatcher(index, it.createEmptyModel())
                    },{ matcher ->
                        store.setMatcher(index, matcher)
                    }, {
                        store.setMatcher(index, IngredientMatcherModels.exact.resolveOrThrow().createEmptyModel())
                    })
            }

            SubMenu.CONSUMER -> {
                ConsumerMenu()
            }

            else -> {
                Row(Modifier.fillMaxWidth().height(3.slots), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                        Button(onClick = {
                            currentSubMenu = SubMenu.STACK_CHOICES
                            store.fetchChoices(index)
                        }) {
                            Icon(stack = IngredientEditorDefaults.EditChoices)
                        }

                        Button(onClick = {
                            currentSubMenu = SubMenu.TAG_CHOICES
                            store.fetchTags(index)
                        }) {
                            Icon(stack = IngredientEditorDefaults.EditTags)
                        }
                    }

                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                        Button(onClick = {
                            currentSubMenu = SubMenu.MATCHER
                            store.fetchMatcher(index)
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