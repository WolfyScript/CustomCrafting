package com.wolfyscript.customcrafting.ui.editor.recipe_editor

import androidx.compose.runtime.*
import androidx.compose.runtime.key
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import com.wolfyscript.scafall.items.ItemStackRef
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.text.compareTo
import kotlin.text.set

@Composable
fun StackChoicesMenu(
    choices: () -> List<ItemStackRef>,
    onRemove: (Int) -> Unit,
    onAdd: (Int, ItemStackRef) -> Unit,
    onReplace: (Int, ItemStackRef) -> Unit,
) {
    Column(Modifier.fillMaxWidth().height(4.slots)) {
        repeat(3) { row ->
            Row {
                repeat(9) { col ->
                    val index = row * 9 + col
                    Slot(
                        value = { choices().getOrNull(index)?.create()?.snapshot() ?: ItemStack.EMPTY.snapshot() },
                        onValueChange = {
                            val stackRef = ItemStackRef.parse(it.createStack())
                            if (it.isEmpty || stackRef == null) {
                                onRemove(index)
                                return@Slot
                            }
                            if (choices().getOrNull(index)?.create()?.snapshot()?.isEmpty ?: true) {
                                onAdd(index, stackRef)
                            } else {
                                onReplace(index, stackRef)
                            }
                        }
                    )
                }
            }
        }
        Row {
            repeat(9) {
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            }
        }
    }
}

private object CachedTags {

    private var tags: List<HolderSet.Named<Item>> = emptyList()

    fun getTagsSubList(fromIndex: Int, toIndex: Int): List<HolderSet.Named<Item>> {
        if (tags.isEmpty()) {
            tags = BuiltInRegistries.ITEM.tags
                .sorted { holders, holders1 -> holders.key().location.compareTo(holders1.key().location) }
                .toList()
        }
        if (fromIndex > toIndex || toIndex > tags.size) {
            return emptyList()
        }
        return tags.subList(fromIndex, toIndex)
    }

}

private class TagChoicesStore() : Store() {

    val availableTags: StateFlow<TagsState>
        field = MutableStateFlow(TagsState())

    data class TagsState(
        val loading: Boolean = false,
        val tags: List<TagPreview> = emptyList(),
    )

    data class TagPreview(val key: Key, val icon: Item? = null)

    fun setSelectionPage(page: Int) {
        val startIndex = page * 27
        getTagsSubList(startIndex, startIndex + 27)
    }

    private fun getTagsSubList(fromIndex: Int, toIndex: Int) {
        storeCoroutineScope.launch {
            availableTags.update {
                TagsState(loading = true)
            }
            availableTags.update {
                TagsState(
                    tags = CachedTags.getTagsSubList(fromIndex, toIndex)
                        .map { TagPreview(it.key().location.toKey(), icon = it.firstOrNull()?.value()) })
            }
        }
    }

}

@Composable
fun TagChoicesMenu(
    tags: () -> List<Key>,
    onRemove: (Key) -> Unit,
    onAdd: (Key) -> Unit,
) {
    val store = store(Key.Companion.customCrafting("tag_choices")) {
        TagChoicesStore()
    }

    var selectingTag: Boolean by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().height(4.slots)) {
        if (selectingTag) {
            val tagPreviews by store.availableTags.collectAsState()
            TagSelection(
                tagPreviews,
                onAdd = {
                    onAdd(it)
                    selectingTag = false
                },
                onPageChange = { store.setSelectionPage(it) }
            )
        } else {
            var page by remember { mutableStateOf(0) }

            Column(Modifier.fillMaxWidth().height(3.slots)) {
                repeat(3) { row ->
                    Row(Modifier.fillMaxWidth()) {
                        repeat(9) { col ->
                            val index = row * 9 + col
                            tags().getOrNull(index)?.let { key ->
                                Button(onClick = {
                                    onRemove(key)
                                }) {
                                    Icon(stack = ItemStack(Items.NAME_TAG).apply {
                                        set(DataComponents.ITEM_NAME, key.toString().deser().vanilla())
                                    }.snapshot())
                                }
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().height(1.slots), horizontalArrangement = Arrangement.SpaceAround) {
                Button(onClick = {
                    if (page > 0) {
                        page -= 1
                    }
                }) {
                    Icon(stack = Defaults.PreviousPage)
                }

                Button(onClick = {
                    selectingTag = true
                    store.setSelectionPage(0)
                }) {
                    Icon(stack = Defaults.AddNewTag)
                }

                Button(onClick = {
                    if (page < tags().size / 27) {
                        page += 1
                    }
                }) {
                    Icon(stack = Defaults.NextPage)
                }
            }
        }
    }
}

@Composable
private fun TagSelection(
    tagPreviews: TagChoicesStore.TagsState,
    onAdd: (Key) -> Unit,
    onPageChange: (Int) -> Unit,
) {
    var selectorPage by remember { mutableStateOf(0) }
    TagSelectPage(
        tagPreviews,
        onSelect = {
            onAdd(it.key)
            selectorPage = 0
        }
    )
    Row(Modifier.fillMaxWidth().height(1.slots), horizontalArrangement = Arrangement.SpaceAround) {
        Button(onClick = {
            if (selectorPage > 0) {
                selectorPage -= 1
                onPageChange(selectorPage)
            }
        }) {
            Icon(stack = Defaults.PreviousPage)
        }
        Button(onClick = {
            if (tagPreviews.tags.size >= 27) {
                selectorPage += 1
                onPageChange(selectorPage)
            }
        }) {
            Icon(stack = Defaults.NextPage)
        }
    }
}

@Composable
private fun TagSelectPage(
    tagsState: TagChoicesStore.TagsState,
    onSelect: (TagChoicesStore.TagPreview) -> Unit,
) {
    if (tagsState.loading) {
        Column(
            Modifier.fillMaxWidth().height(3.slots),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                Modifier.fillMaxWidth().height(3.slots),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(stack = Defaults.LoadingTagsIcon)
            }
        }
    } else {
        Column(Modifier.fillMaxWidth().height(3.slots)) {
            repeat(3) { row ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(9) { col ->
                        val index = row * 9 + col
                        tagsState.tags.getOrNull(index)?.let { tagPreview ->
                            Button(onClick = {
                                onSelect(tagPreview)
                            }) {
                                Icon(stack = ItemStack(tagPreview.icon ?: Items.STONE).apply {
                                    set(DataComponents.ITEM_NAME, tagPreview.key.toString().deser().vanilla())
                                }.snapshot())
                            }
                        }
                    }
                }
            }
        }
    }
}

private object Defaults {

    val LoadingTagsIcon = ItemStack(Items.BLUE_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "<light_blue>Loading Tags...".deser().vanilla())
    }.snapshot()

    val AddNewTag = ItemStack(Items.BOOKSHELF).apply {
        set(DataComponents.ITEM_NAME, "Add Item Tag".deser().vanilla())
    }.snapshot()

    val NextPage = ItemStack(Items.GREEN_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "Next Page".deser().vanilla())
    }.snapshot()

    val PreviousPage = ItemStack(Items.RED_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "Previous Page".deser().vanilla())
    }.snapshot()

}
