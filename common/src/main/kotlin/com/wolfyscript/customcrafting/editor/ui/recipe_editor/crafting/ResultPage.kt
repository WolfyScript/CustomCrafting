package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
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
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

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
                Icon(stack = ResultDefaults.EditChoicesDisabledIcon)
            }
        }

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