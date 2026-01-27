package com.wolfyscript.customcrafting.editor.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.customcrafting.editor.ui.Paths
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.compose.layout.Alignment.CenterVertically
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.NavKey
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.model.store

@Composable
internal fun EditorHome(backstack: SnapshotStateList<NavKey>) {
    val editorStore: EditorHomeStore = store(key = Key.customCrafting("home")) { EditorHomeStore(it) }
    val homeState by editorStore.homeState.collectAsState()

    Row(
        Modifier.fillMaxWidth().height(4.slots),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = CenterVertically
    ) {
        for (recipeType in homeState.recipeTypes) {
            Button(onClick = {
                editorStore.selectRecipeType(recipeType)
                backstack.add(Paths.RecipeEditor(recipeType))
            }) {
                Icon(stack = recipeType.icon)
            }
        }
    }
}
