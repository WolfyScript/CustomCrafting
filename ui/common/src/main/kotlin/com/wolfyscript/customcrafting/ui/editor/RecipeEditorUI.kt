package com.wolfyscript.customcrafting.ui.editor

import androidx.compose.runtime.*
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.ui.editor.home.EditorHome
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.RecipeEditor
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.gui.elements.*
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
    viewProperties(Key.Companion.customCrafting("recipe_editor")) {
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
