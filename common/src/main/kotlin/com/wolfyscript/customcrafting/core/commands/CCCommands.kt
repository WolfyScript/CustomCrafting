package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.customcrafting.CustomCraftingCommon
import net.minecraft.commands.CommandSourceStack

const val SUCCESS_RESULT = 1

class CCCommands(val customCrafting: CustomCraftingCommon) {

    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        MainCommand.register(customCrafting, dispatcher)
        RecipesCommand.register(customCrafting, dispatcher)
    }

}
