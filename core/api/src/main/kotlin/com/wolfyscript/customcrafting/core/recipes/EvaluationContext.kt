package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.scafall.wrappers.ScafallBlockEntity
import com.wolfyscript.scafall.wrappers.ScafallPlayer
import com.wolfyscript.scafall.wrappers.world.ScafallBlockPos
import com.wolfyscript.scafall.wrappers.world.ScafallGlobalPrecisePos

interface EvaluationContext {

    val player: ScafallPlayer?

    val location: ScafallGlobalPrecisePos?

    val blockPos: ScafallBlockPos?

    val blockEntity: ScafallBlockEntity?

}