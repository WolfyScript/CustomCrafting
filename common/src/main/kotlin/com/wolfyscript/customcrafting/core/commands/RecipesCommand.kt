package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object RecipesCommand {

    const val ROOT_NAME = "recipes"

    fun register(customCrafting: CustomCrafting, dispatcher: CommandDispatcher<CommandSourceStack>) {
        sequenceOf(ROOT_NAME, "cc:$ROOT_NAME", "${Key.CUSTOMCRAFTING_NAMESPACE}:$ROOT_NAME").forEach {
            dispatcher.register(
                Commands.literal(it)
                    .then(Commands.literal("reload").executes { reload(customCrafting) })
            )
        }
    }

    private fun reload(customCrafting: CustomCrafting): Int {
        ScafallProvider.get().scheduler.asyncTask(ScafallProvider.get().corePlugin) {
            customCrafting.dataManager.resourceLoader.loadResources()
        }
        return SUCCESS_RESULT
    }

}