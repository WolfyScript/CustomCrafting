package com.wolfyscript.customcrafting.editor.cli.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.cli.recipeEditorCLIEntry
import com.wolfyscript.customcrafting.editor.domain.SessionModel
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.IdentifierArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions

object RecipesEditorCommand {

    const val ROOT_NAME = "recipes"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        sequenceOf(ROOT_NAME, "cc:$ROOT_NAME", "${Key.CUSTOMCRAFTING_NAMESPACE}:$ROOT_NAME").forEach { alias ->
            dispatcher.register(
                Commands.literal(alias).requires { it.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) }.apply {
                    recipeEditorCLIEntry(dispatcher)
                }
            )
        }
    }

}