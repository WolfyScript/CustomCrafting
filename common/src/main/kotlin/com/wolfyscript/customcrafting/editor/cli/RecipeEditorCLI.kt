package com.wolfyscript.customcrafting.editor.cli

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.IdentifierArgument
import net.minecraft.network.chat.Component

internal fun LiteralArgumentBuilder<CommandSourceStack>.recipeEditorCLIEntry(dispatcher: CommandDispatcher<CommandSourceStack>) {
    then(
        Commands.literal("create")
            .then(Commands.argument("type", IdentifierArgument.id()).executes { ctx ->
                val executor = ctx.source.player ?: return@executes 0

                val editor = CustomCraftingProvider.get().server?.recipeEditor
                    ?: return@executes SUCCESS_RESULT
                val session = editor.getOrCreateSession(executor.uuid).getOrThrow()

                val typeId = IdentifierArgument.getId(ctx, "type").toKey()
                val recipeType = CustomCraftingRegistryTypes.recipeTypes.resolveOrThrow()[typeId]
                    ?: return@executes SUCCESS_RESULT

                val createResult = session.create(recipeType)

                if (createResult.isSuccess) {
                    ctx.source.sendSuccess({ Component.literal("You are now editing a new $recipeType recipe") }, false)
                    return@executes SUCCESS_RESULT
                }

                ctx.source.sendFailure(Component.literal("Failed to create $recipeType recipe: ${createResult.exceptionOrNull()?.message ?: "Unknown error"}"))
                return@executes 0
            }.suggests { context, builder ->
                for (key in CustomCraftingRegistryTypes.recipeTypes.resolveOrThrow().keySet()) {
                    if (key.namespace == Key.CUSTOMCRAFTING_NAMESPACE) {
                        builder.suggest(key.value)
                    } else {
                        builder.suggest(key.toString())
                    }
                }
                return@suggests builder.buildFuture()
            })
    )
    then(
        Commands.literal("edit")
            .then(Commands.argument("recipe", IdentifierArgument.id()).executes { ctx ->

                return@executes SUCCESS_RESULT
            }.suggests { context, builder ->
                CustomCraftingProvider.get().server?.recipeManager?.let { manager ->
                    for (reference in manager.recipes()) {
                        builder.suggest(reference.toString())
                    }
                }
                return@suggests builder.buildFuture()
            })
    )
}