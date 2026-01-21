package com.wolfyscript.customcrafting.editor.ui.recipe_editor

import androidx.compose.runtime.Composable
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
import com.wolfyscript.viewportl.gui.compose.modifier.width
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

private class IngredientEditorStore(
    val ingredientIndex: Int,
    val getIngredientUseCase: IngredientUseCases.GetIngredientByIndexUseCase,
    val setIngredientUseCase: IngredientUseCases.SetIngredientAtUseCase,
    val setStackChoiceUseCase: IngredientUseCases.Choices.Set,
    val removeStackChoiceUseCase: IngredientUseCases.Choices.Remove,
    val addStackChoiceUseCase: IngredientUseCases.Choices.Add,
) : Store() {

    fun addStackChoice(stack: ItemStackRef) {
        addStackChoiceUseCase.add(ingredientIndex, stack)
    }

    fun removeStackChoiceAt(index: Int) {
        removeStackChoiceUseCase.remove(ingredientIndex, index)
    }

    fun setStackChoiceAt(index: Int, stack: ItemStackRef) {
        setStackChoiceUseCase.set(ingredientIndex, index, stack)
    }

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
            IngredientUseCases.Choices.Set(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Remove(getIngredientsUseCase, setIngredientUseCase),
            IngredientUseCases.Choices.Add(getIngredientsUseCase, setIngredientUseCase),
        )
    }

    Column(Modifier.height(5.slots)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { onComplete() }) {
                Icon(stack = ItemStack(Items.BARRIER).apply { set(DataComponents.ITEM_NAME, "<b><red>Done".deser().vanilla()) }.snapshot())
            }
        }
        Row(Modifier.fillMaxWidth().height(3.slots), horizontalArrangement = Arrangement.SpaceAround) {
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                Button(onClick = {}) {
                    Icon(stack = IngredientEditorDefaults.EditChoices)
                }

                Button(onClick = {}) {
                    Icon(stack = IngredientEditorDefaults.EditTags)
                }
            }

            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
                Button(onClick = {}) {
                    Icon(stack = IngredientEditorDefaults.EditMatcher)
                }

                Button(onClick = {}) {
                    Icon(stack = IngredientEditorDefaults.EditConsumer)
                }
            }
        }
    }

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