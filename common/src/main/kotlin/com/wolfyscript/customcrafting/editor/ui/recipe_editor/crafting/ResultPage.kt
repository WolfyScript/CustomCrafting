package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.elements.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

@Composable
fun ResultPage() {

    // TODO
    // - Result slot input -> edit first choice of result
    // - Choices -> subpage to edit multiple choices
    // - Tags -> subpage to edit tags
    // - Result Actions Button -> subpage
    // - Result Modifiers Button -> subpage
    //

    Row(Modifier.fillMaxWidth().height(4.slots), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Slot(
                value = { ItemStack.EMPTY.snapshot() },
                onValueChange = {}
            )
            Button(onClick = { }) {
                Icon(stack = ItemStack(Items.ITEM_FRAME).snapshot())
            }
            Button(onClick = { }) {
                Icon(stack = ItemStack(Items.NAME_TAG).snapshot())
            }
        }

        Column(Modifier.height(3.slots), verticalArrangement = Arrangement.SpaceBetween) {
            // Actions
            Button(onClick = { }) {
                Icon(stack = ItemStack(Items.COMMAND_BLOCK).snapshot())
            }
            // Modifiers
            Button(onClick = { }) {
                Icon(stack = ItemStack(Items.CRAFTER).snapshot())
            }
        }
    }

}