package com.wolfyscript.customcrafting.core.recipe.action

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.permissions.LevelBasedPermissionSet
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

/**
 * Represents a result action that executes a list of commands.
 *
 * @property commands The list of commands to be executed.
 */
internal class CommandResultAction(
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
        val blockEntity = context.blockEntity?.unwrap()

        val pos = if (player != null) {
            player.position()
        } else if (context.blockPos != null) {
            Vec3.atCenterOf(context.blockPos!!.unwrap())
        } else return

        val level = player?.level() ?: blockEntity?.level ?: return

        if (level !is ServerLevel) {
            return // Why are we not on a server?
        }
        val server = level.server

        val srcStack = CommandSourceStack(
            CCCommandSource,
            pos,
            Vec2(0.0f, 0.0f),
            level,
            LevelBasedPermissionSet.GAMEMASTER,
            defaultName,
            defaultCompName,
            server,
            player
        )

        for (command in commands) {
            server.commands.performPrefixedCommand(srcStack, command)
        }
    }

    override fun toString(): String {
        return "commands $commands"
    }

}