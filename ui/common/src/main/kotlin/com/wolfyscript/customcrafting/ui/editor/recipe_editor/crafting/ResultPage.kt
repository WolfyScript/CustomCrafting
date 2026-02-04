package com.wolfyscript.customcrafting.ui.editor.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.usecase.RecipeCraftingUseCases
import com.wolfyscript.customcrafting.editor.domain.usecase.RecipeResultUseCases
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

private class ResultStore(
    private val getResultUseCase: RecipeResultUseCases.GetResultUseCase,
    private val addResultStackChoice: RecipeResultUseCases.Choices.Add,
    private val setResultStackChoice: RecipeResultUseCases.Choices.Set,
    private val removeResultStackChoice: RecipeResultUseCases.Choices.Remove,
) : Store() {

    data class IconState(val icon: ItemStackSnapshot)

    private val initialIconState = IconState(
        getResultUseCase.get().choices.stacks.firstOrNull()?.create()?.snapshot()
            ?: ItemStack.EMPTY.snapshot()
    )

    val icon: StateFlow<IconState>
        field = MutableStateFlow(initialIconState)

    fun setFirstChoice(stack: ItemStackRef) {
        setResultStackChoice.set(0, stack)
        updateIcon()
    }

    fun removeFirstChoice() {
        removeResultStackChoice.remove(0)
        updateIcon()
    }

    fun addFirstChoice(stack: ItemStackRef) {
        addResultStackChoice.add(stack)
        updateIcon()
    }

    fun updateIcon() {
        storeCoroutineScope.launch {
            icon.update {
                IconState(
                    getResultUseCase.get().choices.stacks.firstOrNull()?.create()?.snapshot()
                        ?: ItemStack.EMPTY.snapshot()
                )
            }
        }
    }

}


@Composable
fun ResultPage() {
    // TODO
    // - Choices -> subpage to edit multiple choices
    // - Tags -> subpage to edit tags
    // - Result Actions Button -> subpage
    // - Result Modifiers Button -> subpage
    //
    val store = store(Key.customCrafting("result")) {
        val session = CustomCraftingProvider.get().server!!.recipeEditor.getOrCreateSession(it).getOrThrow()
        val getResult = RecipeCraftingUseCases.Result.Get(session)
        val setResult = RecipeCraftingUseCases.Result.Set(session)
        ResultStore(
            getResult,
            RecipeResultUseCases.Choices.Add(getResult, setResult),
            RecipeResultUseCases.Choices.Set(getResult, setResult),
            RecipeResultUseCases.Choices.Remove(getResult, setResult)
        )
    }

    val previewIcon by store.icon.collectAsState()

    Row(
        Modifier.fillMaxWidth().height(4.slots),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ResultInput(
            value = previewIcon,
            iconGetter = {
                previewIcon.icon
            },
            onModify = { store.updateIcon() },
            onRemove = { store.removeFirstChoice() },
            onAdd = { store.addFirstChoice(it) },
            onReplace = { store.setFirstChoice(it) },
        )

        Column(Modifier.height(3.slots), verticalArrangement = Arrangement.SpaceBetween) {
            // Actions
            Button(onClick = { }) {
                Icon(stack = ResultDefaults.EditActionsIcon)
            }
            // Modifiers
            Button(onClick = { }) {
                Icon(stack = ResultDefaults.EditModifiersIcon)
            }
        }
    }

}

@Composable
private fun ResultInput(
    value: ResultStore.IconState,
    iconGetter: () -> ItemStackSnapshot,
    onModify: (ItemStackSnapshot) -> Unit,
    onRemove: () -> Unit,
    onAdd: (ItemStackRef) -> Unit,
    onReplace: (ItemStackRef) -> Unit,
) {
    Column(Modifier.height(2.slots), verticalArrangement = Arrangement.Top) {
        Slot(
            value = iconGetter,
            onValueChange = {
                if (it.isEmpty) {
                    onRemove()
                    onModify(it)
                    return@Slot
                }
                val stackRef = ItemStackRef.parse(it.createStack())
                if (stackRef != null) {
                    if (value.icon.isEmpty) {
                        onAdd(stackRef)
                    } else {
                        onReplace(stackRef)
                    }
                } else if (!value.icon.isEmpty) {
                    onRemove()
                }
            }
        )
        if (value.icon.isEmpty /* && ingredient.tags.isEmpty()*/) {
            Icon(stack = ResultDefaults.EditChoicesDisabledIcon)
        } else {
            Button(onClick = {}) {
                Icon(stack = ResultDefaults.EditChoicesIcon)
            }
        }
    }
}

private object ResultDefaults {

    val EditChoicesDisabledIcon = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "<grey><st>Edit Choices & Tags".deser().vanilla())
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

    val EditChoicesIcon = ItemStack(Items.GLOW_ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Edit Choices & Tags".deser().vanilla())
    }.snapshot()

    val EditActionsIcon = ItemStack(Items.COMMAND_BLOCK).apply {
        set(DataComponents.ITEM_NAME, "Edit Actions".deser().vanilla())
        set(
            DataComponents.LORE,
            ItemLore(
                listOf(
                    "<!i><white>Actions execute custom behaviour".deser().vanilla(),
                    "<!i><white>upon successfully crafting.".deser().vanilla()
                )
            )
        )
    }.snapshot()

    val EditModifiersIcon = ItemStack(Items.CRAFTER).apply {
        set(DataComponents.ITEM_NAME, "Edit Modifiers".deser().vanilla())
        set(
            DataComponents.LORE,
            ItemLore(
                listOf(
                    "<!i><white>Modifiers transform the".deser().vanilla(),
                    "<!i><white>resulting stack based on".deser().vanilla(),
                    "<!i><white>ingredient data.".deser().vanilla()
                )
            )
        )
    }.snapshot()

}