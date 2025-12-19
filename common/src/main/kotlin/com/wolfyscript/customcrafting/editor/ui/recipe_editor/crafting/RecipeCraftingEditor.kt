package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxSize
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.gui.elements.Box
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Row

@Composable
fun RecipeCraftingEditor(existingRecipeKey: Key? = null) {
    viewProperties(Key.customCrafting("recipe_editor_crafting")) {
        size(9.slots, 6.slots)
        title("<b>Crafting Recipe Editor</b>: <yellow>${if (existingRecipeKey != null) "editing $existingRecipeKey" else "creating new recipe"}")
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            // Main Content

        }


        Row {
            // Bottom Nav

        }
    }

}