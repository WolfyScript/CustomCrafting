package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

@Composable
fun SavingPage() {

    // TODO
    // 'Save' Button
    // 'Save as' Button
    //

    Row(Modifier.fillMaxWidth().height(4.slots), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
        // Save
        Button(onClick = { }) {
            Icon(stack = SavingPageDefaults.SaveIcon)
        }

        // Save as...
        Button(onClick = { }) {
            Icon(stack = SavingPageDefaults.SaveAsIcon)
        }
    }

}

private object SavingPageDefaults {

    val SaveIcon = ItemStack(Items.WRITTEN_BOOK).apply {
        set(DataComponents.ITEM_NAME, "Save".deser().vanilla())
    }.snapshot()

    val SaveAsIcon = ItemStack(Items.WRITABLE_BOOK).apply {
        set(DataComponents.ITEM_NAME, "Save as...".deser().vanilla())
    }.snapshot()
}