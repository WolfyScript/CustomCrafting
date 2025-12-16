package com.wolfyscript.customcrafting.editor.ui

import androidx.compose.runtime.Composable
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.wolfyscript.customcrafting.core.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

private val recipeEditorUI = Key.customCrafting("recipe_editor_ui")

internal fun LiteralArgumentBuilder<CommandSourceStack>.recipeEditorUIEntry(dispatcher: CommandDispatcher<CommandSourceStack>) {
    then(
        Commands.literal("editor")
            .executes { ctx ->
                val viewportl = ScafallProvider.get().viewportl
                val executor = ctx.source.player ?: return@executes 0
                ScafallProvider.get().scheduler.asyncTask(ScafallProvider.get().modInfo) {
                    viewportl.guiManager.getViewRuntime(executor.uuid).let { playerRuntime ->
                        playerRuntime.joinViewer(executor.uuid)
                        playerRuntime.setNewView(recipeEditorUI) { RecipeEditor() }
                        playerRuntime.openView()
                    }
                }
                return@executes SUCCESS_RESULT
            }
    )
}

@Composable
internal fun RecipeEditor() {
    viewProperties(Key.customCrafting("recipe_editor")) {
        size(9.slots, 4.slots)
        title("<b>Recipe Editor")
    }




}