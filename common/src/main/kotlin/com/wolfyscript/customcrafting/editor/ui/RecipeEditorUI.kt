package com.wolfyscript.customcrafting.editor.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.editor.ui.home.EditorHome
import com.wolfyscript.customcrafting.editor.ui.home.EditorHomeStore
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.RecipeEditor
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.compose.layout.Alignment.CenterVertically
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.store
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

internal fun LiteralArgumentBuilder<CommandSourceStack>.recipeEditorUIEntry(dispatcher: CommandDispatcher<CommandSourceStack>) {
    then(
        Commands.literal("editor")
            .executes { ctx ->
                val viewportl = ScafallProvider.get().viewportl
                val executor = ctx.source.player ?: return@executes 0
                ScafallProvider.get().scheduler.asyncTask(ScafallProvider.get().modInfo) {
                    CustomCraftingProvider.get().server?.recipeEditor?.getOrCreateSession(executor.uuid)
                    viewportl.guiManager.getViewRuntime(executor.uuid).let { playerRuntime ->
                        playerRuntime.joinViewer(executor.uuid)
                        playerRuntime.setContent { RecipeEditorRoot() }
                        playerRuntime.openView()
                    }
                }
                return@executes SUCCESS_RESULT
            }
    )
}

@Composable
internal fun RecipeEditorRoot() {
    viewProperties(Key.customCrafting("recipe_editor")) {
        size(9.slots, 4.slots)
        title("<b>Recipe Editor")
    }

    val backstack = remember { mutableStateListOf<NavKey>(Paths.Home) }

    NavigationRoot(backstack) {
        composable<Paths.Home> {
            EditorHome(backstack)
        }

        composable<Paths.RecipeEditor> {
            RecipeEditor(it.recipeType, backstack)
        }

    }

}
