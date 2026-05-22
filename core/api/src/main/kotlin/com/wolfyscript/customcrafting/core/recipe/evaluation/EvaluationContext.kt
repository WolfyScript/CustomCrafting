package com.wolfyscript.customcrafting.core.recipe.evaluation

import com.wolfyscript.scafall.wrappers.world.ScafallBlockPos
import com.wolfyscript.scafall.wrappers.world.ScafallGlobalPrecisePos
import com.wolfyscript.scafall.wrappers.world.entity.ScafallPlayer
import com.wolfyscript.scafall.wrappers.world.level.block.entity.ScafallBlockEntity

interface EvaluationContext {

    companion object {

        @JvmStatic
        fun of(player: ScafallPlayer?, location: ScafallGlobalPrecisePos? = null, blockPos: ScafallBlockPos, blockEntity: ScafallBlockEntity? = null): EvaluationContext {
            return EvaluationContextImpl(player, location, blockPos, blockEntity)
        }

        @JvmStatic
        fun of(player: ScafallPlayer?, location: ScafallGlobalPrecisePos?): EvaluationContext {
            return EvaluationContextImpl(player, location)
        }

    }

    val player: ScafallPlayer?

    val location: ScafallGlobalPrecisePos?

    val blockPos: ScafallBlockPos?

    val blockEntity: ScafallBlockEntity?

}