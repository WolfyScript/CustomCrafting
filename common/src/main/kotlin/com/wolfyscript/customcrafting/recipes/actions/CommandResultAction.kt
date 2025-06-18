package com.wolfyscript.customcrafting.recipes.actions

import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.ResultAction
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.utils.unwrap
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec2

class CommandResultAction(
    val commands: List<String>,
) : ResultAction {

    private object CCCommandSource : CommandSource {

        override fun sendSystemMessage(component: Component) { }

        override fun acceptsSuccess(): Boolean = true

        override fun acceptsFailure(): Boolean = true

        override fun shouldInformAdmins(): Boolean = false

    }

    private val defaultName: String = "CustomCrafting@"
    private val defaultCompName = Component.literal(defaultName)

    override fun run(context: EvaluationContext, bulk: Boolean) {
        val player = context.player?.unwrap()
        val location = context.location?.unwrap()
        val (pos, level) = if (location != null) {
            val position = location.first
            val level = ScafallProvider.get().server.minecraftServer.getLevel(location.second)
            position to level
        } else if (player != null) {
            player.position().to(player.level())
        } else {
            return // Requires a level to execute commands either player or location should be specified!
        }
        if (level !is ServerLevel) {
            return // Why are we not on a server?
        }
        val server = level.server

        val srcStack = CommandSourceStack(
            CCCommandSource,
            pos,
            Vec2(0.0f, 0.0f),
            level,
            2,
            defaultName,
            defaultCompName,
            server,
            player
        )

        for (command in commands) {
            server.commands.performPrefixedCommand(srcStack, command)
        }
    }


}