package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.scafall.wrappers.world.ScafallBlockPos
import com.wolfyscript.scafall.wrappers.world.ScafallGlobalPrecisePos
import com.wolfyscript.scafall.wrappers.world.entity.ScafallPlayer
import com.wolfyscript.scafall.wrappers.world.level.block.entity.ScafallBlockEntity

interface EvaluationContext {

    val player: ScafallPlayer?

    val location: ScafallGlobalPrecisePos?

    val blockPos: ScafallBlockPos?

    val blockEntity: ScafallBlockEntity?

}