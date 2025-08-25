package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.wrappers.ScafallBlockEntity
import com.wolfyscript.scafall.wrappers.ScafallPlayer
import com.wolfyscript.scafall.wrappers.world.ScafallBlockPos
import com.wolfyscript.scafall.wrappers.world.ScafallGlobalPrecisePos

class EvaluationContextImpl(
    override val player: ScafallPlayer?,
    override val location: ScafallGlobalPrecisePos? = null,
    override val blockPos: ScafallBlockPos? = null,
    override val blockEntity: ScafallBlockEntity? = null
) : EvaluationContext {

    constructor(player: ScafallPlayer?, location: ScafallGlobalPrecisePos?) : this(player, location, null, null)

}