package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.customcrafting.CustomCraftingCommon
import net.minecraft.commands.CommandSourceStack

object CCCommands {

    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        MainCommand.register(dispatcher)
        RecipesCommand.register(dispatcher)
    }

}
