package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JavaOps
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.SessionModel
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.editor.ui.withEditorSession
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.compose.modifier.width
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.model.LocalView
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.server.dialog.*
import net.minecraft.server.dialog.action.CommandTemplate
import net.minecraft.server.dialog.action.ParsedTemplate
import net.minecraft.server.dialog.input.TextInput
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

private class SavingStore(val uuid: UUID) : Store() {

    data class State(val isSaved: Boolean)

    val state = MutableStateFlow<State>(State(false))

    fun saveAs(key: Key) {
        withEditorSession(uuid) {
            when (val model = it.model) {
                is SessionModel.EditModel -> {
                    model.saveAs(key)
                }

                is SessionModel.CreateModel -> {
                    model.save(key)
                }
            }
        }
    }

    fun save() {
        withEditorSession(uuid) {
            if (it.model is SessionModel.EditModel) {
                (it.model as SessionModel.EditModel).save()
            }
        }
    }

    fun openSaveCraftingRecipeDialog() {
        val dialog = ConfirmationDialog(
            CommonDialogData(
                "Save Crafting Recipe".deser().vanilla(),
                Optional.empty(),
                true,
                false,
                DialogAction.CLOSE,
                listOf(),
                listOf(
                    Input(
                        "recipe_name",
                        TextInput(
                            200,
                            "Type in the name of the recipe.".deser().vanilla(),
                            true,
                            "",
                            128,
                            Optional.empty()
                        )
                    )
                )
            ),
            ActionButton(
                CommonButtonData(
                    "Save".deser().vanilla(),
                    Optional.of("This currently uses a command to save the recipe.".deser().vanilla()),
                    150
                ),
                Optional.of(
                    CommandTemplate(
                        ParsedTemplate.CODEC.parse(Dynamic(JavaOps.INSTANCE, "recipes editor save $(recipe_name)")).orThrow
                    )
                )
            ),
            ActionButton(
                CommonButtonData(
                    "Cancel".deser().vanilla(),
                    150
                ),
                Optional.empty(),
            )
        )

        ScafallProvider.get().server?.minecraftServer?.playerList?.getPlayer(uuid)?.let { player ->
            player.openDialog(Holder.direct(dialog))
        }

    }

}

@Composable
fun SavingPage() {
    val session =
        CustomCraftingProvider.get().server!!.recipeEditor.getOrCreateSession(LocalView.current.viewer).getOrThrow()

    val store = store(Key.customCrafting("saving")) {
        SavingStore(it)
    }

    // TODO
    // 'Save' Button
    // 'Save as' Button
    //

    Row(
        Modifier.fillMaxWidth().height(4.slots),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.width(3.slots), horizontalArrangement = Arrangement.SpaceAround) {

            if (session.model is SessionModel.EditModel) {
                // Save
                Button(onClick = {
                    store.save()
                }) {
                    Icon(stack = SavingPageDefaults.SaveIcon)
                }
            }

            // Save as...
            Button(onClick = {
                // TODO: Save as
                store.openSaveCraftingRecipeDialog()
            }) {
                Icon(stack = SavingPageDefaults.SaveAsIcon)
            }
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