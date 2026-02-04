package com.wolfyscript.customcrafting.ui.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.ui.editor.RecipeEditorRoot
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.permissions.Permissions

object RecipesEditorUICommand {

    const val ROOT_NAME = "recipes"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        sequenceOf(ROOT_NAME, "cc:$ROOT_NAME", "${Key.CUSTOMCRAFTING_NAMESPACE}:$ROOT_NAME").forEach { alias ->
            dispatcher.register(
                Commands.literal(alias).requires { it.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) }.apply {
                    recipeEditorUIEntry(dispatcher)
                }
            )
        }
    }

}

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
