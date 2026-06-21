package com.wolfyscript.customcrafting.ui.editor.recipe_editor

import androidx.compose.runtime.*
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.ui.UIRegistryTypes
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherContext
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherCustomUIProvider
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.*
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

private class IngredientMatcherStore : Store() {

    data class AvailableMatchers(
        val loading: Boolean = false,
        val matchers: List<EditorModelFactory<out IngredientMatcherModel<*>>> = emptyList(),
    )

    val availableMatchers: StateFlow<AvailableMatchers>
        field = MutableStateFlow(AvailableMatchers())

    fun setSelectionPage(page: Int) {
        val startIndex = page * 20
        getAvailableMatchersList(startIndex, startIndex + 20)
    }

    private fun getAvailableMatchersList(fromIndex: Int, toIndex: Int) {
        storeCoroutineScope.launch {
            availableMatchers.update {
                AvailableMatchers(loading = true)
            }
            availableMatchers.update {
                val matchers = try {
                    val all = EditorRegistryTypes.ingredientMatchers.resolveOrThrow()
                        .values().toList()
                    all.subList(fromIndex, toIndex.coerceAtMost(all.size))
                } catch (e: Exception) {
                    ScafallProvider.get().logger.error("Error while fetching custom ingredient-matcher models", e)
                    emptyList()
                }

                AvailableMatchers(
                    loading = false,
                    matchers = matchers
                )
            }
        }
    }

}

@Composable
fun <M : IngredientMatcherModel<*>> IngredientMatcherMenu(
    matcher: M,
    onSelect: (EditorModelFactory<out IngredientMatcherModel<*>>) -> Unit,
    onModify: (matcher: M) -> Unit,
    onReset: (EditorModelFactory<out IngredientMatcherModel<*>>) -> Unit,
) {
    val store = store(Key.customCrafting("ingredient_matchers")) {
        IngredientMatcherStore()
    }
    var selectingType: Boolean by remember { mutableStateOf(false) }

    Column {
        Row(Modifier.fillMaxWidth().weight(1f)) {
            Column(Modifier.width(3.slots), horizontalAlignment = Alignment.CenterHorizontally) {
                // current type
                Icon(stack = ItemStack(Items.COMMAND_BLOCK).apply {
                    set(DataComponents.ITEM_NAME, "Selected:".deser().vanilla())
                    set(
                        DataComponents.LORE, ItemLore(
                            listOf(
                                "<!i><yellow>${matcher.typeKey}".deser().vanilla()
                            )
                        )
                    )
                }.snapshot())

                if (selectingType) {
                    Button(onClick = {
                        selectingType = false
                    }) {
                        Icon(stack = MatcherMenuDefaults.EditCurrentMatcher)
                    }
                } else {
                    Button(onClick = {
                        selectingType = true
                        store.setSelectionPage(0)
                    }) {
                        Icon(stack = MatcherMenuDefaults.SelectOtherType)
                    }
                }

            }

            if (selectingType) {
                val matchers by store.availableMatchers.collectAsState()
                SelectMatcherType(
                    matchers = matchers,
                    onSelect = {
                        selectingType = false
                        onSelect(it)
                    },
                    onPageChange = {
                        store.setSelectionPage(it)
                    })
            } else {
                // Custom UI of Matcher
                Box(Modifier.fillMaxHeight().weight(1f)) {
                    val ui = try {
                        UIRegistryTypes.ingredientMatchers.resolveOrThrow()[matcher.typeKey]
                    } catch (e: Exception) {
                        ScafallProvider.get().logger.error("Failed to render ui for matcher $matcher", e)
                        null
                    }
                    if (ui != null && ui.modelType.isInstance(matcher)) {
                        val castUI: IngredientMatcherCustomUIProvider<M>? = try {
                            ui as IngredientMatcherCustomUIProvider<M>
                        } catch (e: Exception) {
                            ScafallProvider.get().logger.error("Failed to render ui for matcher $matcher", e)
                            null
                        }
                        if (castUI != null) {
                            val context = IngredientMatcherContext(
                                updateMatcher = onModify
                            )
                            with(castUI) {
                                context.render(matcher)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectMatcherType(
    matchers: IngredientMatcherStore.AvailableMatchers,
    onSelect: (EditorModelFactory<out IngredientMatcherModel<*>>) -> Unit,
    onPageChange: (Int) -> Unit,
) {
    var page: Int by remember { mutableStateOf(0) }
    Row(Modifier.height(4.slots).width(6.slots)) {
        SelectMatcherTypePage(matchers, onSelect)
        Column(Modifier.height(4.slots), verticalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                if (page > 0) {
                    page--
                    onPageChange(page)
                }
            }) {
                Icon(stack = ItemStack(Items.CONCRETE.cyan).snapshot())
            }
            Button(onClick = {
                page++
                onPageChange(page)
            }) {
                Icon(stack = ItemStack(Items.CONCRETE.cyan).snapshot())
            }
        }
    }
}

@Composable
private fun SelectMatcherTypePage(
    matchers: IngredientMatcherStore.AvailableMatchers,
    onSelect: (EditorModelFactory<out IngredientMatcherModel<*>>) -> Unit,
) {
    if (matchers.loading) {
        Box(Modifier.width(5.slots).height(4.slots), contentAlignment = Alignment.Center) {
            Icon(stack = ItemStack(Items.CONCRETE.cyan).snapshot())
        }
    } else {
        Column(Modifier.width(5.slots).height(4.slots)) {
            repeat(4) { row ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(5) { col ->
                        val index = row * 5 + col
                        matchers.matchers.getOrNull(index)?.let { matcherUI ->
                            Button(onClick = {
                                onSelect(matcherUI)
                            }) {
                                Icon(stack = ItemStack(Items.COMMAND_BLOCK).apply {
                                    set(
                                        DataComponents.ITEM_NAME,
                                        EditorRegistryTypes.ingredientMatchers.resolveOrThrow()
                                            .getKey(matcherUI).toString().deser().vanilla()
                                    )
                                }.snapshot())
                            }
                        }
                    }
                }
            }
        }
    }
}

private object MatcherMenuDefaults {

    val SelectOtherType = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Select Other Type".deser().vanilla())
    }.snapshot()

    val EditCurrentMatcher = ItemStack(Items.GLOW_ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Cancel Selection".deser().vanilla())
        set(
            DataComponents.LORE, ItemLore(
                listOf(
                    "<!i><grey>(Edit Selected)".deser().vanilla(),
                )
            )
        )
    }.snapshot()

}