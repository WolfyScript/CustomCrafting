package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object MainCommand {

    fun register(customCrafting: CustomCrafting, dispatcher: CommandDispatcher<CommandSourceStack>) {
        sequenceOf("cc", Key.CUSTOMCRAFTING_NAMESPACE).forEach {
            dispatcher.register(Commands.literal(it)
                .then(Commands.literal("info").executes {

                    customCrafting.logger.info("Ran CustomCrafting info")
                    // TODO: Display version etc.

                    return@executes SUCCESS_RESULT
                }))
        }
    }

}