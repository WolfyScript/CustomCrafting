package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.customcrafting.CustomCraftingCommon
import net.minecraft.commands.CommandSourceStack

const val SUCCESS_RESULT = 1
const val OWNER_LVL = 4
const val ADMIN_LVL = 3
const val GAME_MASTER_LVL = 2
const val MODERATOR_LVL = 1
const val ALL_LVL = 0

class CCCommands(val customCrafting: CustomCraftingCommon) {

    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        MainCommand.register(customCrafting, dispatcher)
        RecipesCommand.register(customCrafting, dispatcher)
    }

}
