package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.scafall.wrappers.world.ScafallBlockPos
import com.wolfyscript.scafall.wrappers.world.ScafallGlobalPrecisePos
import com.wolfyscript.scafall.wrappers.world.entity.ScafallPlayer
import com.wolfyscript.scafall.wrappers.world.level.block.entity.ScafallBlockEntity

class EvaluationContextImpl(
    override val player: ScafallPlayer?,
    override val location: ScafallGlobalPrecisePos? = null,
    override val blockPos: ScafallBlockPos? = null,
    override val blockEntity: ScafallBlockEntity? = null
) : EvaluationContext {

    constructor(player: ScafallPlayer?, location: ScafallGlobalPrecisePos?) : this(player, location, null, null)

}