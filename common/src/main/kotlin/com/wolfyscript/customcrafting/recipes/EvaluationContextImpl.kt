package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.wrappers.world.ScafallGlobalPrecisePos
import com.wolfyscript.scafall.wrappers.world.entity.Player

class EvaluationContextImpl(
    override val player: Player?,
    override val location: ScafallGlobalPrecisePos?
) : EvaluationContext {


}