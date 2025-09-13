package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object MainCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        sequenceOf("cc", Key.CUSTOMCRAFTING_NAMESPACE).forEach {
            dispatcher.register(Commands.literal(it)
                .then(Commands.literal("info").executes {

                    CustomCraftingProvider.get().logger.info("Ran CustomCrafting info")
                    // TODO: Display version etc.

                    return@executes SUCCESS_RESULT
                })
                .then(Commands.literal("backup").executes { ctx ->
                    val customCrafting = CustomCraftingProvider.get()
                    customCrafting.logger.info("Create backup")
                    customCrafting.resourceManager.backupManager.createBackup()

                    ctx.source.sendSuccess({ Component.literal("Creating backup...") }, false)

                    return@executes SUCCESS_RESULT
                })
            )
        }
    }

}